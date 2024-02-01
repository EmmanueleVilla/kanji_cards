import json
import os

import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
import tensorflow as tf
from PIL import Image

from sklearn.metrics import accuracy_score, precision_score, recall_score
from sklearn.model_selection import train_test_split
from tensorflow.keras import layers, losses
from tensorflow.keras.datasets import fashion_mnist
from tensorflow.keras.models import Model

encoder: Model

if os.path.exists("encoder"):
    print("Loading model")
    encoder = tf.keras.models.load_model("encoder")
    print("Model loaded")
    print(encoder.summary())

x = []

for folder in os.listdir("kanji"):
    vectors = []
    for file in os.listdir("kanji/" + folder):
        img = Image.open("kanji/" + folder + "/" + file).convert('L')
        img_array = np.array(img)
        img_array = img_array / 255.0
        x.append(img_array)
        if encoder is not None:
            result = encoder.predict(np.array([img_array]))
            vectors.append(result.tolist())
    # save the vectors as json in a file named after the folder
    if encoder is not None:
        with open("kanji_vectors/" + folder + ".json", "w") as f:
            f.write(json.dumps(vectors))


if encoder is not None:
    exit(1)

x_array = np.array(x)

print(x_array.shape)

x_train, x_test = train_test_split(x_array, test_size=0.2)

print(x_train.shape)
print(x_test.shape)

latent_dim = 256


class Autoencoder(Model):
    def __init__(self, latent_dim):
        super(Autoencoder, self).__init__()
        self.latent_dim = latent_dim
        self.encoder = tf.keras.Sequential([
            layers.InputLayer(input_shape=(128, 128, 1)),
            layers.Lambda(lambda x: tf.where(x > 1, tf.cast(x, tf.float32) / 255.0, x)),
            layers.Conv2D(32, (3, 3), activation='relu', padding='same'),
            layers.MaxPooling2D((2, 2), padding='same'),
            layers.Conv2D(32, (3, 3), activation='relu', padding='same'),
            layers.MaxPooling2D((2, 2), padding='same'),
            layers.Flatten(),
            layers.Dense(latent_dim, activation='relu'),
        ])
        self.decoder = tf.keras.Sequential([
            layers.InputLayer(input_shape=(latent_dim,)),
            layers.Dense(32 * 32 * 64, activation='relu'),
            layers.Reshape((32, 32, 64)),
            layers.Conv2DTranspose(32, (3, 3), activation='relu', padding='same'),
            layers.UpSampling2D((2, 2)),
            layers.Conv2DTranspose(32, (3, 3), activation='relu', padding='same'),
            layers.UpSampling2D((2, 2)),
            layers.Conv2DTranspose(1, (3, 3), activation='sigmoid', padding='same'),
        ])

    def call(self, x):
        encoded = self.encoder(x)
        decoded = self.decoder(encoded)
        return decoded


autoencoder = Autoencoder(latent_dim)

autoencoder.compile(optimizer='adam', loss=losses.MeanSquaredError())

autoencoder.fit(x_train, x_train,
                epochs=100,
                shuffle=True,
                validation_data=(x_test, x_test))

autoencoder.save("autoencoder")

print(autoencoder.summary())

encoded_imgs = autoencoder.encoder(x_test).numpy()
decoded_imgs = autoencoder.decoder(encoded_imgs).numpy()

n = 10
plt.figure(figsize=(20, 4))
for i in range(n):
    ax = plt.subplot(2, n, i + 1)
    plt.imshow(x_test[i])
    plt.title("original")
    plt.gray()
    ax.get_xaxis().set_visible(False)
    ax.get_yaxis().set_visible(False)

    ax = plt.subplot(2, n, i + 1 + n)
    plt.imshow(decoded_imgs[i])
    plt.title("reconstructed")
    plt.gray()
    ax.get_xaxis().set_visible(False)
    ax.get_yaxis().set_visible(False)

plt.savefig('reconstructed.png')

autoencoder.encoder.save("encoder")

converter = tf.lite.TFLiteConverter.from_saved_model("encoder")

converter.optimizations = [tf.lite.Optimize.DEFAULT]
converter.target_spec.supported_types = [tf.float32]

tflite_model = converter.convert()

with open("encoder.tflite", "wb") as f:
    f.write(tflite_model)