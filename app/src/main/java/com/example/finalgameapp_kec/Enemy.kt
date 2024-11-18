package com.example.finalgameapp_kec

import android.graphics.Canvas
import android.graphics.Paint

class Enemy(var x: Float, var y: Float) {

    fun update() {
        y += 5f
    }

    fun draw(canvas: Canvas, paint: Paint) {
        canvas.drawRect(x - 30, y - 30, x + 30, y + 30, paint)  // Drawing a simple square as the enemy
    }
}
