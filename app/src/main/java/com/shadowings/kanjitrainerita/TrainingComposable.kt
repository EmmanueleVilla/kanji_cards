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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.lang.reflect.Type

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingComposable(kanjiList: List<KanjiInfo>, navController: NavHostController) {

    BackHandler {
        MainViewModel.reloadKanjiValues()
        navController.popBackStack()
    }
    var showHint by remember { mutableStateOf(false) }
    var showAnswer by remember { mutableStateOf(false) }

    var selectedList by remember { mutableStateOf(listOf<KanjiInfo>()) }
    var subList by remember { mutableStateOf(listOf<KanjiInfo>()) }
    var currentKanji: KanjiInfo? by remember { mutableStateOf(null) }

    var path by remember { mutableStateOf(Path()) }

    val context = LocalContext.current
    val preferencesManager = PreferencesManager(context)

    LaunchedEffect(Unit) {
        val boxes = kanjiList.groupBy { it.happiness }.toList().sortedBy { it.first }
        var list = mutableListOf<KanjiInfo>()

        val stringList: Type = object : TypeToken<ArrayList<String>>() {}.type

        val kanjiNeuralList: ArrayList<String> = Gson().fromJson(
            context.assets.open("kanji_list.json").bufferedReader().use {
                it.readText()
            }, stringList
        )

        while (list.size < 30) {

            val kanji = kanjiNeuralList.random()
            boxes.drop(2).flatMap { it.second }.firstOrNull { it.kanji == kanji }?.let {
                list.add(
                    it.copy(
                        mode = TrainingMode.Draw
                    )
                )
            } ?: run {
                kanjiList.firstOrNull { it.kanji == kanji }?.let {
                    list.add(it.copy(mode = TrainingMode.Draw))
                } ?: run {
                    Log.e("KanjiList", "Kanji not found: $kanji")
                }
            }

            list = list.distinctBy { it.kanji }.toMutableList()
        }

        list = list.shuffled().toMutableList()

        Log.e("KanjiList", list.map { it.mode }.joinToString(",") { it.toString() })

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
                    Text(text = "Kanji ${30 - subList.size}/30")
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

                                if (info.mode == TrainingMode.Draw) {
                                    IconButton(
                                        enabled = true,
                                        onClick = { path = Path() }) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_eraser),
                                            contentDescription = "Hint"
                                        )
                                    }
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

                    val responses = listOf(
                        Pair(-2, R.drawable.ic_smile_very_dissatisfied),
                        Pair(-1, R.drawable.ic_smile_dissatisfied),
                        Pair(0, R.drawable.ic_smile_neutral),
                        Pair(1, R.drawable.ic_smile_satisfied),
                        Pair(2, R.drawable.ic_smile_excited),
                    )

                    BottomAppBar(
                        actions = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                responses.forEach {
                                    IconButton(onClick = {
                                        preferencesManager.putInt(
                                            "${info.id}",
                                            maxOf(-2, minOf(2, info.happiness + it.first))
                                        )
                                        if (subList.isEmpty()) {
                                            navController.popBackStack()
                                        } else {
                                            currentKanji = subList[0]
                                            subList = subList.drop(1)
                                            showAnswer = false
                                            showHint = false
                                            path = Path()
                                        }
                                    }) {
                                        Icon(
                                            painter = painterResource(id = it.second),
                                            contentDescription = it.first.toString()
                                        )
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            currentKanji?.let {
                Log.e("KanjiList", it.mode.toString())
                when (it.mode) {
                    TrainingMode.Card -> {
                        KanjiCard(
                            info = it,
                            showAnswer = showAnswer,
                            showHint = showHint
                        )
                    }

                    TrainingMode.Draw -> {
                        DrawComposable(
                            info = it,
                            showAnswer = showAnswer,
                            showHint = showHint,
                            path = path
                        )
                    }
                }
            }
        }
    }
}