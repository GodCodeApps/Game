package com.component.game

import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.component.game.net.Client
import com.component.game.net.MsgBody
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val androidId: String =
            Settings.System.getString(contentResolver, Settings.Secure.ANDROID_ID)
        Client.setAccount(androidId)
        Client.bind(this)
        val tvLook = findViewById<TPView>(R.id.tv_look)
        val tvPain = findViewById<TPView>(R.id.tv_pain)
        tvLook.setDrawEnable(false)
        Client.registerListener {
            val fromJson = Gson().fromJson(it, MsgBody::class.java)
            if (fromJson.userId == Client.getAccount()) {
                return@registerListener
            }
            tvLook.setInvalidate(fromJson.preX, fromJson.preY, fromJson.x, fromJson.y)
        }
    }
}