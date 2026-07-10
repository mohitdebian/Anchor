package com.example.ui.screens.block

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.api.GeminiRequest
import com.example.data.api.GeminiContent
import com.example.data.api.GeminiPart
import com.example.data.api.GeminiApiService

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BlockedOverlayViewModel(
    application: Application,
    private val geminiApiService: GeminiApiService,
    private val usageStatsRepository: com.example.data.repository.UsageStatsRepository
) : AndroidViewModel(application) {

    private val _roastMessage = MutableStateFlow<String?>(null)
    val roastMessage: StateFlow<String?> = _roastMessage

    fun fetchRoast(appName: String) {
        if (_roastMessage.value != null) return // already fetched
        
        viewModelScope.launch {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isNotEmpty() && apiKey != "dummy") {
                try {
                    val timeSpentMins = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                        usageStatsRepository.getTopDistractingApps(50).find { it.appName == appName }?.timeInForegroundMinutes ?: 0
                    }
                    val prompt = "The user is trying to open the app '$appName' which is currently blocked. They have already wasted $timeSpentMins minutes on it today. Give them a 1 sentence harsh, regretful, and sarcastic roast using this time spent to make them reconsider their life choices and go back to work."
                    
                    val response = geminiApiService.generateContent(
                        apiKey = apiKey,
                        request = GeminiRequest(
                            contents = listOf(
                                GeminiContent(
                                    parts = listOf(GeminiPart(text = prompt))
                                )
                            )
                        )
                    )
                    var messageContent = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: fallbackRoast(appName)
                    _roastMessage.value = messageContent
                } catch (e: Exception) {
                    _roastMessage.value = fallbackRoast(appName)
                }
            } else {
                _roastMessage.value = fallbackRoast(appName)
            }
        }
    }

    private fun fallbackRoast(appName: String): String {
        return "Trying to open $appName? That's cute. Reality is waiting for you, get back to work."
    }
}
