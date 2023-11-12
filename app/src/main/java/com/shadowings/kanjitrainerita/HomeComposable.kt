package com.shadowings.kanjitrainerita

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Preview
@Composable
fun HomeComposablePreview() {
    HomeComposable(
        kanjis = List(50) {
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
            )
        },
        navController = rememberNavController()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeComposable(kanjis: List<KanjiInfo>, navController: NavHostController) {

    var searching by rememberSaveable { mutableStateOf(false) }
    var query by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false) }
    var sortedBy by rememberSaveable { mutableStateOf(SortedBy.IdAsc) }

    var filteredList = kanjis.filter { kanji ->
        kanji.searchString.contains(query, ignoreCase = true)
    }.sortedBy {
        when (sortedBy) {
            SortedBy.IdAsc -> it.id
            SortedBy.IdDesc -> -it.id
            SortedBy.JlptAsc -> it.jlptLevel
            SortedBy.JlptDesc -> -it.jlptLevel
            SortedBy.MoodAsc -> it.happiness
            SortedBy.MoodDesc -> -it.happiness
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
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

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AnimatedVisibility(!searching) {
                            IconButton(onClick = {
                                searching = true
                            }) {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Localized description"
                                )
                            }
                        }
                        AnimatedVisibility(searching) {
                            Row(
                                modifier = Modifier,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                IconButton(onClick = {
                                    searching = false
                                    active = false
                                    query = ""
                                    filteredList = kanjis
                                }) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Localized description"
                                    )
                                }
                                SearchBar(
                                    modifier = Modifier,
                                    query = query,
                                    onQueryChange = {
                                        Log.e("HomeComposable", "onQueryChange: $it")
                                        query = it
                                    },
                                    onSearch = {},
                                    active = active,
                                    onActiveChange = {
                                        active = it
                                    },
                                    placeholder = { Text("Kanji o significato") },
                                ) {
                                }
                            }
                        }
                        IconButton(onClick = {
                            sortedBy = when (sortedBy) {
                                SortedBy.IdAsc -> SortedBy.IdDesc
                                SortedBy.IdDesc -> SortedBy.JlptAsc
                                SortedBy.JlptAsc -> SortedBy.JlptDesc
                                SortedBy.JlptDesc -> SortedBy.MoodAsc
                                SortedBy.MoodAsc -> SortedBy.MoodDesc
                                SortedBy.MoodDesc -> SortedBy.IdAsc
                            }
                        }) {
                            Icon(
                                painter = painterResource(
                                    id =
                                    when (sortedBy) {
                                        SortedBy.IdAsc -> R.drawable.ic_sort_id_asc
                                        SortedBy.IdDesc -> R.drawable.ic_sort_id_desc
                                        SortedBy.JlptAsc -> R.drawable.ic_sort_jlpt_asc
                                        SortedBy.JlptDesc -> R.drawable.ic_sort_jlpt_desc
                                        SortedBy.MoodAsc -> R.drawable.ic_sort_smile_asc
                                        SortedBy.MoodDesc -> R.drawable.ic_sort_smile_desc
                                    }
                                ),
                                contentDescription = "Localized description",
                            )
                        }
                        IconButton(onClick = { /* do something */ }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_filter),
                                contentDescription = "Localized description",
                            )
                        }
                    }
                },
                floatingActionButton = {
                    ExtendedFloatingActionButton(onClick = {
                        navController.navigate("training")
                    }) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp),
                content = {
                    items(filteredList.size) { index ->
                        KanjiListItem(filteredList[index])
                    }
                })
        }
    }
}