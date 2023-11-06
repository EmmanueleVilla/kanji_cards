package com.shadowings.kanjitrainerita

data class KanjiInfo(
    val id: Int,
    val jlptLevel: Int,
    val kanji: String,
    val meaning: String,
    val story: String,
    val words: List<WordInfo> = listOf(),
    val seenCount: Int
)