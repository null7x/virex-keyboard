package com.virex.core.model

data class RemoteConfig(
    val showPaywall: Boolean = true,
    val nativeAdsInterval: Int = 6,
    val appOpenEnabled: Boolean = true
)
