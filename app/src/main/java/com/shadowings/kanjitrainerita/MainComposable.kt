package com.shadowings.kanjitrainerita

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MainComposable(
    kanjis: List<KanjiInfo>,
    currentKanji: KanjiInfo?,
    nextKanji: () -> Unit
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeComposable(kanjis)
        }
    }
}