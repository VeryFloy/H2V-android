package com.h2v.messenger.core.network

import com.h2v.messenger.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // Auth
    @POST("api/auth/send-otp")
    suspend fun sendOtp(@Body body: SendOtpRequest): Response<ApiResponse<SendOtpResponse>>

    @POST("api/auth/verify-otp")
    suspend fun verifyOtp(@Body body: VerifyOtpRequest): Response<ApiResponse<VerifyOtpResponse>>

    @POST("api/auth/logout")
    suspend fun logout(): Response<ApiResponse<Unit>>

    // Users
    @GET("api/users/me")
    suspend fun getMe(): Response<ApiResponse<UserDto>>

    @PATCH("api/users/me")
    suspend fun updateMe(@Body body: UpdateMeRequest): Response<ApiResponse<UserDto>>

    @GET("api/users/search")
    suspend fun searchUsers(@Query("q") query: String): Response<ApiResponse<List<UserDto>>>

    @GET("api/users/{id}")
    suspend fun getUser(@Path("id") id: String): Response<ApiResponse<UserDto>>

    // Chats
    @GET("api/chats")
    suspend fun getChats(
        @Query("cursor") cursor: String? = null,
        @Query("limit") limit: Int = 30
    ): Response<ApiResponse<ChatsResponse>>

    @GET("api/chats/{id}")
    suspend fun getChat(@Path("id") id: String): Response<ApiResponse<ChatDto>>

    @POST("api/chats/direct")
    suspend fun createDirectChat(@Body body: CreateDirectChatRequest): Response<ApiResponse<ChatDto>>

    // Messages
    @GET("api/chats/{chatId}/messages")
    suspend fun getMessages(
        @Path("chatId") chatId: String,
        @Query("cursor") cursor: String? = null,
        @Query("limit") limit: Int = 50
    ): Response<ApiResponse<MessagesResponse>>

    @PATCH("api/messages/{id}")
    suspend fun editMessage(
        @Path("id") id: String,
        @Body body: EditMessageRequest
    ): Response<ApiResponse<MessageDto>>

    @DELETE("api/messages/{id}")
    suspend fun deleteMessage(@Path("id") id: String): Response<ApiResponse<Unit>>

    @POST("api/messages/{id}/read")
    suspend fun markRead(@Path("id") id: String): Response<ApiResponse<Unit>>
}
