package com.example.finalgameapp_kec

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView

class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {

    private val paint: Paint = Paint()
    private val gameThread: GameThread
    private val player: Player = Player(500f, 1600f, context, 100, 100)
    private val enemies: MutableList<Enemy> = mutableListOf(
        Enemy(300f, 100f, context, 80, 80), // Set enemy size (80x80)
        Enemy(600f, 100f, context, 80, 80)
    )
    private val bullets: MutableList<Bullet> = mutableListOf()

    init {
        holder.addCallback(this)
        gameThread = GameThread(this, holder)
        paint.color = Color.WHITE
        spawnEnemies()
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        gameThread.isRunning = true
        gameThread.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        gameThread.isRunning = false
        try {
            gameThread.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                player.move(event.x)
            }
            MotionEvent.ACTION_DOWN -> {
                shootBullet()
            }
        }
        return true
    }

    fun update() {
        bullets.forEach { it.update() }

        val iterator = bullets.iterator()
        while (iterator.hasNext()) {
            val bullet = iterator.next()
            if (bullet.y < 0) {
                iterator.remove()
            }
        }

        enemies.forEach { it.update() }
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawColor(Color.BLACK)

        player.draw(canvas, paint)

        bullets.forEach { it.draw(canvas, paint) }

        enemies.forEach { it.draw(canvas, paint) }
    }


    private class GameThread(private val gameView: GameView, private val surfaceHolder: SurfaceHolder) : Thread() {
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

    private fun shootBullet() {
        val bullet = Bullet(player.x, player.y)
        bullets.add(bullet)
    }

    private fun spawnEnemies() {
        val context = context // This will be the context from GameView or Activity
        val enemyWidth = 80 // Set a width for the enemy (you can adjust this)
        val enemyHeight = 80 // Set a height for the enemy (you can adjust this)

        for (i in 0..4) {
            // Pass context, newWidth, and newHeight to the Enemy constructor
            val enemy = Enemy(100f * i, 100f, context, enemyWidth, enemyHeight)
            enemies.add(enemy)
        }
    }
}


