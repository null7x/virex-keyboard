package com.virex.core.network

import com.virex.core.model.KeyboardFont
import com.virex.core.model.KeyboardTheme
import com.virex.core.model.RemoteConfig
import retrofit2.http.GET

interface ThemeApi {
    @GET("themes_feed")
    suspend fun themesFeed(): List<KeyboardTheme>

    @GET("remote_config")
    suspend fun remoteConfig(): RemoteConfig

    @GET("fonts")
    suspend fun fonts(): List<KeyboardFont>
}
