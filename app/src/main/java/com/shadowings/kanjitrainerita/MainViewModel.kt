package com.shadowings.kanjitrainerita

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val kanjiLiveData: MutableLiveData<List<KanjiInfo>> = MutableLiveData(listOf())
    val currentKanji = MutableLiveData<KanjiInfo?>(null)

    private val fullKanjiList: MutableLiveData<List<KanjiInfo>> = MutableLiveData(listOf())

    init {
        viewModelScope.launch {
            val kanjis = application.assets.open("kanji.tsv").bufferedReader().use {
                it.readText()
            }

            val list: MutableList<KanjiInfo> = mutableListOf()
            val prefManager = PreferencesManager(application)

            val rows = kanjis.split("\n").drop(1)
            for (row in rows) {
                try {
                    val columns = row.split("\t")
                    val id = columns[0].toInt()
                    val jlptLevel = columns[1].toInt()
                    val kanji = columns[2]
                    val meaning = columns[3]
                    val story = columns[4]
                    val words = mutableListOf<WordInfo>()
                    if (columns.size > 5) {
                        val wordKana = columns[5]
                        val wordKanji = columns[6]
                        val wordMeaning = columns[7]
                        words.add(WordInfo(wordKana, wordKanji, wordMeaning))
                    }
                    if (columns.size > 8) {
                        val wordKana = columns[8]
                        val wordKanji = columns[9]
                        val wordMeaning = columns[10]
                        words.add(WordInfo(wordKana, wordKanji, wordMeaning))
                    }
                    if (columns.size > 11) {
                        val wordKana = columns[11]
                        val wordKanji = columns[12]
                        val wordMeaning = columns[13]
                        words.add(WordInfo(wordKana, wordKanji, wordMeaning))
                    }
                    val happiness = prefManager.getInt(id.toString(), 0)
                    list.add(
                        KanjiInfo(
                            id = id,
                            jlptLevel = jlptLevel,
                            kanji = kanji,
                            meaning = meaning,
                            story = story,
                            words = words,
                            happiness = happiness
                        )
                    )
                } catch (e: Exception) {
                    Log.e("KanjiTrainerITA", "Error parsing kanji: $row")
                }
            }
            kanjiLiveData.value = list.toList()
            fullKanjiList.value = list.toList()
            nextKanji()
        }
    }

    fun nextKanji() {
        viewModelScope.launch {
            currentKanji.value = null
            delay(250)
            currentKanji.value =
                kanjiLiveData.value?.sortedBy { it.happiness }?.subList(0, 10)?.random()
        }
    }
}