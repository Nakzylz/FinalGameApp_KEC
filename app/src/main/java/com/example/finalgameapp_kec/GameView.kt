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
    private val player: Player = Player(500f, 1600f, context, 100, 100)  // Initialize player
    private val enemies: MutableList<Enemy> = mutableListOf()

    private val screenWidth = context.resources.displayMetrics.widthPixels
    private val screenHeight = context.resources.displayMetrics.heightPixels

    private val gameThread: GameThread

    init {
        holder.addCallback(this)
        gameThread = GameThread(holder, this)
        gameThread.isRunning = true
    }


    fun update() {
        player.update()


        val enemiesToRemove = mutableListOf<Enemy>()
        for (enemy in enemies) {
            enemy.update()
            if (enemy.x < -enemy.width) {
                enemiesToRemove.add(enemy)
            }
        }
        enemies.removeAll(enemiesToRemove)


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


        paint.color = Color.BLACK
        canvas.drawRect(0f, 0f, screenWidth.toFloat(), screenHeight.toFloat(), paint)


        player.draw(canvas, paint)


        for (enemy in enemies) {
            enemy.draw(canvas, paint)
        }


        for (bullet in player.bullets) {
            bullet.draw(canvas, paint)
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


















