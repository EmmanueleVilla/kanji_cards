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

    BackHandler {
        MainViewModel.reloadKanjiValues()
        navController.popBackStack()
    }
    var showHint by remember { mutableStateOf(false) }
    var showAnswer by remember { mutableStateOf(false) }
    val mode = remember { mutableStateOf(TrainingMode.Card) }

    var selectedList by remember { mutableStateOf(listOf<KanjiInfo>()) }
    var subList by remember { mutableStateOf(listOf<KanjiInfo>()) }
    var currentKanji: KanjiInfo? by remember { mutableStateOf(null) }

    val context = LocalContext.current
    val preferencesManager = PreferencesManager(context)

    LaunchedEffect(Unit) {
        val boxes = kanjiList.groupBy { it.happiness }.toList().sortedBy { it.first }
        var list = mutableListOf<KanjiInfo>()
        while (list.size < 25) {
            try {
                // take 7 kanjis from the first box
                if (boxes.isNotEmpty()) {
                    list.addAll(boxes[0].second.shuffled().take(7))
                }

                // take 5 kanjis from the second box
                if (boxes.size > 1) {
                    list.addAll(boxes[1].second.shuffled().take(5))
                }

                // take 1 kanji from the other boxes
                if (boxes.size > 2) {
                    list.addAll(boxes.drop(2).flatMap { it.second }.shuffled().take(1))
                }
            } catch (e: Exception) {
                list.add(kanjiList.random())
            }

            // remove duplicates
            list = list.distinct().toMutableList()
        }

        selectedList = list
        subList = list
        currentKanji = subList[0]
        subList = subList.drop(1)
        Log.e("KanjiList", subList.toString())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Kanji ${25 - subList.size}/25")
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
                                            minOf(-2, info.happiness - 2)
                                        )
                                        if (subList.isEmpty()) {
                                            navController.popBackStack()
                                        }
                                        currentKanji = subList[0]
                                        subList = subList.drop(1)

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
                                            minOf(-2, info.happiness - 1)
                                        )
                                        if (subList.isEmpty()) {
                                            navController.popBackStack()
                                        }
                                        currentKanji = subList[0]
                                        subList = subList.drop(1)
                                        showAnswer = false
                                        showHint = false
                                    }) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_smile_dissatisfied),
                                            contentDescription = "dissatisfied"
                                        )
                                    }
                                    IconButton(onClick = {
                                        if (subList.isEmpty()) {
                                            navController.popBackStack()
                                        }
                                        currentKanji = subList[0]
                                        subList = subList.drop(1)
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
                                        if (subList.isEmpty()) {
                                            navController.popBackStack()
                                        }
                                        currentKanji = subList[0]
                                        subList = subList.drop(1)
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
                                            if (subList.isEmpty()) {
                                                navController.popBackStack()
                                            }
                                            currentKanji = subList[0]
                                            subList = subList.drop(1)
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