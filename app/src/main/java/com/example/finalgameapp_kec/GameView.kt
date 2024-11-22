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
        if (enemySpawnTimer > 100) {  // Adjust the number for more/less frequent spawns
            val newEnemy = Enemy(0f, 0f, context, 80, 80, screenWidth, screenHeight)  // Reasonable size for enemies
            newEnemy.spawnAtEdge()  // Randomly set its position and target X
            enemies.add(newEnemy)
            enemySpawnTimer = 0
        }

        // Update enemies
        val enemiesToRemove = mutableListOf<Enemy>()
        for (enemy in enemies) {
            enemy.update()
            if (enemy.x < -enemy.width) {
                enemiesToRemove.add(enemy)
            }
        }
        enemies.removeAll(enemiesToRemove)

        // Update player bullets
        val bulletsToRemove = mutableListOf<Bullet>()
        for (bullet in player.bullets) {
            bullet.update()
            if (bullet.isOffScreen()) {
                bulletsToRemove.add(bullet)
            }
        }
        player.bullets.removeAll(bulletsToRemove)
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




















