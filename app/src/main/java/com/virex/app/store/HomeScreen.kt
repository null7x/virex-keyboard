package com.virex.app.store

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.virex.app.R
import com.virex.app.theme.VirexTheme
import com.virex.core.model.KeyboardTheme

@Composable
fun HomeScreen(
    viewModel: StoreViewModel = hiltViewModel(),
    onThemeClick: (String) -> Unit,
    onFavoritesClick: () -> Unit,
    onProClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF050505), Color(0xFF101322), Color(0xFF0C0620))
                )
            )
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Virex Keyboard",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "RGB EMOJI & THEMES",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color(0xFF98A2FF)
                )
            }
            Row {
                IconButton(onClick = onFavoritesClick) {
                    Icon(Icons.Default.Favorite, contentDescription = "Favorites", tint = Color(0xFFFF2D55))
                }
                if (!state.isPro) {
                    TextButton(onClick = onProClick) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFD700))
                        Text("PRO", color = Color(0xFFFFD700))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Themes Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.themes, key = { it.id }) { theme ->
                ThemeCard(
                    theme = theme,
                    isSelected = theme.id == state.selectedThemeId,
                    isFavorite = state.favorites.contains(theme.id),
                    onClick = { onThemeClick(theme.id) },
                    onApply = { viewModel.applyTheme(theme) },
                    onFavoriteToggle = { viewModel.toggleFavorite(theme.id) }
                )
            }
        }
    }
}

@Composable
private fun ThemeCard(
    theme: KeyboardTheme,
    isSelected: Boolean,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onApply: () -> Unit,
    onFavoriteToggle: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) Color(0xAA2A3555) else Color(0xAA1A1F33))
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Box {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.6f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF3F51FF), Color(0xFFFF2D55)),
                        )
                    )
            )
            IconButton(
                onClick = onFavoriteToggle,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) Color(0xFFFF2D55) else Color(0x55FFFFFF)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = theme.name, color = Color.White, fontWeight = FontWeight.Bold)
        Text(
            text = theme.category.uppercase(),
            color = Color(0xFF8F9BFF),
            style = MaterialTheme.typography.labelSmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onApply,
            modifier = Modifier.fillMaxWidth(),
            enabled = !theme.isPro || isSelected
        ) {
            Text(if (theme.isPro && !isSelected) "Unlock PRO" else if (isSelected) "Applied" else "Apply")
        }
    }
}
