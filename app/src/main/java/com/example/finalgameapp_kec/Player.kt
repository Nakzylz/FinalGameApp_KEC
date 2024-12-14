package com.example.finalgameapp_kec

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect

class Player(var x: Float, var y: Float, context: Context, newWidth: Int, newHeight: Int) {
    private val bitmap: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.playerspaceship1)
    var width: Int = newWidth
    var height: Int = newHeight

    private val scaledBitmap: Bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false)

    val bullets: MutableList<PlayerBullet> = mutableListOf()
    var health = 100

    fun getBounds(): Rect {
        return Rect(x.toInt(), y.toInt(), (x + width).toInt(), (y + height).toInt())
    }

    fun update() {
        if (x < 0) x = 0f
        if (x > 1080 - width) x = 1080f - width
    }

    fun shoot() {
        bullets.add(PlayerBullet(x + width / 2, y, 10f, 20f))
    }

    fun draw(canvas: Canvas, paint: Paint) {
        canvas.drawBitmap(scaledBitmap, x, y, paint)
        for (bullet in bullets) {
            bullet.draw(canvas, paint)
        }
    }

    fun takeDamage(amount: Int) {
        health -= amount
        if (health < 0) health = 0
    }
}
























