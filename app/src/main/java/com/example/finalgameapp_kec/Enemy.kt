package com.example.finalgameapp_kec

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint

class Enemy(var x: Float, var y: Float, context: Context, newWidth: Int, newHeight: Int, private val screenWidth: Float, private val screenHeight: Float) {
    private val bitmap: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.enemyprefabplaceholder)
    var width: Int = newWidth
    var height: Int = newHeight

    // Scale the bitmap to fit smaller size
    private val scaledBitmap: Bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false)

    var isOnScreen = false
    private var shootCount = 0
    private val shootLimit = 5
    var isShooting = false
    var bullets: MutableList<Bullet> = mutableListOf()
    var speedX = 5f

    // Target position the enemy is moving towards
    private var targetX: Float = 0f

    // Modify the spawn method to ensure each enemy has a random stop position
    fun spawnAtEdge() {
        // Spawn at a random position from the right edge
        x = screenWidth + width / 2f

        // Random Y position near the top
        y = (0..screenHeight.toInt() / 4).random().toFloat()

        // Random target X position for the enemy to move towards
        targetX = (100..(screenWidth - width).toInt()).random().toFloat()

        isOnScreen = false
    }

    fun update() {
        if (!isOnScreen) {
            // Start moving towards the target X position
            if (x > targetX) {
                x -= speedX
            } else if (x < targetX) {
                x += speedX
            }

            // Stop when the enemy reaches the target X position
            if (Math.abs(x - targetX) < speedX) {
                x = targetX
                isOnScreen = true
                startShooting()
            }
        }

        if (x < -width) {
            // Reset enemy if it moves off the screen
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
        // Draw the scaled enemy bitmap
        canvas.drawBitmap(scaledBitmap, x - width / 2f, y - height / 2f, paint)
        for (bullet in bullets) {
            bullet.draw(canvas, paint)
        }
    }
}





