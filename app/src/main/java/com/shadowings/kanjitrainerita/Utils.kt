package com.shadowings.kanjitrainerita

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import java.nio.ByteBuffer
import java.nio.ByteOrder

@Composable
fun annotateRecursively(
    placeHolderList: List<Pair<String, SpanStyle>>,
    originalText: String
): AnnotatedString {
    var annotatedString = buildAnnotatedString { append(originalText) }
    for (item in placeHolderList) {
        annotatedString = buildAnnotatedString {
            val startIndex = annotatedString.indexOf(item.first)
            val endIndex = startIndex + item.first.length
            append(annotatedString)
            addStyle(style = item.second, start = startIndex, end = endIndex)
        }
    }
    return annotatedString
}

fun boldTextInAsterisks(input: String): String {
    val regex = Regex("\\*(.*?)\\*")
    val replacedText = input.replace(regex) {
        "<b>${it.groups[1]?.value}</b>"
    }
    return replacedText
}

fun getGrayscaleBuffer(bitmap: Bitmap): ByteBuffer {
    val width = bitmap.width
    val height = bitmap.height
    val mImgData: ByteBuffer = ByteBuffer
        .allocateDirect(width * height)
    mImgData.order(ByteOrder.nativeOrder())
    val pixels = IntArray(width * height)
    bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
    for (pixel in pixels) {
        val color = Color.red(pixel)
        val byte = color.toByte()
        mImgData.put(byte)
    }
    return mImgData
}