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
