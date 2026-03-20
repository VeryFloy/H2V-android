package com.h2v.messenger.core.network

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.h2v.messenger.BuildConfig
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

data class WsEvent(val type: String, val payload: JsonObject)

@Singleton
class WebSocketManager @Inject constructor(
    private val tokenManager: TokenManager,
    private val gson: Gson
) {
    private val client = OkHttpClient.Builder()
        .pingInterval(30, TimeUnit.SECONDS)
        .build()

    private var webSocket: WebSocket? = null
    private var heartbeatJob: Job? = null
    private var reconnectJob: Job? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _events = MutableSharedFlow<WsEvent>(extraBufferCapacity = 64)
    val events = _events.asSharedFlow()

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState = _connectionState.asStateFlow()

    private var reconnectAttempt = 0
    private val maxReconnectDelay = 30_000L

    enum class ConnectionState { DISCONNECTED, CONNECTING, AUTHENTICATING, CONNECTED }

    fun connect() {
        if (_connectionState.value != ConnectionState.DISCONNECTED) return
        _connectionState.value = ConnectionState.CONNECTING

        val wsUrl = BuildConfig.BASE_URL
            .replace("https://", "wss://")
            .replace("http://", "ws://") + "/ws"

        val request = Request.Builder().url(wsUrl).build()
        webSocket = client.newWebSocket(request, createListener())
    }

    fun disconnect() {
        reconnectJob?.cancel()
        heartbeatJob?.cancel()
        webSocket?.close(1000, "Client disconnect")
        webSocket = null
        _connectionState.value = ConnectionState.DISCONNECTED
        reconnectAttempt = 0
    }

    fun send(event: String, payload: Any = emptyMap<String, Any>()) {
        val json = gson.toJson(mapOf("event" to event, "payload" to payload))
        webSocket?.send(json)
    }

    private fun createListener() = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            _connectionState.value = ConnectionState.AUTHENTICATING
            scope.launch {
                val token = tokenManager.getToken() ?: return@launch
                send("auth", mapOf("token" to token))
            }
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            try {
                val json = gson.fromJson(text, JsonObject::class.java)
                val type = json.get("event")?.asString ?: json.get("type")?.asString ?: return
                val payload = json.getAsJsonObject("payload") ?: JsonObject()

                if (type == "auth:ok") {
                    _connectionState.value = ConnectionState.CONNECTED
                    reconnectAttempt = 0
                    startHeartbeat()
                }

                _events.tryEmit(WsEvent(type, payload))
            } catch (_: Exception) {}
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            webSocket.close(1000, null)
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            heartbeatJob?.cancel()
            _connectionState.value = ConnectionState.DISCONNECTED
            if (code != 1000) scheduleReconnect()
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            heartbeatJob?.cancel()
            _connectionState.value = ConnectionState.DISCONNECTED
            scheduleReconnect()
        }
    }

    private fun startHeartbeat() {
        heartbeatJob?.cancel()
        heartbeatJob = scope.launch {
            while (isActive) {
                delay(25_000)
                send("presence:ping")
            }
        }
    }

    private fun scheduleReconnect() {
        reconnectJob?.cancel()
        reconnectJob = scope.launch {
            val delay = minOf(1000L * (1 shl reconnectAttempt), maxReconnectDelay)
            reconnectAttempt++
            delay(delay)
            connect()
        }
    }
}
