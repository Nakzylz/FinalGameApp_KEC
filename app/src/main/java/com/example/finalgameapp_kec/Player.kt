package com.example.finalgameapp_kec

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint

class Player(var x: Float, var y: Float, context: Context, newWidth: Int, newHeight: Int) {
    private val bitmap: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.playerspaceship1)
    var width: Int = newWidth
    var height: Int = newHeight

    private val scaledBitmap: Bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false)

    val bullets: MutableList<PlayerBullet> = mutableListOf()

    fun update() {
        if (x < 0) x = 0f
        if (x > 1080 - width) x = 1080f - width
    }

    fun shoot() {
        bullets.add(PlayerBullet(x + width / 2, y, 10f, 20f))  // Add a new bullet at the player's position
    }

    fun draw(canvas: Canvas, paint: Paint) {
        canvas.drawBitmap(scaledBitmap, x, y, paint)
        for (bullet in bullets) {
            bullet.draw(canvas, paint)
        }
    }
}























