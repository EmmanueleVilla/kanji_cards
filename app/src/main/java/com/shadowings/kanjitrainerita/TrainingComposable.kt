package com.shadowings.kanjitrainerita

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingComposable(kanjiList: List<KanjiInfo>, navController: NavHostController) {
    BackHandler(true) {
        Log.d("TAG", "OnBackPressed")
    }
    var showHint by remember { mutableStateOf(false) }
    var showAnswer by remember { mutableStateOf(false) }
    var mode = remember { mutableStateOf(TrainingMode.Card) }

    var subList by remember { mutableStateOf(listOf<KanjiInfo>()) }
    var currentKanji: KanjiInfo? by remember { mutableStateOf(null) }

    val context = LocalContext.current
    val preferencesManager = PreferencesManager(context)

    LaunchedEffect(Unit) {
        val list = preferencesManager.getKanjiIds(listOf())
        subList = kanjiList.filter { list.contains(it.id) }
        currentKanji = subList.random()
        Log.e("KanjiList", subList.toString())
    }

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
            currentKanji?.let { info ->
                if (mode.value == TrainingMode.Card) {
                    if (!showAnswer) {
                        BottomAppBar(
                            actions = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    IconButton(onClick = { }) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_fav_empty),
                                            contentDescription = "Favourite"
                                        )
                                    }

                                    IconButton(
                                        enabled = !showHint,
                                        onClick = { showHint = true }) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_hint),
                                            contentDescription = "Hint"
                                        )
                                    }

                                    IconButton(onClick = { showAnswer = true }) {
                                        Icon(
                                            Icons.Default.Done,
                                            contentDescription = "Done"
                                        )
                                    }
                                }
                            })
                    } else {
                        BottomAppBar(
                            actions = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    IconButton(onClick = {
                                        preferencesManager.putInt(
                                            "${info.id}",
                                            minOf(-5, info.happiness - 2)
                                        )
                                        currentKanji = subList.random()
                                        showAnswer = false
                                        showHint = false
                                    }) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_smile_very_dissatisfied),
                                            contentDescription = "very dissatisfied"
                                        )
                                    }
                                    IconButton(onClick = {
                                        preferencesManager.putInt(
                                            "${info.id}",
                                            minOf(-5, info.happiness - 1)
                                        )
                                        currentKanji = subList.random()
                                        showAnswer = false
                                        showHint = false
                                    }) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_smile_dissatisfied),
                                            contentDescription = "dissatisfied"
                                        )
                                    }
                                    IconButton(onClick = {
                                        currentKanji = subList.random()
                                        showAnswer = false
                                        showHint = false
                                    }) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_smile_neutral),
                                            contentDescription = "neutral"
                                        )
                                    }
                                    IconButton(onClick = {
                                        preferencesManager.putInt(
                                            "${info.id}",
                                            maxOf(5, info.happiness + 1)
                                        )
                                        currentKanji = subList.random()
                                        showAnswer = false
                                        showHint = false
                                    }) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_smile_satisfied),
                                            contentDescription = "satisfied"
                                        )
                                    }
                                    IconButton(
                                        enabled = !showHint,
                                        onClick = {
                                            preferencesManager.putInt(
                                                "${info.id}",
                                                maxOf(5, info.happiness + 2)
                                            )
                                            currentKanji = subList.random()
                                            showAnswer = false
                                            showHint = false
                                        }) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_smile_excited),
                                            contentDescription = "excited"
                                        )
                                    }
                                }
                            }
                        )
                    }
                }
            }

        }
    ) { paddingValues ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (currentKanji != null && mode.value == TrainingMode.Card) {
                KanjiCard(
                    info = currentKanji!!,
                    showAnswer = showAnswer,
                    showHint = showHint
                )
            }
        }
    }
}