package com.shadowings.kanjitrainerita

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun HomeComposablePreview() {
    HomeComposable(
        kanjis = listOf(
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

            )
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeComposable(kanjis: List<KanjiInfo>) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Kanji Trainer ITA")
                },
                actions = {
                    Row(Modifier.padding(end = 12.dp)) {
                        Icon(Icons.Default.Info, contentDescription = "Info")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                actions = {
                    IconButton(onClick = { /* do something */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Localized description")
                    }
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_sort),
                            contentDescription = "Localized description",
                        )
                    }
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_filter),
                            contentDescription = "Localized description",
                        )
                    }
                },
                floatingActionButton = {
                    ExtendedFloatingActionButton(onClick = { }) {
                        Row(
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Study")
                            Spacer(modifier = Modifier.padding(4.dp))
                            Text("Studia")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxWidth()
                .padding(
                    bottom = paddingValues.calculateBottomPadding(),
                    top = paddingValues.calculateTopPadding(),
                )
        ) {
            Divider()
            Row {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp),
                    text = "${kanjis.size} kanji visualizzati su ${kanjis.size}",
                    textAlign = TextAlign.Center
                )
            }
            Divider()
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp),
                content = {
                    items(kanjis.size) { index ->
                        KanjiListItem(kanjis[index])
                    }
                })
        }

    }
}