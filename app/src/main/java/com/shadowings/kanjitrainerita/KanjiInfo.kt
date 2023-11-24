package com.shadowings.kanjitrainerita

data class KanjiInfo(
    val id: Int,
    val jlptLevel: Int,
    val kanji: String,
    val meaning: String,
    val story: String,
    val words: List<WordInfo> = listOf(),
    val happiness: Int,
    val searchString: String = "$kanji $meaning ${words.joinToString { it.kana + it.kanji + it.meaning }}",
    val mode: TrainingMode = TrainingMode.Card
)