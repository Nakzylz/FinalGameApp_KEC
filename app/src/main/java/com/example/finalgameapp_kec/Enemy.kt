package com.example.finalgameapp_kec

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import kotlin.random.Random

class Enemy(var x: Float, var y: Float, context: Context, newWidth: Int, newHeight: Int, private val screenWidth: Float, private val screenHeight: Float) {

    private val bitmap: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.enemyspaceship1)
    var width: Int = newWidth
    var height: Int = newHeight

    private val scaledBitmap: Bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false)

    private var targetX: Float = 0f
    private var moveDownTimer: Int = 0

    private val shootLimit = 5
    private var shootCount = 0
    var isOnScreen = false

    var bullets: MutableList<EnemyBullet> = mutableListOf()

    private var shootTimer: Int = 0
    private val shootInterval = 180

    fun getBounds(): Rect {
        return Rect(x.toInt(), y.toInt(), (x + width).toInt(), (y + height).toInt())
    }

    fun spawnAtEdge() {
        x = screenWidth + width / 2f
        y = Random.nextInt(0, (screenHeight / 4).toInt()).toFloat()
        targetX = Random.nextInt(100, (screenWidth - width).toInt()).toFloat()
        isOnScreen = false
    }

    fun update() {
        if (!isOnScreen) {
            if (x > targetX) {
                x -= 5f
            } else if (x < targetX) {
                x += 5f
            }

            if (Math.abs(x - targetX) < 5f) {
                x = targetX
                isOnScreen = true
            }
        }

        if (isOnScreen) {
            moveDownTimer++
            if (moveDownTimer > 300) {
                y += 10f
            }
        }

        for (bullet in bullets) {
            bullet.update()
        }

        shootTimer++
        if (shootTimer > shootInterval) {
            shoot()
            shootTimer = 0
        }

        if (x < -width || y > screenHeight) {
            isOnScreen = false
        }
    }

    private fun shoot() {
        if (shootCount < shootLimit) {
            bullets.add(EnemyBullet(x, y))
            shootCount++
        }
    }

    fun draw(canvas: Canvas, paint: Paint) {
        canvas.drawBitmap(scaledBitmap, x - width / 2f, y - height / 2f, paint)
        for (bullet in bullets) {
            bullet.draw(canvas, paint)
        }
    }
}



