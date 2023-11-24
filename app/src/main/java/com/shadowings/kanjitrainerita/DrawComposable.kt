package com.shadowings.kanjitrainerita

import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.shadowings.kanjitrainerita.ml.Model
import com.shadowings.kanjitrainerita.ui.theme.KanjiTrainerITATheme
import com.smarttoolfactory.gesture.pointerMotionEvents
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.lang.reflect.Type


class DarkModeProvider : PreviewParameterProvider<Boolean> {
    override val values = sequenceOf(true, false)
}

@Preview
@Composable
fun DrawPreview(@PreviewParameter(DarkModeProvider::class) isDarkMode: Boolean) {
    KanjiTrainerITATheme(darkTheme = isDarkMode) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            DrawComposable(
                info = KanjiInfo(
                    id = 0,
                    jlptLevel = 5,
                    kanji = "新",
                    meaning = "Latte",
                    story = "Una pila 立 di legna 木 appena tagliata *nuova* con un'ascia 斤",
                    words = listOf(
                        WordInfo(
                            kana = "あたらしい",
                            kanji = "新しい",
                            meaning = "Nuovo"
                        ),
                        WordInfo(
                            kana = "しんせかい",
                            kanji = "新世界",
                            meaning = "Nuovo mondo"
                        ),
                    ),
                    happiness = 0
                ),
                showAnswer = true,
                showHint = true,
                darkMode = isDarkMode,
                path = Path()
            )
        }
    }
}

@Composable
fun DrawComposable(
    info: KanjiInfo,
    showAnswer: Boolean,
    showHint: Boolean,
    path: Path,
    darkMode: Boolean = isSystemInDarkTheme()
) {
    var motionEvent by remember { mutableStateOf(MotionEvent.Idle) }
    var currentPosition by remember { mutableStateOf(Offset.Unspecified) }
    var previousPosition by remember { mutableStateOf(Offset.Unspecified) }

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
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ElevatedCard(Modifier.width(300.dp)) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                text = "Disegna il kanji per",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
                text = info.meaning,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )
        }
        val snapShot = CaptureBitmap {
            Canvas(
                modifier = drawModifier
                    .fillMaxWidth()
                    .height(screenWidth - screenWidth / 10)
                    .padding(16.dp)
                    .background(if (darkMode) Color.Black else Color.White)
                    .border(1.dp, Color.Black)
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
                    color = if (!darkMode) Color.Black else Color.White,
                    path = path,
                    style = Stroke(
                        width = 14.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )

                drawLine(
                    color = Color.Gray,
                    start = Offset(size.width / 2, 0f),
                    end = Offset(size.width / 2, size.height),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(
                        floatArrayOf(10f, 10f), 0f
                    )
                )
                drawLine(
                    color = Color.Gray,
                    start = Offset(0f, size.height / 2),
                    end = Offset(size.width, size.height / 2),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(
                        floatArrayOf(10f, 10f), 0f
                    )
                )
            }
        }
        AnimatedVisibility(visible = showHint || showAnswer) {
            Column {
                Spacer(modifier = Modifier.size(16.dp))
                ElevatedCard(Modifier.width(300.dp)) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 4.dp, start = 8.dp, end = 8.dp),
                        text = "STORIA",
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 8.dp, start = 8.dp, end = 8.dp),
                        text = info.story,
                        fontSize = 22.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
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