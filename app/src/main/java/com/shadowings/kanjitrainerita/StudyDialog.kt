package com.shadowings.kanjitrainerita

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun StudyDialogPreview() {
    StudyDialog(
        onDismissRequest = {},
        onConfirmation = {},
        kanjis = listOf(),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    kanjis: List<KanjiInfo>,
) {
    val prefManager = PreferencesManager(LocalContext.current)
    var selections by rememberSaveable {
        mutableStateOf(listOf(true, false, false, false, false))
    }
    var cards by rememberSaveable {
        mutableStateOf(true)
    }
    var draw by rememberSaveable {
        mutableStateOf(true)
    }
    var filteredList = kanjis.filter { kanji ->
        selections[5 - kanji.jlptLevel]
    }.sortedBy { k -> k.happiness }
        .take(20)
        .shuffled()

    AlertDialog(
        icon = {
            Icon(Icons.Default.Settings, contentDescription = "Example Icon")
        },
        title = {
            Column {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Livelli",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    items(5) {
                        FilterChip(
                            selected = selections[it],
                            onClick = {
                                selections = selections.mapIndexed { index, value ->
                                    if (index == it) {
                                        !value
                                    } else {
                                        value
                                    }
                                }
                                filteredList = kanjis.filter { kanji ->
                                    selections[5 - kanji.jlptLevel]
                                }.sortedBy { k -> k.happiness }
                                    .take(20)
                                    .shuffled()
                            },
                            label = { Text(text = "N${5 - it}") })
                        Spacer(modifier = Modifier.padding(4.dp))
                    }
                }
                Spacer(modifier = Modifier.padding(8.dp))
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Modalità",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    FilterChip(
                        selected = cards,
                        onClick = {
                            cards = !cards
                        },
                        label = { Text(text = "Carte") })
                    Spacer(modifier = Modifier.padding(4.dp))
                    FilterChip(
                        selected = draw,
                        onClick = {
                            draw = !draw
                        },
                        label = { Text(text = "Disegno") })
                }
                Spacer(modifier = Modifier.padding(8.dp))
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = "Studierai questi kanji:",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
                LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    columns = GridCells.Fixed(5),
                    horizontalArrangement = Arrangement.Center
                ) {
                    items(filteredList.size) { index ->
                        Text(
                            text = filteredList[index].kanji,
                            textAlign = TextAlign.Center
                        )
                        Spacer(
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            }
        },
        text = {
            Text(text = "")
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                enabled = filteredList.isNotEmpty() && (cards || draw),
                onClick = {
                    prefManager.putKanjiIds(
                        filteredList.map { it.id }
                    )
                    onConfirmation()
                }
            ) {
                Text("Inizia")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Annulla")
            }
        }
    )
}
