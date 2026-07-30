package edu.metrostate.ics342.mediatracker.ui.library

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import edu.metrostate.ics342.mediatracker.data.model.LibraryItem
import edu.metrostate.ics342.mediatracker.data.model.LibraryStatus
import edu.metrostate.ics342.mediatracker.data.model.creatorCredit
import edu.metrostate.ics342.mediatracker.theme.PrimaryContainer
import edu.metrostate.ics342.mediatracker.theme.OnPrimaryContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    onMediaClick: (Int) -> Unit,
    onAddClick: () -> Unit = {},
    onViewPriorities: () -> Unit = {},
    viewModel: LibraryViewModel = viewModel(
        factory = LibraryViewModel.factory(LocalContext.current.applicationContext as Application)
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedStatus by viewModel.filterState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        TopAppBar(
            title = { Text("My Library") },
            actions = {
                IconButton(onClick = onViewPriorities) {
                    Icon(Icons.Filled.Star, contentDescription = "Priorities")
                }
                Button(
                    onClick = onAddClick,
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.padding(end = 12.dp)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Add")
                }
            }
        )

        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            LibraryStatus.values().forEachIndexed { index, status ->
                SegmentedButton(
                    shape = RoundedCornerShape(
                        topStart = if (index == 0) 50.dp else 0.dp,
                        bottomStart = if (index == 0) 50.dp else 0.dp,
                        topEnd = if (index == LibraryStatus.values().lastIndex) 50.dp else 0.dp,
                        bottomEnd = if (index == LibraryStatus.values().lastIndex) 50.dp else 0.dp
                    ),
                    selected = selectedStatus == status,
                    onClick  = { viewModel.updateFilter(status) },
                    label    = { Text(stringResource(status.labelRes)) },
                    icon = {
                        if (selectedStatus == status) {
                            Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    },
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = PrimaryContainer,
                        activeContentColor   = OnPrimaryContainer
                    )
                )
            }
        }

        when (val state = uiState) {
            is LibraryUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is LibraryUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            state.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = { viewModel.loadLibrary() }) {
                            Text("Retry")
                        }
                    }
                }
            }

            is LibraryUiState.Success -> {
                val libraryItems = state.items

                if (libraryItems.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Nothing in '${stringResource(selectedStatus.labelRes)}' yet.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    Text(
                        if (libraryItems.size == 1)
                            stringResource(edu.metrostate.ics342.mediatracker.R.string.library_item_count, libraryItems.size)
                        else
                            stringResource(edu.metrostate.ics342.mediatracker.R.string.library_items_count, libraryItems.size),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style    = MaterialTheme.typography.labelMedium,
                        color    = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(libraryItems, key = { it.mediaId }) { item ->
                            LibraryItemCard(
                                item           = item,
                                onClick        = { onMediaClick(item.mediaId) },
                                onRemove       = { viewModel.removeItem(item.mediaId) },
                                onStatusChange = { newStatus -> viewModel.updateStatus(item.mediaId, newStatus) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LibraryItemCard(
    item: LibraryItem,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    onStatusChange: (LibraryStatus) -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var statusDialogVisible by remember { mutableStateOf(false) }

    if (statusDialogVisible) {
        AlertDialog(
            onDismissRequest = { statusDialogVisible = false },
            title = { Text(stringResource(edu.metrostate.ics342.mediatracker.R.string.action_change_status)) },
            text = {
                Column {
                    LibraryStatus.values().forEach { s ->
                        TextButton(
                            onClick  = { onStatusChange(s); statusDialogVisible = false },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text(stringResource(s.labelRes)) }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { statusDialogVisible = false }) { Text(stringResource(edu.metrostate.ics342.mediatracker.R.string.settings_cancel_button)) }
            }
        )
    }

    Card(
        modifier  = Modifier.fillMaxWidth().clickable { onClick() },
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (item.media.coverUrl != null) {
                    AsyncImage(
                        model             = item.media.coverUrl,
                        contentDescription = item.media.title,
                        contentScale      = ContentScale.Crop,
                        modifier          = Modifier.fillMaxSize()
                    )
                } else {
                    val (bgColor, emoji) = when (item.media.mediaType) {
                        "book"  -> Color(0xFFD8D3F7) to "📖"
                        "movie" -> Color(0xFFF7D3E0) to "🎬"
                        "show"  -> Color(0xFFF7EBD3) to "📺"
                        else    -> MaterialTheme.colorScheme.surfaceVariant to "?"
                    }
                    Surface(color = bgColor, modifier = Modifier.fillMaxSize()) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(emoji, style = MaterialTheme.typography.titleLarge)
                        }
                    }
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(item.media.title, style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold, maxLines = 2)
                Spacer(Modifier.height(2.dp))
                Text(item.media.creatorCredit(LocalContext.current),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(6.dp))
                SuggestionChip(
                    onClick = { statusDialogVisible = true },
                    label   = { Text(stringResource(item.status.labelRes),
                        style = MaterialTheme.typography.labelSmall) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = Color(0xFFF3F1F7)
                    ),
                    border = SuggestionChipDefaults.suggestionChipBorder(
                        enabled = true,
                        borderColor = Color(0xFFE0DDE8)
                    )
                )
            }

            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(Icons.Outlined.MoreVert, stringResource(edu.metrostate.ics342.mediatracker.R.string.action_more_options))
                }
                DropdownMenu(
                    expanded         = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text    = { Text(stringResource(edu.metrostate.ics342.mediatracker.R.string.action_change_status)) },
                        onClick = { menuExpanded = false; statusDialogVisible = true }
                    )
                    DropdownMenuItem(
                        text    = { Text(stringResource(edu.metrostate.ics342.mediatracker.R.string.action_remove_from_library),
                            color = MaterialTheme.colorScheme.error) },
                        onClick = { menuExpanded = false; onRemove() }
                    )
                }
            }
        }
    }
}