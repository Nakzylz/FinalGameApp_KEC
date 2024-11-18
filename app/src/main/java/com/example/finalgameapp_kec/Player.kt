package com.example.finalgameapp_kec

import android.graphics.Canvas
import android.graphics.Paint

class Player(var x: Float, var y: Float) {


    fun move(dx: Float) {
        x = dx
    }


    fun draw(canvas: Canvas, paint: Paint) {
        canvas.drawRect(x - 30, y - 30, x + 30, y + 30, paint)  // Drawing a simple square as the player
    }
}
