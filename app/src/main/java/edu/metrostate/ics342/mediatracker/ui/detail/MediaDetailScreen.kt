package edu.metrostate.ics342.mediatracker.ui.detail

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.metrostate.ics342.mediatracker.R
import edu.metrostate.ics342.mediatracker.data.model.Media
import edu.metrostate.ics342.mediatracker.data.model.Review
import edu.metrostate.ics342.mediatracker.data.model.creatorCredit
import edu.metrostate.ics342.mediatracker.theme.MovieContainer
import edu.metrostate.ics342.mediatracker.theme.OnMovieContainer


// ── STUB — Students build this in Week 7 ─────────────────────────────────────
//
// Week 7 task: Build the Media Detail screen.
//   1. Receive mediaId from the navigation argument (typed Int — see NavGraph).
//   2. Call GET /media/{mediaId} to load full details.
//   3. Display: cover image, title, creator credit, metadata grid, genre chips,
//      average rating, description, and a library status control.
//   4. Display the reviews list from GET /reviews?mediaId={id}.
//   5. Handle loading and error states (full-screen — no half-built screens).
@Composable
fun MediaDetailScreen(
    mediaId: Int,
    onNavigateBack: () -> Unit,
    onWriteReview: (Int) -> Unit,
    viewModel: MediaDetailViewModel = viewModel()
) {
    LaunchedEffect(mediaId) { viewModel.setMediaId(mediaId) }

    val media   by viewModel.media.collectAsState()
    val reviews by viewModel.reviews.collectAsState()

    if (media == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    MediaDetailContent(
        media          = media!!,
        reviews        = reviews,
        onNavigateBack = onNavigateBack,
        onWriteReview  = { onWriteReview(mediaId) }
    )
}

@Composable
private fun MediaDetailContent(
    media: Media,
    reviews: List<Review>,
    onNavigateBack: () -> Unit,
    onWriteReview: () -> Unit
) {
    val context = LocalContext.current

    val containerColor = when (media.mediaType) {
        "book"  -> MaterialTheme.colorScheme.primaryContainer
        "movie" -> MovieContainer
        else    -> MaterialTheme.colorScheme.secondaryContainer
    }
    val iconTint = when (media.mediaType) {
        "book"  -> MaterialTheme.colorScheme.onPrimaryContainer
        "movie" -> OnMovieContainer
        else    -> MaterialTheme.colorScheme.secondary
    }
    val iconRes = when (media.mediaType) {
        "book"  -> R.drawable.menu_book_24px
        "movie" -> R.drawable.movie_24px
        else    -> R.drawable.tv_24px
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        }

        // Cover art
        Box(
            modifier = Modifier
                .size(160.dp, 200.dp)
                .align(Alignment.CenterHorizontally)
                .clip(RoundedCornerShape(16.dp))
                .background(containerColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = iconTint
            )
        }

        Spacer(Modifier.height(16.dp))

        // Title & creator
        Text(
            text = media.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Text(
            text = media.creatorCredit(context),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(8.dp))

        // Star rating
        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(5) { i ->
                Text(
                    text = if (i < media.averageRating.toInt()) "★" else "☆",
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Spacer(Modifier.width(4.dp))
            Text(
                text = "${"%.1f".format(media.averageRating)} (${media.ratingCount})",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(Modifier.height(16.dp))

        // Action buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {},
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(50)
            ) {
                Text("+ Want To")
            }
            OutlinedButton(
                onClick = {},
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(50)
            ) {
                Icon(
                    Icons.Outlined.FavoriteBorder,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text("Save")
            }
        }

        Spacer(Modifier.height(20.dp))

        // About
        Text(
            text = "ABOUT",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "A story about ${media.title} by ${media.creatorCredit(context)}...",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(16.dp))

        // Metadata grid
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetadataCard("YEAR", media.publishedYear?.toString() ?: "—", Modifier.weight(1f))
            if (media.mediaType == "book") {
                MetadataCard("PAGES", media.pageCount?.toString() ?: "—", Modifier.weight(1f))
            }
            MetadataCard("GENRE", media.genres.firstOrNull() ?: "—", Modifier.weight(1f))
            if (media.network != null) {
                MetadataCard("NETWORK", media.network, Modifier.weight(1f))
            }
        }

        Spacer(Modifier.height(20.dp))

        // Reviews header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "REVIEWS (${reviews.size})",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.weight(1f))
            TextButton(onClick = onWriteReview) {
                Text("+ Write Review", color = MaterialTheme.colorScheme.primary)
            }
        }

        // Reviews list
        reviews.forEach { review ->
            ReviewCard(review = review)
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun MetadataCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ReviewCard(review: Review) {
    val displayName = review.user?.username ?: review.userId
    val initial = displayName.first().uppercaseChar()
    val avatarColor = MaterialTheme.colorScheme.primary

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(avatarColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initial.toString(),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "@$displayName",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = review.createdAt,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(6.dp))
            Row {
                repeat(5) { i ->
                    Text(
                        text = if (i < review.rating) "★" else "☆",
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = review.reviewText ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}