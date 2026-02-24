package com.virex.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.virex.ads.VkAdsManager
import com.virex.app.config.RemoteConfigManager
import com.virex.app.deeplink.DeepLinkHandler
import com.virex.app.navigation.AppNavigation
import com.virex.app.navigation.Screen
import com.virex.app.theme.VirexTheme
import com.virex.billing.BillingGateway
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var adsManager: VkAdsManager
    
    @Inject
    lateinit var billingGateway: BillingGateway
    
    @Inject
    lateinit var remoteConfigManager: RemoteConfigManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize monetization with error safety
        try {
            adsManager.initialize()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        try {
            billingGateway.initialize()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        // Fetch remote config
        lifecycleScope.launch {
            try {
                remoteConfigManager.fetchConfig()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        
        // Handle deep link
        val deepLink = DeepLinkHandler.handleIntent(intent)
        
        setContent {
            VirexTheme {
                var startRoute by remember { mutableStateOf<String?>(null) }
                
                LaunchedEffect(deepLink) {
                    startRoute = when (deepLink?.type) {
                        DeepLinkHandler.DeepLink.Type.THEME_PREVIEW -> {
                            "preview/${deepLink.themeId}"
                        }
                        else -> null
                    }
                }
                
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation(startDestination = startRoute ?: Screen.Home.route)
                }
            }
        }
        
        // Show app open ad after brief delay (retention)
        lifecycleScope.launch {
            kotlinx.coroutines.delay(1500)
            if (remoteConfigManager.isAppOpenAdEnabled()) {
                adsManager.showAppOpenIfReady(this@MainActivity)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        adsManager.destroy()
        billingGateway.destroy()
    }
}
