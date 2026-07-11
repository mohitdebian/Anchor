package com.example

import android.content.Context
import com.example.services.AmbientSoundManager
import com.example.data.database.AppDatabase
import com.example.data.repository.FocusRepository
import com.example.data.repository.UsageStatsRepository
import com.example.data.repository.ScheduleRepository
import com.example.data.repository.UserRepository
import com.example.data.api.GeminiApiService
import com.example.data.api.GeminiRetrofitClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory


class ThemeManager(private val context: Context) {
    private val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val isDarkTheme = kotlinx.coroutines.flow.MutableStateFlow(prefs.getBoolean("dark_theme", true))
    fun setDarkTheme(isDark: Boolean) {
        prefs.edit().putBoolean("dark_theme", isDark).apply()
        isDarkTheme.value = isDark
    }
}

interface AppContainer {
    val themeManager: ThemeManager
    val ambientSoundManager: AmbientSoundManager
    val focusRepository: FocusRepository
    val usageStatsRepository: UsageStatsRepository
    val geminiApiService: GeminiApiService
    val scheduleRepository: ScheduleRepository
    val userRepository: UserRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    private val database by lazy { AppDatabase.getDatabase(context) }
    
    override val focusRepository: FocusRepository by lazy {
        FocusRepository(database.focusSessionDao())
    }
    
    override val usageStatsRepository: UsageStatsRepository by lazy {
        UsageStatsRepository(context)
    }
    
    override val scheduleRepository: ScheduleRepository by lazy {
        ScheduleRepository(database.scheduleDao())
    }
    
    override val userRepository: UserRepository by lazy {
        UserRepository(context, database.dailyGoalDao())
    }
    
    override val themeManager: ThemeManager by lazy { ThemeManager(context) }
    override val ambientSoundManager: AmbientSoundManager by lazy { AmbientSoundManager() }

    override val geminiApiService: GeminiApiService by lazy {
        GeminiRetrofitClient.api
    }
}
