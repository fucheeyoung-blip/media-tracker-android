package edu.metrostate.ics342.mediatracker.ui.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import edu.metrostate.ics342.mediatracker.data.model.Priority

private enum class PriorityFilter(val label: String, val level: Int?) {
    ALL("All", null),
    HIGH("High", 1),
    MEDIUM("Medium", 2),
    LOW("Low", 3)
}

private fun priorityLabel(level: Int): String = when (level) {
    1 -> "High Priority"
    2 -> "Medium Priority"
    else -> "Low Priority"
}

private fun priorityColor(level: Int): Color = when (level) {
    1 -> Color(0xFFE53935) // red
    2 -> Color(0xFFFB8C00) // orange
    else -> Color(0xFF43A047) // green
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriorityScreen(
    viewModel: PriorityViewModel,
    onBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    var selectedFilter by remember { mutableStateOf(PriorityFilter.ALL) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Priorities", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* future: sort/filter settings */ }) {
                        Icon(Icons.Filled.Tune, contentDescription = "Options")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PriorityFilter.values().forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter.label) },
                        leadingIcon = if (filter.level != null) {
                            {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                )
                            }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors()
                    )
                }
            }

            when (val state = uiState) {
                is PriorityUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is PriorityUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                is PriorityUiState.Success -> {
                    val filtered = state.items
                        .filter { selectedFilter.level == null || it.priority == selectedFilter.level }
                        .sortedBy { it.orderIndex }

                    if (filtered.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize().padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No priorities set — mark a \"Want To\" item as a priority to see it here.",
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        Text(
                            text = "Drag to reorder",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall
                        )

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filtered, key = { it.mediaId }) { item ->
                                PriorityListItem(item = item)
                            }
                        }
                    }
                }
            }
        }
    }

    // TODO(Week 2): surface errorMessage via Snackbar once a SnackbarHostState
    // is wired up (mirrors how LibraryScreen likely surfaces its errorMessage).
}

@Composable
private fun PriorityListItem(item: Priority) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Drag handle placeholder — becomes a real drag target in Week 2
            // (LazyColumn + pointerInput/detectDragGestures, or the `reorderable` lib).
            Icon(
                imageVector = Icons.Filled.DragHandle,
                contentDescription = "Drag to reorder",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.size(12.dp))

            // Placeholder for media artwork/icon — swap for your existing
            // media thumbnail Composable if you have one (e.g. from LibraryScreen).
            Box(
                modifier = Modifier
                    .size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                // Replace with AsyncImage/media icon as used elsewhere in your app
            }

            Spacer(modifier = Modifier.size(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.media.title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(4.dp))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = priorityColor(item.priority).copy(alpha = 0.15f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = priorityLabel(item.priority),
                        color = priorityColor(item.priority),
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                val timeText = item.estimatedTimeHours?.let { "Est. $it hours" }
                val notesText = item.notes?.let { "\"$it\"" }
                val subtitle = listOfNotNull(timeText, notesText).joinToString(" · ")

                if (subtitle.isNotEmpty()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}