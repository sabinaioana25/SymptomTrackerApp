package com.example.recoverylogger.presentation.entry

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun EntryLogScreen(viewModel: EntryViewModel, onBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()

    EntryLogContent(
        uiState = uiState,
        onRetry = viewModel::loadEntries,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryLogContent(
    uiState: EntryUiState,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    val backgroundColor = when {
        uiState.isLoading -> Color(0xFFF5F5F5)
        uiState.error != null -> Color(0xFFFFF3F3)
        uiState.entries.isEmpty() -> Color(0xFFF0F8FF)
        else -> Color.White
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Entries") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←", style = MaterialTheme.typography.titleLarge)
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(backgroundColor)
        ) {
            when {
                uiState.isLoading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
                uiState.error != null -> EntryErrorView(
                    message = uiState.error,
                    onRetry = onRetry,
                    modifier = Modifier.align(Alignment.Center)
                )
                uiState.entries.isEmpty() -> EntryEmptyView(
                    modifier = Modifier.align(Alignment.Center)
                )
                else -> EntryList(entries = uiState.entries)
            }
        }
    }
}

@Composable
private fun EntryList(entries: List<EntryUiModel>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(entries, key = { it.id }) { entry ->
            EntryCard(entry = entry)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun EntryCard(entry: EntryUiModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = entry.displayDate,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(
                            color = Color(0xFFFF9800),
                            shape = CircleShape
                        )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            entry.heartburnScore?.let { score ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Heartburn", style = MaterialTheme.typography.bodySmall)
                    Text(
                        text = "$score / 10",
                        style = MaterialTheme.typography.bodySmall,
                        color = heartburnColour(score)
                    )
                }
            }

            entry.bloatingLevel?.let { level ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Bloating", style = MaterialTheme.typography.bodySmall)
                    Text(text = level, style = MaterialTheme.typography.bodySmall)
                }
            }

            if (entry.hasDifficultySwallowing == true) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "⚠ Difficulty swallowing reported",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFE65100)
                )
            }
        }
    }
}

private fun heartburnColour(score: Int): Color = when {
    score <= 3 -> Color(0xFF2E7D32)
    score <= 6 -> Color(0xFFF9A825)
    else -> Color(0xFFC62828)
}

@Composable
private fun EntryErrorView(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Something went wrong",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFFC62828)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) { Text("Try again") }
    }
}

@Composable
private fun EntryEmptyView(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "No entries yet", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Start logging your recovery to track your progress",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
