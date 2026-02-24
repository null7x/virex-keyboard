package com.virex.keyboard

object SimpleOfflineSuggestions {
    
    private val englishDict = mapOf(
        "hel" to listOf("hello", "help", "held"),
        "hell" to listOf("hello", "hello"),
        "th" to listOf("the", "that", "this"),
        "tha" to listOf("that", "than", "thanks"),
        "wh" to listOf("what", "when", "where"),
        "wha" to listOf("what", "whatever"),
        "ho" to listOf("how", "home", "hope"),
        "how" to listOf("how", "however"),
        "i" to listOf("I", "I'm", "I'll"),
        "yo" to listOf("you", "your", "you're"),
        "you" to listOf("you", "your", "you're"),
        "we" to listOf("we", "well", "were"),
        "ar" to listOf("are", "area", "around"),
        "ca" to listOf("can", "call", "came"),
        "wi" to listOf("with", "will", "wish"),
        "ha" to listOf("have", "has", "had"),
        "me" to listOf("me", "mean", "meet"),
        "an" to listOf("and", "an", "any"),
        "fr" to listOf("from", "free", "friend"),
        "go" to listOf("go", "good", "got"),
        "ju" to listOf("just", "jump"),
        "li" to listOf("like", "life", "little"),
        "ma" to listOf("make", "may", "many"),
        "ne" to listOf("new", "need", "next"),
        "on" to listOf("one", "only", "once"),
        "pe" to listOf("people", "person", "perfect"),
        "re" to listOf("really", "real", "read"),
        "so" to listOf("some", "something", "someone"),
        "ti" to listOf("time", "think", "thing"),
        "us" to listOf("use", "used", "using"),
        "ve" to listOf("very", "video", "view"),
        "wo" to listOf("work", "world", "would"),
        "ye" to listOf("yes", "year", "yet")
    )
    
    private val russianDict = mapOf(
        "при" to listOf("привет", "пример", "принять"),
        "прив" to listOf("привет", "привыкать"),
        "как" to listOf("как", "какой", "каким"),
        "что" to listOf("что", "чтобы", "чтоб"),
        "чт" to listOf("что", "чтобы"),
        "это" to listOf("это", "этого", "этот"),
        "эт" to listOf("это", "этот"),
        "да" to listOf("да", "давай", "даже"),
        "не" to listOf("не", "нет", "него"),
        "на" to listOf("на", "над", "нас"),
        "по" to listOf("по", "пока", "после"),
        "от" to listOf("от", "ответ", "отлично"),
        "мо" to listOf("может", "можно", "мой"),
        "те" to listOf("тебе", "тебя", "теперь"),
        "сп" to listOf("спасибо", "спокойной"),
        "ес" to listOf("если", "есть"),
        "вс" to listOf("все", "всё", "всего"),
        "бу" to listOf("будет", "буду", "будь"),
        "то" to listOf("только", "тоже", "точно"),
        "ко" to listOf("когда", "кого", "конечно"),
        "ме" to listOf("меня", "мне", "между"),
        "та" to listOf("так", "там", "такой"),
        "ну" to listOf("ну", "нужно"),
        "се" to listOf("себя", "сегодня", "сейчас"),
        "де" to listOf("делать", "день", "дело"),
        "ра" to listOf("раз", "работа", "разве")
    )
    
    fun getSuggestions(prefix: String, isEnglish: Boolean): List<String> {
        if (prefix.isEmpty() || prefix.length < 2) return emptyList()
        
        val dict = if (isEnglish) englishDict else russianDict
        val normalized = prefix.lowercase()
        
        return dict[normalized] ?: emptyList()
    }
}
