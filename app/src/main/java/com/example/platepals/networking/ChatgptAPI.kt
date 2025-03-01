package com.example.platepals.networking

import com.example.platepals.model.Message
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class ChatGptRequest(
    val messages: List<Message>,
    val model: String = "gpt-4o-mini",
    val max_tokens: Int = 300,
)

data class Choice(
    val message: Message
)

data class ChatGptResponse(
    val choices: List<Choice>
)

interface ChatgptAPI {
    @POST("v1/chat/completions")
    fun getChatResponse(
        @Body request: ChatGptRequest
    ): Call<ChatGptResponse>
}