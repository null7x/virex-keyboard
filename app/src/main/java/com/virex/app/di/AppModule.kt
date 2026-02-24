package com.virex.app.di

import android.content.Context
import com.virex.ads.VkAdsManager
import com.virex.analytics.AnalyticsBatcher
import com.virex.app.config.RemoteConfigManager
import com.virex.app.data.AppPreferences
import com.virex.app.data.ThemeRepository
import com.virex.billing.BillingGateway
import com.virex.core.RecommendationEngine
import com.virex.core.ThemeManager
import com.virex.core.font.FontManager
import com.virex.core.network.ThemeApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()
        
        return Retrofit.Builder()
            .baseUrl("https://YOUR_VERCEL_URL/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideThemeApi(retrofit: Retrofit): ThemeApi {
        return retrofit.create(ThemeApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAppPreferences(@ApplicationContext context: Context): AppPreferences {
        return AppPreferences(context)
    }

    @Provides
    @Singleton
    fun provideThemeRepository(): ThemeRepository {
        return ThemeRepository()
    }

    @Provides
    @Singleton
    fun provideRecommendationEngine(): RecommendationEngine {
        return RecommendationEngine()
    }

    @Provides
    @Singleton
    fun provideAnalyticsBatcher(): AnalyticsBatcher {
        return AnalyticsBatcher()
    }

    @Provides
    @Singleton
    fun provideThemeManager(): ThemeManager {
        return ThemeManager
    }

    @Provides
    @Singleton
    fun provideVkAdsManager(@ApplicationContext context: Context): VkAdsManager {
        return VkAdsManager(context)
    }

    @Provides
    @Singleton
    fun provideBillingGateway(@ApplicationContext context: Context): BillingGateway {
        return BillingGateway(context)
    }

    @Provides
    @Singleton
    fun provideRemoteConfigManager(api: ThemeApi): RemoteConfigManager {
        return RemoteConfigManager(api)
    }

    @Provides
    @Singleton
    fun provideFontManager(@ApplicationContext context: Context): FontManager {
        return FontManager(context)
    }
}
