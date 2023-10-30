package com.shadowings.kanjitrainerita

data class KanjiInfo(
    val id: Int = 0,
    val kanji: String,
    val meaning: String,
    val story: String,
    val words: List<WordInfo> = listOf()
)