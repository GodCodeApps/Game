package com.component.game.net

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.fixedRateTimer


/**
 * Copyright (C), 2020-2021, 中传互动（湖北）信息技术有限公司
 * @Author: pym
 * @Date: 2021/4/19 14:55
 * @Description:
 */
class IMService : Service() {
    companion object {
        var TAG = "IWebSocketListener"
    }

    private val binder = ServiceBinder()
    override fun onBind(intent: Intent): IBinder? {
        return binder
    }


    class ServiceBinder : Binder(), ServiceWritable {
        private var mWebSocket: WebSocket? = null
        private var receiverListener: ((String) -> Unit)? = null
        fun registerListener(receiverListener: ((String) -> Unit)) {
            this.receiverListener = receiverListener
        }

        private fun heartBeat() {
            fixedRateTimer(this.javaClass.simpleName, false, 1, (1000 * 5).toLong()) {
                MainScope().launch {
                    var isSuccess = mWebSocket?.send("心跳") ?: false
                    if (!isSuccess) {
                        mWebSocket?.cancel()
                        connect()
                    }
                }
            }

        }

        override fun connect() {
            var client = OkHttpClient.Builder()
                .readTimeout(3, TimeUnit.SECONDS)
                .build()
            val request = Request.Builder()
                .url("ws://8.129.109.206:8080/webSocket/im/chat/" + "${Client.getAccount()}/9f6657fa87a5032e")
//                .url("ws://192.168.0.124:8080/webSocket/im/chat/" + "${Client.getAccount()}/9f6657fa87a5032e")
//                .url("ws://echo.websocket.org")
                .build()
            mWebSocket = client.newWebSocket(request, IWebSocketListener(receiverListener))
            heartBeat()
        }

        override fun reNet(isForce: Boolean) {
        }

        override fun close() {
            mWebSocket?.close(4001, "关闭")
        }

        override fun sendMsg(msgBody: String) {
            val isSuccess = mWebSocket?.send(msgBody)
            Log.d(TAG, "send==${msgBody}>>>>isSuccess==${isSuccess}")

        }

        override fun getServiceBinder(): ServiceBinder {
            return this
        }

        class IWebSocketListener(var receiverListener: ((String) -> Unit)?) : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                Log.d(TAG, "onOpen==${response}")
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
                Log.d(TAG, "onClosed==${reason}")

            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                Log.d(TAG, "onFailure==${t?.message}")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                receiverListener?.invoke(text)
                Log.d(TAG, "onMessage==${text}")
            }

            override fun onMessage(webSocket: WebSocket, bytes: okio.ByteString) {
                super.onMessage(webSocket, bytes)
                Log.d(TAG, "onMessage==${webSocket}")

            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosing(webSocket, code, reason)
                Log.d(TAG, "onClosing==")
            }
        }
    }


}