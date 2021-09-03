package com.component.game.net

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception

/**
 * Copyright (C), 2020-2021, 中传互动（湖北）信息技术有限公司
 * @Author: pym
 * @Date: 2021/9/2 17:16
 * @Description:
 */
object Client {
    private var mWritable: ServiceWritable? = null
    private var userId = ""
    private var map = HashMap<String, (String) -> Unit>()
    private var receiverListener: (String) -> Unit = {
        GlobalScope.launch(Dispatchers.Main) {
            map.values.forEach { listener ->
                listener?.invoke(it)
            }
        }

    }

    fun bind(context: Context): Client {
        context.bindService(
            Intent(context, IMService::class.java),
            ConnectionImpl(),
            Context.BIND_AUTO_CREATE
        )
        return this
    }

    fun setAccount(id: String) {
        userId = id
    }

    fun getAccount(): String {
        return userId
    }

    fun sendMsg(msg: String) {
        mWritable?.sendMsg(msg)
    }

    fun registerListener(listener: (String) -> Unit) {
        map[listener.javaClass.name] = listener
    }

    fun unRegisterListener(listener: (String) -> Unit) {
        map.remove(listener.javaClass.name)
    }

    private class ConnectionImpl : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            service?.let {
                mWritable = service as ServiceWritable
                mWritable?.getServiceBinder()?.registerListener(receiverListener)
                try {
                    GlobalScope.launch(Dispatchers.IO) {
                        mWritable?.connect()
                    }
                } catch (e: Exception) {
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {

        }
    }
}
