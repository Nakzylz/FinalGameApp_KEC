package com.example.finalgameapp_kec

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

class PlayerBullet(var x: Float, var y: Float, var width: Float, var height: Float) {

    private val speed = 20f

    fun update() {
        y -= speed  // Move the bullet upwards
    }

    fun isOffScreen(): Boolean {
        return y < 0
    }

    fun draw(canvas: Canvas, paint: Paint) {
        paint.color = android.graphics.Color.GREEN
        canvas.drawRect(x - width / 2, y - height / 2, x + width / 2, y + height / 2, paint)
    }
}


class EnemyBullet(var x: Float, var y: Float) {

    private val speed = 20f

    fun update() {
        y += speed  // Move the bullet downwards
    }

    fun draw(canvas: Canvas, paint: Paint) {
        paint.color = android.graphics.Color.RED
        canvas.drawRect(x - 5f, y - 10f, x + 5f, y + 10f, paint)
    }
}















