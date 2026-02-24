package com.virex.app.store

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material3.OutlinedButton
import com.virex.app.R
import com.virex.app.share.ShareHelper

@Composable
fun ThemePreviewScreen(
    themeId: String,
    viewModel: StoreViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onApply: () -> Unit
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val theme = remember(state.themes, themeId) {
        state.themes.find { it.id == themeId }
    }
    val viewStartTime = remember { System.currentTimeMillis() }

    DisposableEffect(themeId) {
        onDispose {
            val duration = System.currentTimeMillis() - viewStartTime
            viewModel.recordThemeView(themeId, duration)
        }
    }

    if (theme == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF050505))
                .padding(16.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text("Theme not found", color = Color.White)
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF050505), Color(0xFF101322), Color(0xFF0C0620))
                )
            )
            .padding(16.dp)
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = painterResource(id = R.drawable.theme_preview_placeholder),
            contentDescription = theme.name,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.4f)
                .clip(RoundedCornerShape(24.dp))
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = theme.name,
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = theme.category.uppercase(),
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF98A2FF)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (theme.isPro && !state.isPro) {
            Text(
                text = "⭐ PRO Theme",
                color = Color(0xFFFFD700),
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Column(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = {
                    viewModel.applyTheme(theme)
                    onApply()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (theme.isPro && !state.isPro) "Unlock PRO" else "Apply Theme")
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedButton(
                onClick = {
                    ShareHelper.shareTheme(context, theme.id, theme.name)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Share Theme 🔥")
            }
        }
    }
}
