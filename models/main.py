import json
from PIL import Image, ImageDraw, ImageFont
import os
import random
import tensorflow as tf
from tensorflow import keras
from tensorflow.keras import layers
from tensorflow.keras.utils import plot_model
from tensorflow.keras.layers.experimental.preprocessing import (
    RandomRotation,
    RandomZoom,
    Rescaling,
    RandomTranslation,
)
from keras.utils import plot_model
import matplotlib.pyplot as plt
import pandas as pd
import seaborn as sns
from sklearn.metrics import confusion_matrix
import numpy as np
from tflite_support import flatbuffers
from tflite_support import metadata as _metadata
from tflite_support import metadata_schema_py_generated as _metadata_fb

INPUT_SIZE = 128
MAX_EPOCHS = 1000

if os.path.exists("conc"):
    conc_model = tf.keras.models.load_model("conc")

    # load the kanji list
    with open("kanji_list.json", "r") as f:
        kanji_list = json.load(f)

    # output
    tsv = "id\tkanji\tfont\tpredicted\taccuracy\n"

    label_index = 0
    count = 0
    # for each folder in the /kanji folder
    for folder in os.listdir("kanji"):
        # for each image in the folder
        # the kanji id is the folder name
        kanji_id = folder

        for image in os.listdir(os.path.join("kanji", folder)):
            # the image name is ID_image_{font}.png
            font_id = image.split("_")[2].split(".")[0]

            # load the image
            img = tf.keras.preprocessing.image.load_img(
                os.path.join("kanji", folder, image), color_mode="grayscale"
            )
            # resize the image
            img = img.resize((INPUT_SIZE, INPUT_SIZE))
            # convert the image to a numpy array
            img_array = tf.keras.preprocessing.image.img_to_array(img)
            # expand the dimensions of the image to match the input of the model
            img_array = tf.expand_dims(img_array, 0)
            # predict the kanji
            predictions = conc_model.predict(img_array, verbose=0)
            # get the index of the highest probability
            index = np.argmax(predictions[0])
            max = predictions[0][index]
            if index != label_index:
                new_row = f"{kanji_id}\t{kanji_list[label_index]}\t{font_id}\t{kanji_list[index]}\t{max}\n"
                print(new_row)
                tsv += new_row
        label_index += 1

    # save the tsv file
    with open("predictions.tsv", "w", encoding="utf-8") as f:
        f.write(tsv)
    exit(1)

output_folder = "kanji"

data = pd.read_csv("kanji.tsv", sep="\t")
df = pd.DataFrame(data)
num_classes = len(df.index)

if not os.path.exists(output_folder):
    font_folder = "fonts"

    font_files = [
        f for f in os.listdir(font_folder) if f.endswith(".ttf") or f.endswith(".otf")
    ]

    os.mkdir(output_folder)

    font_objects = [
        ImageFont.truetype(os.path.join(font_folder, font_file), size=INPUT_SIZE * 0.8)
        for font_file in font_files
    ]

    kanji_list = [row["kanji"] for index, row in df.iterrows()]
    # save kanji list to json file
    with open("kanji_list.json", "w") as f:
        json.dump(kanji_list, f)

    for index, row in df.iterrows():
        id = row["id"]
        kanji = row["kanji"]

        label_out = os.path.join(output_folder, str(id).zfill(5))

        os.mkdir(label_out)

        count = 0
        for font in font_objects:
            image = Image.new("RGB", (INPUT_SIZE, INPUT_SIZE), "black")
            draw = ImageDraw.Draw(image)

            draw.text(
                (INPUT_SIZE / 2, INPUT_SIZE / 2),
                kanji,
                font=font,
                anchor="mm",
                align="center",
                fill="white",
            )

            image_path = os.path.join(label_out, f"{id}_image_{count}.png")
            image.save(image_path)
            count += 1

    image.close()

    print("dataset created")
else:
    print("dataset already exists, skipping the creation")

# set used just to print because it's easier with int values
sample_ds = tf.keras.utils.image_dataset_from_directory(
    output_folder,
    color_mode="grayscale",
    batch_size=32,
    image_size=(INPUT_SIZE, INPUT_SIZE),
    shuffle=True,
    seed=42,
)

train_ds = tf.keras.utils.image_dataset_from_directory(
    output_folder,
    label_mode="categorical",
    color_mode="grayscale",
    batch_size=32,
    image_size=(INPUT_SIZE, INPUT_SIZE),
    shuffle=True,
    seed=42,
    validation_split=0.2,
    subset="training",
)

val_ds = tf.keras.utils.image_dataset_from_directory(
    output_folder,
    label_mode="categorical",
    color_mode="grayscale",
    batch_size=32,
    image_size=(INPUT_SIZE, INPUT_SIZE),
    shuffle=True,
    seed=42,
    validation_split=0.2,
    subset="validation",
)

plt.figure(figsize=(10, 10))

print(sample_ds.class_names)

for images, labels in sample_ds.take(1):
    for i in range(9):
        ax = plt.subplot(3, 3, i + 1)
        plt.imshow(images[i].numpy().astype("uint8"), cmap="Greys")
        plt.title(sample_ds.class_names[labels[i]])
        plt.axis("off")

plt.savefig("dataset.png")

plt.close()

data_augmentation = keras.Sequential(
    [
        RandomRotation(factor=0.03),
        RandomZoom(height_factor=0.1, width_factor=0.1),
        RandomTranslation(height_factor=0.05, width_factor=0.05),
    ]
)

aug_train_ds = train_ds.map(lambda x, y: (data_augmentation(x, training=True), y))
aug_val_ds = val_ds.map(lambda x, y: (data_augmentation(x, training=True), y))

AUTOTUNE = tf.data.AUTOTUNE
aug_train_ds = aug_train_ds.prefetch(buffer_size=AUTOTUNE)
aug_val_ds = aug_val_ds.prefetch(buffer_size=AUTOTUNE)

plt.figure(figsize=(10, 10))

for images, labels in aug_train_ds.take(1):
    for i in range(9):
        ax = plt.subplot(3, 3, i + 1)
        plt.imshow(images[i].numpy().astype("uint8"), cmap="Greys")
        plt.axis("off")

plt.savefig("augmented.png")

plt.close()

configs = [
    ("333", (3, 3), (3, 3), (3, 3)),
    ("533", (5, 5), (3, 3), (3, 3)),
    ("753", (7, 7), (5, 5), (3, 3)),
]

for name, kern_size_one, kern_size_two, kern_size_three in configs:
    if not os.path.exists(name):
        model = keras.Sequential(
            [
                layers.Input(
                    shape=(INPUT_SIZE, INPUT_SIZE, 1),
                    dtype=tf.uint8,
                    name=name + "_input",
                ),
                layers.Lambda(
                    lambda x: tf.cast(x, tf.float32) / 255.0, name=name + "_rescale"
                ),
                layers.Conv2D(
                    32, kern_size_one, activation="relu", name=name + "_conv1"
                ),
                layers.BatchNormalization(name=name + "_batchnorm1"),
                layers.MaxPooling2D((2, 2), name=name + "_maxpool1"),
                layers.Conv2D(
                    32, kern_size_two, activation="relu", name=name + "_conv2"
                ),
                layers.BatchNormalization(name=name + "_batchnorm2"),
                layers.MaxPooling2D((2, 2), name=name + "_maxpool2"),
                layers.Conv2D(
                    32, kern_size_three, activation="relu", name=name + "_conv3"
                ),
                layers.BatchNormalization(name=name + "_batchnorm3"),
                layers.MaxPooling2D((2, 2), name=name + "_maxpool3"),
                layers.Flatten(name=name + "_flatten"),
                layers.Dropout(0.35, name=name + "_dropout1"),
                layers.Dense(num_classes * 2, activation="relu", name=name + "_dense1"),
                layers.Dropout(0.35, name=name + "_dropout2"),
                layers.Dense(num_classes, activation="softmax", name=name + "_output"),
            ]
        )

        # Compile the model
        model.compile(
            optimizer="sgd",
            loss="categorical_crossentropy",
            metrics=["accuracy"],
        )

        # Stops early if loss doesn't improve in 10 epochs
        e_callback = tf.keras.callbacks.EarlyStopping(monitor="loss", patience=5)

        training = model.fit(
            aug_train_ds,
            epochs=MAX_EPOCHS,
            validation_data=aug_val_ds,
            callbacks=[e_callback],
        )

        model.evaluate(aug_val_ds)

        model.save(name)

        print(model.summary())

        data = pd.DataFrame(training.history)[["accuracy", "val_accuracy"]]
        data.plot()
        plt.savefig("accuracy_" + name + ".png")
        plt.close()

        data = pd.DataFrame(training.history)[["loss", "val_loss"]].plot()
        data.plot()
        plt.savefig("loss_" + name + ".png")
        plt.close()
    else:
        print("model " + name + " already trained, skipping the creation")

first_model = tf.keras.models.load_model("333")
second_model = tf.keras.models.load_model("533")
third_model = tf.keras.models.load_model("753")

models = [first_model, second_model, third_model]
for model in models:
    for layer in model.layers:
        layer.trainable = False

# Common input
input = tf.keras.Input(shape=(INPUT_SIZE, INPUT_SIZE, 1))

# Get output for each model input
outputs = [model(input) for model in models]

# Contenate the ouputs
x = tf.keras.layers.Concatenate(name="concatenate")(outputs)

x = tf.keras.layers.Dense(num_classes * 1.75, activation="relu", name="dense_0")(x)

x = tf.keras.layers.Dropout(0.35, name="dropout_0")(x)

x = tf.keras.layers.Dense(num_classes * 1.75, activation="relu", name="dense_1")(x)

x = tf.keras.layers.Dropout(0.35, name="dropout_1")(x)

output = tf.keras.layers.Dense(num_classes, activation="softmax", name="dense_2")(x)

# Create concatenated model
conc_model = tf.keras.Model(input, output)

if os.path.exists("conc"):
    conc_model = tf.keras.models.load_model("conc")
else:
    # Compile the model
    conc_model.compile(
        optimizer="sgd",
        loss="categorical_crossentropy",
        metrics=["accuracy"],
    )

    # Stops early if loss doesn't improve in N epochs
    e_callback = tf.keras.callbacks.EarlyStopping(monitor="loss", patience=15)

    training = conc_model.fit(
        aug_train_ds,
        epochs=MAX_EPOCHS,
        validation_data=aug_val_ds,
        callbacks=[e_callback],
    )

    conc_model.save("conc")

    # show model structure
    print(conc_model.summary())

    data = pd.DataFrame(training.history)[["accuracy", "val_accuracy"]]
    data.plot()
    plt.savefig("accuracy_conc.png")
    plt.close()

    data = pd.DataFrame(training.history)[["loss", "val_loss"]].plot()
    data.plot()
    plt.savefig("loss_conc.png")
    plt.close()

for model in models:
    model.evaluate(aug_val_ds)
conc_model.evaluate(aug_val_ds)

plot_model(conc_model, to_file="model.png")

# Convert the model to tf lite
converter = tf.lite.TFLiteConverter.from_keras_model(conc_model)
# converter.optimizations = [tf.lite.Optimize.DEFAULT]
# converter.target_spec.supported_ops = [tf.lite.OpsSet.TFLITE_BUILTINS]
tflite_model = converter.convert()

# Save the model to file
with open("model.tflite", "wb") as f:
    f.write(tflite_model)

interpreter = tf.lite.Interpreter(model_path="model.tflite")
interpreter.allocate_tensors()

# Get input and output tensors.
input_details = interpreter.get_input_details()
output_details = interpreter.get_output_details()

# Test the model on random input data.
input_shape = input_details[0]["shape"]
img = tf.keras.preprocessing.image.load_img(
    "kanji\\02129\\2129_image_1.png", color_mode="grayscale"
)
# resize the image
img = img.resize((INPUT_SIZE, INPUT_SIZE))
# convert the image to a numpy array
img_array = tf.keras.preprocessing.image.img_to_array(img)
print("_____________________")
print(img_array.max())
# expand the dimensions of the image to match the input of the model
input_data = tf.expand_dims(img_array, 0)
interpreter.set_tensor(input_details[0]["index"], input_data)

interpreter.invoke()

# The function `get_tensor()` returns a copy of the tensor data.
# Use `tensor()` in order to get a pointer to the tensor.
output_data = interpreter.get_tensor(output_details[0]["index"])

# get the index of the highest probability
index = np.argmax(output_data)
print(index)
# load the kanji list
with open("kanji_list.json", "r") as f:
    kanji_list = json.load(f)
# print the kanji
print(kanji_list[index])

# Creates model info.
model_meta = _metadata_fb.ModelMetadataT()
model_meta.name = "Shadowings kanji classifier"
model_meta.description = "Shadowings kanji classifier"
model_meta.version = "0.1"
model_meta.author = "Emmanuele Villa"
model_meta.license = (
    "Apache License. Version 2.0 " "http://www.apache.org/licenses/LICENSE-2.0."
)

# Creates input info.
input_meta = _metadata_fb.TensorMetadataT()

# Creates output info.
output_meta = _metadata_fb.TensorMetadataT()

input_meta.name = "image"
input_meta.description = (
    "Input image to be classified. The expected image is {0} x {1}, with "
    "one channel per pixel. Each value in the "
    "tensor is a single byte between 0 and 255.".format(INPUT_SIZE, INPUT_SIZE)
)
input_meta.content = _metadata_fb.ContentT()
input_meta.content.contentProperties = _metadata_fb.ImagePropertiesT()
input_meta.content.contentProperties.colorSpace = _metadata_fb.ColorSpaceType.GRAYSCALE
input_meta.content.contentPropertiesType = (
    _metadata_fb.ContentProperties.ImageProperties
)
input_normalization = _metadata_fb.ProcessUnitT()
input_normalization.optionsType = _metadata_fb.ProcessUnitOptions.NormalizationOptions
input_normalization.options = _metadata_fb.NormalizationOptionsT()
input_normalization.options.mean = [127.5]
input_normalization.options.std = [127.5]
input_meta.processUnits = [input_normalization]
input_stats = _metadata_fb.StatsT()
input_stats.max = [255]
input_stats.min = [0]
input_meta.stats = input_stats

output_meta = _metadata_fb.TensorMetadataT()
output_meta.name = "probability"
output_meta.description = "Probabilities of the N labels respectively."
output_meta.content = _metadata_fb.ContentT()
output_meta.content.content_properties = _metadata_fb.FeaturePropertiesT()
output_meta.content.contentPropertiesType = (
    _metadata_fb.ContentProperties.FeatureProperties
)
output_stats = _metadata_fb.StatsT()
output_stats.max = [1.0]
output_stats.min = [0.0]
output_meta.stats = output_stats

# Creates subgraph info.
subgraph = _metadata_fb.SubGraphMetadataT()
subgraph.inputTensorMetadata = [input_meta]
subgraph.outputTensorMetadata = [output_meta]
model_meta.subgraphMetadata = [subgraph]

b = flatbuffers.Builder(0)
b.Finish(model_meta.Pack(b), _metadata.MetadataPopulator.METADATA_FILE_IDENTIFIER)
metadata_buf = b.Output()

populator = _metadata.MetadataPopulator.with_model_file("model.tflite")
populator.load_metadata_buffer(metadata_buf)
populator.populate()

tf.lite.experimental.Analyzer.analyze(
    model_content=tflite_model, gpu_compatibility=True
)
