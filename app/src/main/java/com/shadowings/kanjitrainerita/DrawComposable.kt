package com.shadowings.kanjitrainerita

import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.shadowings.kanjitrainerita.ml.Model
import com.smarttoolfactory.gesture.pointerMotionEvents
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.lang.reflect.Type


@Preview
@Composable
fun DrawPreview() {
    DrawComposable()
}

@Composable
fun DrawComposable() {
    var motionEvent by remember { mutableStateOf(MotionEvent.Idle) }
    var currentPosition by remember { mutableStateOf(Offset.Unspecified) }
    var previousPosition by remember { mutableStateOf(Offset.Unspecified) }
    var path by remember { mutableStateOf(Path()) }
    val drawModifier = Modifier
        .pointerMotionEvents(
            onDown = { pointerInputChange: PointerInputChange ->
                currentPosition = pointerInputChange.position
                motionEvent = MotionEvent.Down
                pointerInputChange.consume()
            },
            onMove = { pointerInputChange: PointerInputChange ->
                currentPosition = pointerInputChange.position
                motionEvent = MotionEvent.Move
                pointerInputChange.consume()
            },
            onUp = { pointerInputChange: PointerInputChange ->
                motionEvent = MotionEvent.Up
                pointerInputChange.consume()
            },
            delayAfterDownInMillis = 25L
        )
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val context = LocalContext.current
    Column(
        Modifier
            .fillMaxSize()
            .padding(screenWidth / 20),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val snapShot = CaptureBitmap {
            Canvas(
                modifier = drawModifier
                    .fillMaxWidth()
                    .height(screenWidth - screenWidth / 10)
                    .background(Color.Black)
            ) {
                when (motionEvent) {
                    MotionEvent.Down -> {
                        path.moveTo(currentPosition.x, currentPosition.y)
                        previousPosition = currentPosition
                    }

                    MotionEvent.Move -> {
                        path.quadraticBezierTo(
                            previousPosition.x,
                            previousPosition.y,
                            (previousPosition.x + currentPosition.x) / 2,
                            (previousPosition.y + currentPosition.y) / 2

                        )
                        previousPosition = currentPosition
                    }

                    MotionEvent.Up -> {
                        path.lineTo(currentPosition.x, currentPosition.y)
                        currentPosition = Offset.Unspecified
                        previousPosition = currentPosition
                        motionEvent = MotionEvent.Idle
                    }

                    else -> Unit
                }

                drawPath(
                    color = Color.White,
                    path = path,
                    style = Stroke(
                        width = 14.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }
        Button(onClick = { path = Path() }, modifier = Modifier.padding(8.dp)) {
            Text(text = "Clear", fontSize = 18.sp)
        }
        Button(onClick = {

            val bitmap = snapShot.invoke()
            val scaled = Bitmap.createScaledBitmap(bitmap, 128, 128, false)

            val model = Model.newInstance(context)

            val image = TensorBuffer.createFixedSize(intArrayOf(1, 128, 128, 1), DataType.FLOAT32)

            image.loadBuffer(getGrayscaleBuffer(scaled))

            val outputs = model.process(image)
            Log.e("outputs", outputs.probabilityAsTensorBuffer.toString())
            val probability = outputs.probabilityAsTensorBuffer.floatArray
            Log.e("prob", probability.contentToString())
            val intProb = probability.map { (it * 100).toInt() }
            val maxProb = intProb.maxOrNull() ?: 0
            val index = intProb.indexOf(maxProb)
            model.close()

            val stringList: Type = object : TypeToken<ArrayList<String>>() {}.type

            val kanjiList: ArrayList<String> = Gson().fromJson(
                context.assets.open("kanji_list.json").bufferedReader().use {
                    it.readText()
                }, stringList
            )

            Toast.makeText(
                context,
                "Predicted ${kanjiList[index]} with $maxProb% probability",
                Toast.LENGTH_SHORT
            ).show()


        }, modifier = Modifier.padding(8.dp)) {
            Text(text = "Classify", fontSize = 18.sp)
        }
    }
}