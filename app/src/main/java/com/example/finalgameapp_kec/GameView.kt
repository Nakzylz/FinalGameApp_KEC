package com.example.finalgameapp_kec

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView

class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {
    private val paint: Paint = Paint()
    private val player: Player = Player(500f, 1600f, context, 100, 100)
    private val enemies: MutableList<Enemy> = mutableListOf()

    private val screenWidth = context.resources.displayMetrics.widthPixels.toFloat() // Use Float for easier positioning
    private val screenHeight = context.resources.displayMetrics.heightPixels.toFloat()

    private val gameThread: GameThread
    private var enemySpawnTimer = 0

    init {
        holder.addCallback(this)
        gameThread = GameThread(holder, this)
        gameThread.isRunning = true
    }

    fun update() {
        player.update()

        // Enemy spawning logic
        enemySpawnTimer++
        if (enemySpawnTimer > 100 && enemies.size < 6) {  // Allow spawning only if there are fewer than 6 enemies
            val newEnemy = Enemy(0f, 0f, context, 80, 80, screenWidth, screenHeight)
            newEnemy.spawnAtEdge()  // Randomly set its position and target X
            enemies.add(newEnemy)
            enemySpawnTimer = 0
        }

        // Update each enemy
        val enemiesToRemove = mutableListOf<Enemy>()
        val bulletsToRemove = mutableListOf<Bullet>()

        // Handle bullet collision and update logic
        for (bullet in player.bullets) {
            bullet.update()

            // Check if the bullet collides with any enemy
            for (enemy in enemies) {
                if (isCollision(bullet, enemy)) {
                    bulletsToRemove.add(bullet)
                    enemiesToRemove.add(enemy)
                }
            }

            // Remove bullets that are off-screen
            if (bullet.isOffScreen()) {
                bulletsToRemove.add(bullet)
            }
        }

        // Remove collided bullets and enemies
        player.bullets.removeAll(bulletsToRemove)
        enemies.removeAll(enemiesToRemove)

        // Remove bullets that went off-screen
        val remainingBulletsToRemove = mutableListOf<Bullet>()
        for (bullet in player.bullets) {
            bullet.update()
            if (bullet.isOffScreen()) {
                remainingBulletsToRemove.add(bullet)
            }
        }
        player.bullets.removeAll(remainingBulletsToRemove)

        // Update the enemies (move them down)
        for (enemy in enemies) {
            enemy.update()
            if (enemy.x < -enemy.width || enemy.y > screenHeight) {
                enemiesToRemove.add(enemy) // Add to removal list if it's off the screen
            }
        }

        // Now safely remove off-screen enemies after the iteration
        enemies.removeAll(enemiesToRemove)
    }


    // Collision detection between a bullet and an enemy
    fun isCollision(bullet: Bullet, enemy: Enemy): Boolean {
        val bulletRadius = 10f  // Radius of the bullet
        val enemyCenterX = enemy.x
        val enemyCenterY = enemy.y
        val enemyWidth = enemy.width
        val enemyHeight = enemy.height

        // Check if the bullet is within the bounds of the enemy's area (circle vs. rectangle)
        val distanceX = bullet.x - enemyCenterX
        val distanceY = bullet.y - enemyCenterY

        // Use the Pythagorean theorem to check if the bullet is within the enemy's bounding box
        val distance = Math.sqrt((distanceX * distanceX + distanceY * distanceY).toDouble()).toFloat()

        // If the distance between the bullet and enemy is less than the radius + half the width/height, a collision has occurred
        return distance < bulletRadius + Math.max(enemyWidth, enemyHeight) / 2
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Clear screen with a black background
        paint.color = Color.BLACK
        canvas.drawRect(0f, 0f, screenWidth, screenHeight, paint)

        // Draw the player and enemies
        player.draw(canvas, paint)
        for (enemy in enemies) {
            enemy.draw(canvas, paint)
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        gameThread.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        var retry = true
        gameThread.isRunning = false
        while (retry) {
            try {
                gameThread.join()
                retry = false
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                player.x = event.x - player.width / 2
                if (player.x < 0) player.x = 0f
                if (player.x > screenWidth - player.width) player.x = (screenWidth - player.width).toFloat()
            }
            MotionEvent.ACTION_DOWN -> {
                player.shoot()
            }
        }
        return true
    }

    private class GameThread(private val surfaceHolder: SurfaceHolder, private val gameView: GameView) : Thread() {
        var isRunning = false

        override fun run() {
            while (isRunning) {
                val canvas = surfaceHolder.lockCanvas()
                canvas?.let {
                    synchronized(surfaceHolder) {
                        gameView.update()
                        gameView.onDraw(it)
                    }
                    surfaceHolder.unlockCanvasAndPost(it)
                }
            }
        }
    }
}




















