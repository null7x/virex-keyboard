package com.virex.keyboard

import android.content.BroadcastReceiver
import android.inputmethodservice.InputMethodService
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.View
import com.virex.core.ThemeBroadcaster
import com.virex.core.emoji.EmojiManager
import com.virex.core.model.KeyboardTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class VirexKeyboardService : InputMethodService(), VirexKeyboardView.KeyListener {

    private var isShiftEnabled = false
    private var currentLanguage = "en" // "en" or "ru"
    private var themeReceiver: BroadcastReceiver? = null
    private var emojiPanelVisible = false
    
    private lateinit var emojiPanel: EmojiPanelView
    private lateinit var suggestionBar: SuggestionBar
    private lateinit var keyboardView: VirexKeyboardView
    private lateinit var emojiManager: EmojiManager
    
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    private val deleteHandler = Handler(Looper.getMainLooper())
    private val deleteRunnable = object : Runnable {
        override fun run() {
            currentInputConnection?.deleteSurroundingText(1, 0)
            deleteHandler.postDelayed(this, 50)
        }
    }

    override fun onCreateInputView(): View {
        return layoutInflater.inflate(R.layout.keyboard_view, null).also { rootView ->
            keyboardView = rootView.findViewById(R.id.virexKeyboardView)
            emojiPanel = rootView.findViewById(R.id.emojiPanel)
            suggestionBar = rootView.findViewById(R.id.suggestionBar)
            
            keyboardView.apply {
                setKeyListener(this@VirexKeyboardService)
                setLanguage(currentLanguage)
            }
            
            emojiPanel.setEmojiListener(object : EmojiPanelView.EmojiListener {
                override fun onEmojiSelected(emoji: String) {
                    currentInputConnection?.commitText(emoji, 1)
                    serviceScope.launch {
                        emojiManager.addRecentEmoji(emoji)
                    }
                    hideEmojiPanel()
                }
                
                override fun onClosePanel() {
                    hideEmojiPanel()
                }
            })
            
            suggestionBar.setSuggestionListener(object : SuggestionBar.SuggestionListener {
                override fun onSuggestionSelected(suggestion: String) {
                    val textBefore = currentInputConnection.getTextBeforeCursor(50, 0)
                    val lastSpace = textBefore?.lastIndexOf(' ') ?: -1
                    val charsToDelete = if (lastSpace >= 0) {
                        (textBefore?.length ?: 0) - lastSpace - 1
                    } else {
                        textBefore?.length ?: 0
                    }
                    currentInputConnection.deleteSurroundingText(charsToDelete, 0)
                    currentInputConnection.commitText("$suggestion ", 1)
                    updateSuggestions("")
                }
            })
        }
    }

    override fun onCreate() {
        super.onCreate()
        emojiManager = EmojiManager(this)
        themeReceiver = ThemeBroadcaster.createReceiver { _, keyColor, fontColor, background ->
            if (::keyboardView.isInitialized) {
                keyboardView.applyTheme(
                    KeyboardTheme("", "", "", background, keyColor, fontColor, false)
                )
            }
        }
        registerReceiver(themeReceiver, ThemeBroadcaster.getIntentFilter(), RECEIVER_NOT_EXPORTED)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        themeReceiver?.let { unregisterReceiver(it) }
    }

    override fun onKeyPress(value: String) {
        when (value) {
            "⌫" -> {
                currentInputConnection.deleteSurroundingText(1, 0)
                val currentWord = getCurrentWord()
                updateSuggestions(currentWord)
            }
            "↵" -> {
                currentInputConnection.commitText("\n", 1)
                updateSuggestions("")
            }
            "⇧" -> {
                isShiftEnabled = !isShiftEnabled
            }
            "🌐" -> {
                currentLanguage = if (currentLanguage == "en") "ru" else "en"
                keyboardView.setLanguage(currentLanguage)
                updateSuggestions("")
            }
            "😀" -> {
                toggleEmojiPanel()
                return
            }
            " " -> {
                currentInputConnection.commitText(" ", 1)
                updateSuggestions("")
            }
            else -> {
                val out = if (isShiftEnabled) value.uppercase() else value
                currentInputConnection.commitText(out, 1)
                if (isShiftEnabled) isShiftEnabled = false
                
                val currentWord = getCurrentWord()
                updateSuggestions(currentWord)
            }
        }
        playClick()
        vibrate()
    }

    override fun onKeyLongPress(value: String) {
        if (value == "⌫") {
            deleteHandler.post(deleteRunnable)
        }
    }

    override fun onKeyRelease(value: String) {
        if (value == "⌫") {
            deleteHandler.removeCallbacks(deleteRunnable)
        }
    }

    private fun playClick() {
        @Suppress("DEPRECATION")
        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK)
    }

    private fun vibrate() {
        val vibrator = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            (getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }
        vibrator.vibrate(VibrationEffect.createOneShot(10, VibrationEffect.DEFAULT_AMPLITUDE))
    }
    
    private fun toggleEmojiPanel() {
        emojiPanelVisible = !emojiPanelVisible
        if (emojiPanelVisible) {
            emojiPanel.visibility = View.VISIBLE
            keyboardView.visibility = View.GONE
            suggestionBar.visibility = View.GONE
        } else {
            emojiPanel.visibility = View.GONE
            keyboardView.visibility = View.VISIBLE
            suggestionBar.visibility = View.VISIBLE
        }
    }
    
    private fun hideEmojiPanel() {
        emojiPanelVisible = false
        emojiPanel.visibility = View.GONE
        keyboardView.visibility = View.VISIBLE
        suggestionBar.visibility = View.VISIBLE
    }
    
    private fun getCurrentWord(): String {
        val textBefore = currentInputConnection?.getTextBeforeCursor(50, 0) ?: return ""
        val words = textBefore.split(" ")
        return words.lastOrNull() ?: ""
    }
    
    private fun updateSuggestions(currentWord: String) {
        if (currentWord.isEmpty() || currentWord.length < 2) {
            suggestionBar.setSuggestions(emptyList())
            return
        }
        
        val suggestions = SimpleOfflineSuggestions.getSuggestions(
            currentWord,
            currentLanguage == "en"
        )
        suggestionBar.setSuggestions(suggestions)
    }
}
