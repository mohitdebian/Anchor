package com.example.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.api.NimChatRequest
import com.example.data.api.NimMessage
import com.example.data.api.NvidiaNimApi
import com.example.data.repository.FocusRepository
import com.example.data.repository.UsageStatsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray

data class AppInsight(
    val appName: String,
    val packageName: String,
    val roast: String,
    val timeSpentMinutes: Int
)

data class InsightsState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val insights: List<AppInsight> = emptyList(),
    val genericInsight: String = ""
)

class InsightsViewModel(
    private val api: NvidiaNimApi,
    private val focusRepository: FocusRepository,
    private val usageStatsRepository: UsageStatsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(InsightsState())
    val uiState: StateFlow<InsightsState> = _uiState.asStateFlow()

    init {
        loadInsights()
    }

    private fun generateLocalRoast(appName: String, minutes: Int): String {
        val name = appName.lowercase()
        return when {
            name.contains("instagram") -> {
                "$minutes minutes on Instagram? You could have read a chapter of a book, but instead you mindlessly scrolled past reels of strangers' fake highlights. Disappointing."
            }
            name.contains("youtube") -> {
                "You watched $minutes minutes of YouTube today. Did those videos pay your bills, or are you just funding creators' vacations while your own goals gather dust?"
            }
            name.contains("facebook") || name.contains("meta") -> {
                "Wasted $minutes minutes on Facebook. Checking up on people you barely know or arguing with strangers in comments? Your focus is in shambles."
            }
            name.contains("tiktok") -> {
                "TikTok sucked away $minutes minutes of your life today. Short-form videos have absolutely vaporized your attention span. Keep this up and you won't focus on anything again."
            }
            name.contains("twitter") || name.contains(" x ") || name.equals("x") -> {
                "Spent $minutes minutes on X/Twitter. Reading rage-bait and hot takes isn't productivity. Your future self is shaking their head in utter regret."
            }
            name.contains("reddit") -> {
                "Mindlessly browsed Reddit for $minutes minutes. Reading other people's stories doesn't build your own. Turn off the app and get to work."
            }
            name.contains("whatsapp") || name.contains("telegram") || name.contains("signal") || name.contains("messenger") || name.contains("chat") || name.contains("discord") -> {
                "Spent $minutes minutes chatting. Surely those notifications couldn't wait. Typing words on a screen is much easier than doing real work, isn't it?"
            }
            name.contains("gmail") || name.contains("outlook") || name.contains("mail") -> {
                "Mindlessly checked email for $minutes minutes. Refreshing your inbox won't make you productive; it just gives you the cheap illusion of doing something."
            }
            name.contains("netflix") || name.contains("prime video") || name.contains("hulu") || name.contains("disney") || name.contains("hotstar") || name.contains("spotify") || name.contains("music") -> {
                "Indulged in $minutes minutes of streaming/entertainment. Sinking into comfort won't get your hard work done. Reality is waiting for you."
            }
            else -> {
                "Wasted $minutes minutes on $appName today. That is exactly $minutes minutes you will never, ever get back. What was even the point of that session?"
            }
        }
    }

    fun loadInsights() {
        viewModelScope.launch {
            _uiState.value = InsightsState(isLoading = true)
            try {
                // Gather real data from the repository
                val distractingApps = withContext(Dispatchers.IO) {
                    usageStatsRepository.getTopDistractingApps(5)
                }

                if (distractingApps.isEmpty()) {
                    _uiState.value = InsightsState(
                        isLoading = false,
                        genericInsight = "Look at you, pretending to be productive. There is literally zero app usage recorded today. Did you actually get some work done, or did you just leave your phone in another room to trick me? I've got my eyes on you.",
                        insights = emptyList()
                    )
                    return@launch
                }

                // Generate local fallback insights first (ensures we ALWAYS have real roasts and NEVER show fake apps)
                val fallbackInsights = distractingApps.map { app ->
                    AppInsight(
                        appName = app.appName,
                        packageName = app.packageName,
                        roast = generateLocalRoast(app.appName, app.timeInForegroundMinutes),
                        timeSpentMinutes = app.timeInForegroundMinutes
                    )
                }

                val apiKey = BuildConfig.NVIDIA_NIM_API_KEY
                if (apiKey.isEmpty() || apiKey == "dummy") {
                    // Fall back directly to our high-quality local custom roasts without error
                    _uiState.value = InsightsState(
                        isLoading = false,
                        insights = fallbackInsights,
                        error = null
                    )
                    return@launch
                }

                val prompt = """
                    You are a harsh, brutally honest productivity coach.
                    Analyze the user's top distracting apps today:
                    ${distractingApps.joinToString { "${it.appName} (${it.packageName}): ${it.timeInForegroundMinutes} mins" }}
                    
                    For each app, write a short, demotivating, sarcastic roast (1-2 sentences) making the user feel regret for wasting time on it.
                    
                    CRITICAL: You MUST output ONLY a valid JSON array of objects. Do not wrap in markdown blocks like ```json. Just output the raw JSON array.
                    Each object must have exactly three keys: "appName", "packageName", and "roast".
                    Example:
                    [
                      {"appName": "Instagram", "packageName": "com.instagram.android", "roast": "45 minutes on Instagram? You could have read a chapter of a book, but instead you watched strangers' highlight reels."}
                    ]
                """.trimIndent()

                val response = api.getInsights(
                    authHeader = "Bearer $apiKey",
                    request = NimChatRequest(
                        messages = listOf(
                            NimMessage(role = "user", content = prompt)
                        )
                    )
                )

                var messageContent = response.choices.firstOrNull()?.message?.content ?: "[]"
                messageContent = messageContent.trim()
                if (messageContent.startsWith("```json")) {
                    messageContent = messageContent.substringAfter("```json").substringBeforeLast("```").trim()
                } else if (messageContent.startsWith("```")) {
                    messageContent = messageContent.substringAfter("```").substringBeforeLast("```").trim()
                }

                val parsedInsights = mutableListOf<AppInsight>()
                try {
                    val jsonArray = JSONArray(messageContent)
                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        val pkgName = obj.optString("packageName")
                        val appName = obj.optString("appName")
                        
                        // Strict validation: Ensure this app is actually in the user's real distracting list!
                        // This prevents the AI from hallucinating or making up apps like Instagram if they were never opened.
                        val actualApp = distractingApps.find { 
                            it.packageName.equals(pkgName, ignoreCase = true) || 
                            it.appName.equals(appName, ignoreCase = true) 
                        }
                        if (actualApp != null) {
                            parsedInsights.add(
                                AppInsight(
                                    appName = actualApp.appName,
                                    packageName = actualApp.packageName,
                                    roast = obj.optString("roast"),
                                    timeSpentMinutes = actualApp.timeInForegroundMinutes
                                )
                            )
                        }
                    }
                } catch (e: Exception) {
                    // Fall back to local roasts if JSON parsing fails
                    _uiState.value = InsightsState(
                        isLoading = false,
                        insights = fallbackInsights,
                        error = null
                    )
                    return@launch
                }

                // If the parsed insights list is empty or the model hallucinated completely, use fallback insights
                val finalInsights = if (parsedInsights.isEmpty()) fallbackInsights else parsedInsights

                _uiState.value = InsightsState(
                    isLoading = false,
                    insights = finalInsights,
                    error = null
                )
            } catch (e: Exception) {
                // If any error occurs (network, rate limit, etc.), silently fall back to our high-quality local roasts so the app stays functional and premium!
                val distractingApps = withContext(Dispatchers.IO) {
                    usageStatsRepository.getTopDistractingApps(5)
                }
                if (distractingApps.isNotEmpty()) {
                    val fallbackInsights = distractingApps.map { app ->
                        AppInsight(
                            appName = app.appName,
                            packageName = app.packageName,
                            roast = generateLocalRoast(app.appName, app.timeInForegroundMinutes),
                            timeSpentMinutes = app.timeInForegroundMinutes
                        )
                    }
                    _uiState.value = InsightsState(
                        isLoading = false,
                        insights = fallbackInsights,
                        error = null
                    )
                } else {
                    _uiState.value = InsightsState(
                        isLoading = false,
                        genericInsight = "Look at you, pretending to be productive. There is literally zero app usage recorded today. Did you actually get some work done, or did you just leave your phone in another room to trick me? I've got my eyes on you.",
                        insights = emptyList()
                    )
                }
            }
        }
    }
}
