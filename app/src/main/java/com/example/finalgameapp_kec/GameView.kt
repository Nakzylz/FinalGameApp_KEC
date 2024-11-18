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
    private val gameThread: GameThread
    private val player: Player = Player(500f, 1600f)
    private val bullets: MutableList<Bullet> = mutableListOf()
    private val enemies: MutableList<Enemy> = mutableListOf()

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


        bullets.removeAll { it.y < 0 }


        enemies.forEach { it.update() }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.BLACK)  // Background color

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

                        gameView.draw(it)
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

        for (i in 0..4) {
            val enemy = Enemy(100f * i, 100f)
            enemies.add(enemy)
        }
    }
}

