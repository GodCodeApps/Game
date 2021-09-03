package com.component.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.component.game.net.Client
import com.component.game.net.MsgBody
import com.google.gson.Gson

/**
 * Copyright (C), 2020-2021, 中传互动（湖北）信息技术有限公司
 * @Author: pym
 * @Date: 2021/9/3 11:08
 * @Description:
 */
class TPView(context: Context, attr: AttributeSet) : View(context, attr) {
    private var preX: Float = 0.0f
    private var preY: Float = 0.0f
    private var mPath = Path()
    private var mPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
    private var enable: Boolean = true

    init {
        mPaint.style = Paint.Style.STROKE
        mPaint.color = Color.RED
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.strokeWidth = 10f

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(mPath, mPaint)
    }

    fun setDrawEnable(enable: Boolean) {
        this.enable = enable
    }

    fun setInvalidate(preX: Float, preY: Float, x: Float, y: Float) {
        mPath.moveTo(preX, preY)
        mPath.quadTo(preX, preY, x, y)
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!enable) {
            return false
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mPath.moveTo(event.x, event.y)
                preX = event.x
                preY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                mPath.quadTo(preX, preY, event.x, event.y)
                val apply = MsgBody(preX, preY, event.x, event.y)
                var msg = Gson().toJson(apply)
                Client.sendMsg(msg)
                preX = event.x
                preY = event.y

            }
        }
        invalidate()
        return true
    }

}
