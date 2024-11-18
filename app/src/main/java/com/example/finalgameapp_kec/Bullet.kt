package com.example.finalgameapp_kec

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

class Bullet(var x: Float, var y: Float) {
    var speedY = 10f

    fun update() {
        y -= speedY
    }

    fun isOffScreen(): Boolean {
        return y < 0
    }

    fun draw(canvas: Canvas, paint: Paint) {
        paint.color = Color.RED
        canvas.drawCircle(x, y, 10f, paint)
    }
}













