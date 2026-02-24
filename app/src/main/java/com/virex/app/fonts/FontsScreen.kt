package com.virex.app.fonts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.virex.core.model.FontCategory
import com.virex.core.model.FontState
import com.virex.core.model.KeyboardFont

@Composable
fun FontsScreen(
    onBack: () -> Unit,
    viewModel: FontsViewModel = hiltViewModel()
) {
    val fonts by viewModel.fonts.collectAsState(initial = emptyList())
    val fontStates by viewModel.fontStates.collectAsState(initial = emptyMap())
    val currentFontId by viewModel.currentFontId.collectAsState(initial = "roboto")
    val isPro by viewModel.isPro.collectAsState(initial = false)
    
    LaunchedEffect(Unit) {
        viewModel.loadFonts()
    }

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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text(
                text = "Custom Fonts",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Font List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Personalize your keyboard with custom fonts",
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(fonts) { font ->
                FontItem(
                    font = font,
                    state = fontStates[font.id] ?: FontState.NotDownloaded,
                    isActive = font.id == currentFontId,
                    isPro = isPro,
                    onDownload = { viewModel.downloadFont(font) },
                    onApply = { viewModel.applyFont(font.id) },
                    onDelete = { viewModel.deleteFont(font.id) }
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun FontItem(
    font: KeyboardFont,
    state: FontState,
    isActive: Boolean,
    isPro: Boolean,
    onDownload: () -> Unit,
    onApply: () -> Unit,
    onDelete: () -> Unit
) {
    val isLocked = font.isPro && !isPro

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (!isLocked && state is FontState.Downloaded) {
                    Modifier.clickable { onApply() }
                } else {
                    Modifier
                }
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) {
                Color(0xFFFFD700).copy(alpha = 0.15f)
            } else {
                Color(0xFF1A1F2E)
            }
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = font.displayName,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (font.isPro) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = Color(0xFFFFD700),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "PRO",
                                color = Color.Black,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${font.category.name.lowercase().replace("_", " ")} • ${formatFileSize(font.fileSize)}",
                    color = Color.White.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            when {
                isLocked -> {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Locked",
                        tint = Color.White.copy(alpha = 0.3f),
                        modifier = Modifier.size(24.dp)
                    )
                }
                isActive -> {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Active",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(28.dp)
                    )
                }
                state is FontState.Downloaded -> {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(onClick = onDelete) {
                            Text("Delete", color = Color(0xFFFF4444))
                        }
                        Button(
                            onClick = onApply,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFFD700)
                            )
                        ) {
                            Text("Apply", color = Color.Black)
                        }
                    }
                }
                state is FontState.Downloading -> {
                    CircularProgressIndicator(
                        progress = state.progress,
                        modifier = Modifier.size(32.dp),
                        color = Color(0xFFFFD700),
                        trackColor = Color.White.copy(alpha = 0.2f)
                    )
                }
                state is FontState.NotDownloaded -> {
                    IconButton(onClick = onDownload) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Download",
                            tint = Color(0xFFFFD700)
                        )
                    }
                }
                state is FontState.Error -> {
                    TextButton(onClick = onDownload) {
                        Text("Retry", color = Color(0xFFFF4444))
                    }
                }
            }
        }
    }
}

private fun formatFileSize(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        else -> String.format("%.1f MB", bytes / (1024.0 * 1024.0))
    }
}
