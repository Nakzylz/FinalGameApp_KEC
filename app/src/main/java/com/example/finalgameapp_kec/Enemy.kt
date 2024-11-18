package com.example.finalgameapp_kec

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint

class Enemy(var x: Float, var y: Float, context: Context, newWidth: Int, newHeight: Int) {
    private val bitmap: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.enemyprefabplaceholder)
    var width: Int = newWidth
    var height: Int = newHeight
    var isOnScreen = false
    private var shootCount = 0
    private val shootLimit = 5
    var isShooting = false
    var bullets: MutableList<Bullet> = mutableListOf()
    var speedX = 5f


    fun update() {
        if (!isOnScreen) {

            x -= speedX
            if (x <= 300f) {
                isOnScreen = true
                startShooting()
            }
        }

        if (x < -width) {

        }
    }

    // Shoot method (fires bullets)
    fun shoot() {
        if (shootCount < shootLimit) {
            bullets.add(Bullet(x, y))
            shootCount++
        }
    }

    fun startShooting() {
        isShooting = true
    }


    fun draw(canvas: Canvas, paint: Paint) {
        canvas.drawBitmap(bitmap, x - width / 2f, y - height / 2f, paint)


        for (bullet in bullets) {
            bullet.draw(canvas, paint)
        }
    }
}


















