package com.example.data.api

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

data class NimChatRequest(
    val model: String = "meta/llama-3.1-8b-instruct",
    val messages: List<NimMessage>,
    val max_tokens: Int = 512
)

data class NimMessage(
    val role: String,
    val content: String
)

data class NimChatResponse(
    val choices: List<NimChoice>
)

data class NimChoice(
    val message: NimMessage
)

interface NvidiaNimApi {
    @POST("chat/completions")
    suspend fun getInsights(
        @Header("Authorization") authHeader: String,
        @Body request: NimChatRequest
    ): NimChatResponse
}
