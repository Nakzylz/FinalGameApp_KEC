package com.example.finalgameapp_kec

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView

class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {

    private val paint: Paint = Paint()
    private val player: Player = Player(500f, 1600f, context, 100, 200)
    private val enemies: MutableList<Enemy> = mutableListOf()

    private val screenWidth = context.resources.displayMetrics.widthPixels.toFloat()
    private val screenHeight = context.resources.displayMetrics.heightPixels.toFloat()

    private val gameThread: GameThread
    private var enemySpawnTimer = 0

    private var background: Bitmap
    private var backgroundY = 0f

    init {
        // Load the background image
        background = BitmapFactory.decodeResource(context.resources, R.drawable.backdropblacklittlesparkblack)

        val scaleWidth = screenWidth / background.width
        val scaleHeight = screenHeight / background.height
        val scale = Math.max(scaleWidth, scaleHeight)
        val scaledBackground = Bitmap.createScaledBitmap(background, (background.width * scale).toInt(), (background.height * scale).toInt(), false)

        background = scaledBackground

        holder.addCallback(this)
        gameThread = GameThread(holder, this)
        gameThread.isRunning = true
    }

    fun update() {

        player.update()

        // Remove off-screen bullets
        val bulletsToRemove = mutableListOf<PlayerBullet>()
        for (bullet in player.bullets) {
            bullet.update()  // Update bullet position
            if (bullet.isOffScreen()) {
                bulletsToRemove.add(bullet)
            }
        }
        player.bullets.removeAll(bulletsToRemove)  // Remove off-screen bullets

        // Spawn new enemies if there are fewer than 6
        enemySpawnTimer++
        if (enemySpawnTimer > 100 && enemies.size < 6) {  // Allow spawning only if there are fewer than 6 enemies
            val newEnemy = Enemy(0f, 0f, context, 80, 180, screenWidth, screenHeight)
            newEnemy.spawnAtEdge()  // Randomly set its position and target X
            enemies.add(newEnemy)
            enemySpawnTimer = 0
        }

        // Update the enemies and check for collisions
        val enemiesToRemove = mutableListOf<Enemy>()
        val bulletsToRemoveFromEnemies = mutableListOf<PlayerBullet>()

        for (enemy in enemies) {
            enemy.update()
            for (bullet in player.bullets) {
                if (isCollision(bullet, enemy)) {
                    bulletsToRemoveFromEnemies.add(bullet)
                    enemiesToRemove.add(enemy)
                }
            }
        }

        player.bullets.removeAll(bulletsToRemoveFromEnemies)
        enemies.removeAll(enemiesToRemove)

        // Remove enemies that go off-screen
        enemies.removeAll { enemy -> enemy.x < -enemy.width || enemy.y > screenHeight }

        // Move the background to create a scrolling effect
        backgroundY += 5f  // Scroll down
        if (backgroundY >= screenHeight) {
            backgroundY = 0f  // Reset to create a continuous loop
        }
    }

    // Collision detection between a bullet and an enemy
    fun isCollision(bullet: PlayerBullet, enemy: Enemy): Boolean {
        val bulletRadius = 10f
        val enemyCenterX = enemy.x
        val enemyCenterY = enemy.y
        val enemyWidth = enemy.width
        val enemyHeight = enemy.height

        val distanceX = bullet.x - enemyCenterX
        val distanceY = bullet.y - enemyCenterY

        val distance = Math.sqrt((distanceX * distanceX + distanceY * distanceY).toDouble()).toFloat()

        return distance < bulletRadius + Math.max(enemyWidth, enemyHeight) / 2
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw the background image
        canvas.drawBitmap(background, 0f, backgroundY, paint)
        if (backgroundY > 0) {
            canvas.drawBitmap(background, 0f, backgroundY - screenHeight, paint)
        }

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























