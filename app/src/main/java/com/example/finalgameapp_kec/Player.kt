package com.example.finalgameapp_kec

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint

class Player(var x: Float, private var y: Float, context: Context, val width: Int, private val height: Int) {
    private val playerBitmap: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.playerprefabplaceholder)
    private val scaledBitmap: Bitmap = Bitmap.createScaledBitmap(playerBitmap, width, height, false)

    val bullets: MutableList<Bullet> = mutableListOf()

    fun draw(canvas: Canvas, paint: Paint) {
        canvas.drawBitmap(scaledBitmap, x, y, paint)


        for (bullet in bullets) {
            bullet.draw(canvas, paint)
        }
    }

    fun update() {

        val bulletsToRemove = mutableListOf<Bullet>()
        for (bullet in bullets) {
            bullet.update()
            if (bullet.isOffScreen()) {
                bulletsToRemove.add(bullet)
            }
        }
        bullets.removeAll(bulletsToRemove)
    }

    fun shoot() {

        if (bullets.size < 1) {
            bullets.add(Bullet(x + width / 2f, y))
        }
    }
}




















