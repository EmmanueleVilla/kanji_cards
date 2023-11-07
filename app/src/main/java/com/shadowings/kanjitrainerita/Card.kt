package com.shadowings.kanjitrainerita

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
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
                    happiness = 0
                ),
                {}
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KanjiCard(info: KanjiInfo, nextKanji: () -> Unit) {
    var showHint by remember { mutableStateOf(false) }
    var showAnswer by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Kanji Trainer ITA")
                },
                navigationIcon = {
                    Row(Modifier.padding(start = 12.dp)) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(onClick = {

                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_fav_empty),
                                contentDescription = "Favourite"
                            )
                        }

                        IconButton(onClick = {
                            showHint = true
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_hint),
                                contentDescription = "Hint"
                            )
                        }

                        IconButton(onClick = {
                            showAnswer = true
                        }) {
                            Icon(
                                Icons.Default.Done,
                                contentDescription = "Done"
                            )
                        }
                    }
                })
        }
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(
                    bottom = paddingValues.calculateBottomPadding(),
                    top = paddingValues.calculateTopPadding(),
                    start = 16.dp,
                    end = 16.dp
                )
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ElevatedCard(Modifier.size(300.dp)) {
                Text(
                    modifier = Modifier.fillMaxSize(),
                    text = info.kanji,
                    fontSize = 220.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.size(16.dp))
            AnimatedVisibility(visible = showHint || showAnswer) {
                ElevatedCard(Modifier.width(300.dp)) {
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
            }

            AnimatedVisibility(visible = showHint) {
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
                    Spacer(modifier = Modifier.size(16.dp))
                    ElevatedCard(Modifier.width(300.dp)) {
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
                }
            }
        }
    }
}