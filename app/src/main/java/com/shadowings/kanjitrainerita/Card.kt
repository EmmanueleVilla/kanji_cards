package com.shadowings.kanjitrainerita

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shadowings.kanjitrainerita.ui.theme.KanjiTrainerITATheme

@Preview
@Composable
fun KanjiCardPreview() {
    KanjiTrainerITATheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            KanjiCard(
                KanjiInfo(
                    id = 0,
                    jlptLevel = 5,
                    kanji = "乳",
                    meaning = "Latte",
                    story = "Con le unghie 爪 i bambini 子 si attaccano乚 al biberon ",
                    words = listOf(
                        WordInfo(
                            kana = "ぎゅうにゅう",
                            kanji = "牛乳",
                            meaning = "latte vaccino"
                        ),
                        WordInfo(
                            kana = "ぼにゅう",
                            kanji = "母乳",
                            meaning = "latte materno"
                        ),
                    ),
                    seenCount = 0
                ),
                {}
            )
        }
    }
}

@Composable
fun KanjiCard(kanjiInfo: KanjiInfo?, nextKanji: () -> Unit) {
    var showAnswer by remember { mutableStateOf(false) }

    kanjiInfo?.let { info ->
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(Modifier.size(300.dp)) {
                Text(
                    modifier = Modifier.fillMaxSize(),
                    text = info.kanji,
                    fontSize = 220.sp,
                    textAlign = TextAlign.Center
                )
            }
            if (!showAnswer) {
                Spacer(modifier = Modifier.size(16.dp))
                Button(onClick = {
                    showAnswer = true
                }) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Rounded.PlayArrow,
                            contentDescription = "icona-bottone-soluzione"
                        )
                        Text(text = "Mostra Soluzione")
                    }
                }
            } else {
                Spacer(modifier = Modifier.size(16.dp))
                Card(Modifier.width(300.dp)) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 4.dp, start = 8.dp, end = 8.dp),
                        text = "SIGNIFICATO",
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 8.dp, start = 8.dp, end = 8.dp),
                        text = info.meaning,
                        fontSize = 22.sp,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.size(16.dp))
                Card(Modifier.width(300.dp)) {
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
                Spacer(modifier = Modifier.size(16.dp))
                Card(Modifier.width(300.dp)) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 4.dp, start = 8.dp, end = 8.dp),
                        text = "PAROLE",
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    info.words.forEach {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, bottom = 0.dp, start = 8.dp, end = 8.dp),
                            text = it.kana,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 0.dp, bottom = 8.dp, start = 8.dp, end = 8.dp),
                            text = "${it.kanji} (${it.meaning})",
                            fontSize = 22.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Spacer(modifier = Modifier.size(16.dp))
                Button(onClick = {
                    nextKanji()
                    showAnswer = false
                }) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Rounded.PlayArrow,
                            contentDescription = "icona-bottone-prossima"
                        )
                        Text(text = "Prossima Carta")
                    }
                }
            }
        }
    } ?: run {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}