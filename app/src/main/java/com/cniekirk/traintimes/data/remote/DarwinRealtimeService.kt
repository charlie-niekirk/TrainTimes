package com.cniekirk.traintimes.data.remote

import com.tinder.scarlet.websocket.WebSocketEvent
import com.tinder.scarlet.ws.Receive
import kotlinx.coroutines.channels.ReceiveChannel

interface DarwinRealtimeService {

    @Receive
    fun observerWebsocketEvent(): ReceiveChannel<WebSocketEvent>

}