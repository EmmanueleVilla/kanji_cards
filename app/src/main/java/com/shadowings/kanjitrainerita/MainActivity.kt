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
import androidx.lifecycle.ViewModelProvider
import com.shadowings.kanjitrainerita.ui.theme.KanjiTrainerITATheme


class MainActivity : ComponentActivity() {

    private var kanjiList: List<KanjiInfo> by mutableStateOf(listOf())
    private var currentKanji: KanjiInfo? by mutableStateOf(null)

    private val viewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.kanjiLiveData.observeForever {
            kanjiList = it
            Log.e("KanjiTrainerITA", "Kanji list size: ${kanjiList.size}")
        }

        viewModel.currentKanji.observeForever {
            currentKanji = it
        }

        setContent {
            KanjiTrainerITATheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainComposable(
                        kanjiList,
                        currentKanji,
                        viewModel::nextKanji
                    )
                }
            }
        }
    }
}