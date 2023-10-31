package com.shadowings.kanjitrainerita

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.shadowings.kanjitrainerita.ui.theme.KanjiTrainerITATheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private var kanjiList: List<KanjiInfo> by mutableStateOf(listOf())
    private var currentKanji: KanjiInfo? by mutableStateOf(null)

    private fun nextKanji() {
        lifecycleScope.launch {
            currentKanji = null
            delay(250)
            currentKanji = kanjiList.random()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val kanjis = assets.open("database.txt").bufferedReader().use {
            it.readText()
        }

        val list: MutableList<KanjiInfo> = mutableListOf()

        val split = kanjis.split("\n")
        for (i in 0..split.size) {
            try {
                if (!split[i].contains("-----")) {
                    continue
                }
                val id = split[i].replace("-", "").toInt()
                val kanji = split[i + 1]
                val meaning = split[i + 2]
                val story = split[i + 3]
                val words = mutableListOf<WordInfo>()
                for (j in 0 until split[i + 4].toInt()) {
                    val kana = split[i + 5 + j * 3]
                    val kanji = split[i + 6 + j * 3]
                    val meaning = split[i + 7 + j * 3]
                    words.add(WordInfo(kana, kanji, meaning))
                }
                list.add(KanjiInfo(id, kanji, meaning, story, words))
            } catch (e: Exception) {
                Log.e("KanjiTrainerITA", "Error parsing kanji: ${e}")
            }
        }

        kanjiList = list.toList()

        Log.e("KanjiTrainerITA", "Kanji list size: ${kanjiList.size}")

        nextKanji()

        setContent {
            KanjiTrainerITATheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    KanjiCard(currentKanji, ::nextKanji)
                }
            }
        }
    }
}