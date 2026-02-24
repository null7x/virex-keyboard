package com.virex.core.font

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import com.virex.core.model.FontState
import com.virex.core.model.KeyboardFont
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages custom font downloads and loading for keyboard
 */
@Singleton
class FontManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "FontManager"
        private const val FONTS_DIR = "keyboard_fonts"
        private const val DEFAULT_FONT_ID = "roboto"
    }

    private val fontsDir: File by lazy {
        File(context.filesDir, FONTS_DIR).apply { mkdirs() }
    }

    // Cache of loaded Typeface objects
    private val typefaceCache = mutableMapOf<String, Typeface>()

    // Current active font ID
    private val _currentFontId = MutableStateFlow(DEFAULT_FONT_ID)
    val currentFontId: StateFlow<String> = _currentFontId.asStateFlow()

    // Font download states
    private val _fontStates = MutableStateFlow<Map<String, FontState>>(emptyMap())
    val fontStates: StateFlow<Map<String, FontState>> = _fontStates.asStateFlow()

    /**
     * Get Typeface for given font ID
     * Returns system default if font not downloaded or loading fails
     */
    fun getTypeface(fontId: String): Typeface {
        // Check cache
        typefaceCache[fontId]?.let { return it }

        // Check if font file exists
        val fontFile = File(fontsDir, "$fontId.ttf")
        if (!fontFile.exists()) {
            Log.w(TAG, "Font file not found: $fontId, using default")
            return Typeface.DEFAULT
        }

        // Load font
        return try {
            val typeface = Typeface.createFromFile(fontFile)
            typefaceCache[fontId] = typeface
            typeface
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load font: $fontId", e)
            Typeface.DEFAULT
        }
    }

    /**
     * Get current active Typeface
     */
    fun getCurrentTypeface(): Typeface {
        return getTypeface(_currentFontId.value)
    }

    /**
     * Set active font
     */
    fun setCurrentFont(fontId: String) {
        _currentFontId.value = fontId
    }

    /**
     * Check if font is downloaded
     */
    fun isFontDownloaded(fontId: String): Boolean {
        val fontFile = File(fontsDir, "$fontId.ttf")
        return fontFile.exists() && fontFile.length() > 0
    }

    /**
     * Download font from URL
     */
    suspend fun downloadFont(font: KeyboardFont): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Update state to downloading
            updateFontState(font.id, FontState.Downloading(0f))

            val fontFile = File(fontsDir, "${font.id}.ttf")
            val url = URL(font.fontUrl)
            val connection = url.openConnection() as HttpURLConnection
            
            connection.connect()
            
            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                updateFontState(font.id, FontState.Error("Download failed: ${connection.responseCode}"))
                return@withContext Result.failure(Exception("HTTP ${connection.responseCode}"))
            }

            val fileLength = connection.contentLength
            
            connection.inputStream.use { input ->
                FileOutputStream(fontFile).use { output ->
                    val buffer = ByteArray(8192)
                    var totalRead = 0L
                    var bytesRead: Int

                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        totalRead += bytesRead

                        // Update progress
                        if (fileLength > 0) {
                            val progress = totalRead.toFloat() / fileLength
                            updateFontState(font.id, FontState.Downloading(progress))
                        }
                    }
                }
            }

            // Clear cache to force reload
            typefaceCache.remove(font.id)
            
            // Update state to downloaded
            updateFontState(font.id, FontState.Downloaded)
            
            Log.d(TAG, "Font downloaded successfully: ${font.id}")
            Result.success(Unit)
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to download font: ${font.id}", e)
            updateFontState(font.id, FontState.Error(e.message ?: "Download failed"))
            Result.failure(e)
        }
    }

    /**
     * Delete font file
     */
    fun deleteFont(fontId: String): Boolean {
        val fontFile = File(fontsDir, "$fontId.ttf")
        typefaceCache.remove(fontId)
        updateFontState(fontId, FontState.NotDownloaded)
        return fontFile.delete()
    }

    /**
     * Get all downloaded font IDs
     */
    fun getDownloadedFontIds(): List<String> {
        return fontsDir.listFiles()
            ?.filter { it.extension == "ttf" }
            ?.map { it.nameWithoutExtension }
            ?: emptyList()
    }

    /**
     * Get font file size
     */
    fun getFontFileSize(fontId: String): Long {
        val fontFile = File(fontsDir, "$fontId.ttf")
        return if (fontFile.exists()) fontFile.length() else 0
    }

    /**
     * Clear all cached typefaces (e.g., on low memory)
     */
    fun clearCache() {
        typefaceCache.clear()
    }

    private fun updateFontState(fontId: String, state: FontState) {
        val currentStates = _fontStates.value.toMutableMap()
        currentStates[fontId] = state
        _fontStates.value = currentStates
    }

    /**
     * Initialize font states for all fonts
     */
    fun initializeFontStates(fonts: List<KeyboardFont>) {
        val states = fonts.associate { font ->
            font.id to if (isFontDownloaded(font.id)) {
                FontState.Downloaded
            } else {
                FontState.NotDownloaded
            }
        }
        _fontStates.value = states
    }
}
