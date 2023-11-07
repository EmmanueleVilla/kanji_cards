package com.shadowings.kanjitrainerita

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shadowings.kanjitrainerita.ui.theme.KanjiTrainerITATheme

@Preview
@Composable
fun KanjiListItemPreview() {
    KanjiTrainerITATheme {
        KanjiListItem(
            kanji = KanjiInfo(
                id = 0,
                jlptLevel = 5,
                kanji = "乳",
                meaning = "Latte",
                story = "Con le unghie 爪 i bambini 子 si attaccano乚 al biberon ",
                words = listOf(
                    WordInfo(
                        kana = "ぎゅうにゅう",
                        kanji = "牛乳",
                        meaning = "latte vaccino"
                    ),
                    WordInfo(
                        kana = "ぼにゅう",
                        kanji = "母乳",
                        meaning = "latte materno"
                    ),
                ),
                happiness = 0

            )
        )
    }
}

@Composable
fun KanjiListItem(kanji: KanjiInfo) {
    val isDark = isSystemInDarkTheme()
    Column {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .weight(1f),
                text = kanji.kanji,
                fontSize = 40.sp
            )
            Column(
                modifier = Modifier
                    .weight(6f)
                    .padding(start = 16.dp)
            ) {
                Text(kanji.meaning.capitalize(Locale.current), fontSize = 20.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(kanji.story.capitalize(Locale.current), fontSize = 14.sp)
            }
            Column(
                modifier = Modifier
                    .weight(3f)
                    .padding(start = 16.dp),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "N${kanji.jlptLevel}",
                    fontSize = 20.sp,
                    textAlign = TextAlign.End
                )
            }
        }
        Divider()
    }
}