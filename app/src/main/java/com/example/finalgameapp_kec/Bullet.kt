package com.example.finalgameapp_kec

import android.graphics.Canvas
import android.graphics.Paint

class Bullet(var x: Float, var y: Float) {

    fun update() {
        y -= 10f
    }

    fun draw(canvas: Canvas, paint: Paint) {
        canvas.drawRect(x - 5, y - 20, x + 5, y, paint)  // Draw the bullet as a small rectangle
    }
}
