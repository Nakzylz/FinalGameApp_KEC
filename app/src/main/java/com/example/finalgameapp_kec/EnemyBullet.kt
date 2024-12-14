package com.example.finalgameapp_kec

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

class EnemyBullet(var x: Float, var y: Float) {

    private val speed = 20f

    fun update() {
        y += speed
    }

    fun draw(canvas: Canvas, paint: Paint) {
        paint.color = Color.RED
        canvas.drawRect(x - 5f, y - 10f, x + 5f, y + 10f, paint)
    }
}

