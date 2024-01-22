package com.shadowings.kanjitrainerita

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.pow
import kotlin.math.sqrt

fun getGrayscaleBuffer(bitmap: Bitmap): ByteBuffer {
    val width = bitmap.width
    val height = bitmap.height
    val mImgData: ByteBuffer = ByteBuffer
        .allocateDirect(width * height * 4)
    mImgData.order(ByteOrder.nativeOrder())
    val pixels = IntArray(width * height)
    bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
    for (pixel in pixels) {
        val color = Color.red(pixel)
        val byte = color.toByte()
        mImgData.putFloat(color.toFloat())
    }
    return mImgData
}

fun List<Float>.std(): Float {

    val size = this.size
    var sum = 0.0
    var standardDeviation = 0.0

    for (num in this) {
        sum += num
    }

    val mean = sum / size

    for (num in this) {
        standardDeviation += (num - mean).pow(2.0)
    }

    return sqrt(standardDeviation / size).toFloat()
}

fun vectorDistance(vector1: List<Float>, vector2: List<Float>): Float {
    require(vector1.size == vector2.size) { "Vectors must have the same size" }

    var sum = 0.0f
    for (i in vector1.indices) {
        val diff = vector1[i] - vector2[i]
        sum += diff * diff
    }

    return sqrt(sum)
}