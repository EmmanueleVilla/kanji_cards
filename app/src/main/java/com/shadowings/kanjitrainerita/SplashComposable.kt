package com.shadowings.kanjitrainerita

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shadowings.kanjitrainerita.ui.theme.KanjiTrainerITATheme

@Preview
@Composable
fun SplashComposablePreview() {
    KanjiTrainerITATheme {
        SplashComposable()
    }
}

@Composable
fun SplashComposable() {
    Box(Modifier.fillMaxSize()) {
        ElevatedCard(
            Modifier
                .size(75.dp)
                .align(Alignment.Center)) {
            Text(
                modifier = Modifier.fillMaxSize(),
                text = "伊",
                fontSize = 50.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}