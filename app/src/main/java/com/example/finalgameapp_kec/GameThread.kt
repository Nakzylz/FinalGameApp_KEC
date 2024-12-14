package com.example.finalgameapp_kec

import android.graphics.Canvas
import android.view.SurfaceHolder

class GameThread(private val surfaceHolder: SurfaceHolder, private val gameView: GameView) : Thread() {

    var isRunning = false
    private val targetFPS = 60
    private val frameTime = 1000 / targetFPS

    override fun run() {
        var lastTime = System.nanoTime()
        var timeToSleep = 0L
        while (isRunning) {
            val now = System.nanoTime()
            val elapsedTime = now - lastTime
            timeToSleep = (frameTime - elapsedTime / 1000000).coerceAtLeast(0)

            if (elapsedTime >= frameTime * 1000000) {
                val canvas = surfaceHolder.lockCanvas()
                canvas?.let {
                    synchronized(surfaceHolder) {
                        gameView.update()
                        gameView.onDraw(it)
                    }
                    surfaceHolder.unlockCanvasAndPost(it)
                }
                lastTime = now
            }

            if (timeToSleep > 0) {
                try {
                    Thread.sleep(timeToSleep)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
    }
}


