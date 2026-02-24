package com.virex.core.emoji

data class EmojiItem(
    val unicode: String,
    val category: String,
    val keywords: List<String> = emptyList()
)

object EmojiData {
    val smileys = listOf(
        EmojiItem("😀", "smileys"), EmojiItem("😁", "smileys"), EmojiItem("😂", "smileys"),
        EmojiItem("🤣", "smileys"), EmojiItem("😃", "smileys"), EmojiItem("😄", "smileys"),
        EmojiItem("😅", "smileys"), EmojiItem("😆", "smileys"), EmojiItem("😉", "smileys"),
        EmojiItem("😊", "smileys"), EmojiItem("😋", "smileys"), EmojiItem("😎", "smileys"),
        EmojiItem("😍", "smileys"), EmojiItem("😘", "smileys"), EmojiItem("🥰", "smileys"),
        EmojiItem("😗", "smileys"), EmojiItem("😙", "smileys"), EmojiItem("😚", "smileys")
    )

    val hearts = listOf(
        EmojiItem("❤️", "hearts"), EmojiItem("🧡", "hearts"), EmojiItem("💛", "hearts"),
        EmojiItem("💚", "hearts"), EmojiItem("💙", "hearts"), EmojiItem("💜", "hearts"),
        EmojiItem("🖤", "hearts"), EmojiItem("🤍", "hearts"), EmojiItem("🤎", "hearts"),
        EmojiItem("💔", "hearts"), EmojiItem("❣️", "hearts"), EmojiItem("💕", "hearts")
    )

    val gestures = listOf(
        EmojiItem("👍", "gestures"), EmojiItem("👎", "gestures"), EmojiItem("👌", "gestures"),
        EmojiItem("✌️", "gestures"), EmojiItem("🤞", "gestures"), EmojiItem("🤟", "gestures"),
        EmojiItem("🤘", "gestures"), EmojiItem("🤙", "gestures"), EmojiItem("👈", "gestures"),
        EmojiItem("👉", "gestures"), EmojiItem("👆", "gestures"), EmojiItem("👇", "gestures"),
        EmojiItem("☝️", "gestures"), EmojiItem("✋", "gestures"), EmojiItem("🤚", "gestures"),
        EmojiItem("🖐️", "gestures"), EmojiItem("🖖", "gestures"), EmojiItem("👋", "gestures")
    )

    val popular = listOf(
        EmojiItem("🔥", "popular"), EmojiItem("💯", "popular"), EmojiItem("✨", "popular"),
        EmojiItem("⭐", "popular"), EmojiItem("💪", "popular"), EmojiItem("🙏", "popular"),
        EmojiItem("🎉", "popular"), EmojiItem("🎊", "popular"), EmojiItem("🎁", "popular"),
        EmojiItem("🎈", "popular"), EmojiItem("💖", "popular"), EmojiItem("😭", "popular")
    )

    fun getAllEmojis(): List<EmojiItem> = smileys + hearts + gestures + popular
}
