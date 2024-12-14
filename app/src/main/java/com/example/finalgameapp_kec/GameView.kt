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
import android.util.Log
import android.content.SharedPreferences

class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {

    private val paint: Paint = Paint()
    private val player: Player = Player(500f, 1600f, context, 115, 125)
    private val enemies: MutableList<Enemy> = mutableListOf()

    private val screenWidth = context.resources.displayMetrics.widthPixels.toFloat()
    private val screenHeight = context.resources.displayMetrics.heightPixels.toFloat()

    private val gameThread: GameThread
    private var enemySpawnTimer = 0

    private var background: Bitmap
    private var backgroundY = 0f
    private var isGameOver = false

    private var score = 0
    private var highScore = 0
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("GamePrefs", Context.MODE_PRIVATE)

    private var menuBackground: Bitmap
    private var isMenuScreen = true

    private val startButtonWidth = 400f
    private val startButtonHeight = 150f
    private val startButtonX = (screenWidth - startButtonWidth) / 2
    private val startButtonY = screenHeight / 2 + 100f

    init {
        highScore = sharedPreferences.getInt("highScore", 0)

        background = BitmapFactory.decodeResource(context.resources, R.drawable.backdropblacklittlesparkblack)
        val scaleWidth = screenWidth / background.width
        val scaleHeight = screenHeight / background.height
        val scale = Math.max(scaleWidth, scaleHeight)
        val scaledBackground = Bitmap.createScaledBitmap(background, (background.width * scale).toInt(), (background.height * scale).toInt(), false)
        background = scaledBackground


        menuBackground = BitmapFactory.decodeResource(context.resources, R.drawable.menu_background)
        val menuBackgroundScaleWidth = screenWidth / menuBackground.width
        val menuBackgroundScaleHeight = screenHeight / menuBackground.height
        val menuBackgroundScale = Math.max(menuBackgroundScaleWidth, menuBackgroundScaleHeight)
        val scaledMenuBackground = Bitmap.createScaledBitmap(menuBackground, (menuBackground.width * menuBackgroundScale).toInt(), (menuBackground.height * menuBackgroundScale).toInt(), false)
        menuBackground = scaledMenuBackground

        holder.addCallback(this)
        gameThread = GameThread(holder, this)
        gameThread.isRunning = true
    }

    fun update() {
        if (isMenuScreen) return

        if (isGameOver) return
        player.update()

        val playerBulletsToRemove = mutableListOf<PlayerBullet>()
        for (bullet in player.bullets) {
            bullet.update()
            if (bullet.isOffScreen()) {
                playerBulletsToRemove.add(bullet)
            }
        }
        player.bullets.removeAll(playerBulletsToRemove)

        enemySpawnTimer++
        if (enemySpawnTimer > 100 && enemies.size < 6) {
            val newEnemy = Enemy(0f, 0f, context, 80, 120, screenWidth, screenHeight)
            newEnemy.spawnAtEdge()
            enemies.add(newEnemy)
            enemySpawnTimer = 0
        }

        val enemiesToRemove = mutableListOf<Enemy>()
        val enemyBulletsToRemove = mutableListOf<EnemyBullet>()
        val playerBulletsToRemoveList = mutableListOf<PlayerBullet>()

        for (enemy in enemies) {
            enemy.update()

            for (bullet in player.bullets) {
                if (isCollision(bullet, enemy)) {
                    enemiesToRemove.add(enemy)
                    playerBulletsToRemoveList.add(bullet)
                    score += 10
                }
            }

            for (bullet in enemy.bullets) {
                if (isCollision(bullet, player)) {
                    player.takeDamage(10)
                    enemyBulletsToRemove.add(bullet)
                }
            }
        }

        enemies.removeAll(enemiesToRemove)
        player.bullets.removeAll(playerBulletsToRemoveList)
        enemies.forEach { enemy -> enemy.bullets.removeAll(enemyBulletsToRemove) }

        enemies.removeAll { enemy -> enemy.x < -enemy.width || enemy.y > screenHeight }

        backgroundY += 5f
        if (backgroundY >= screenHeight) {
            backgroundY = 0f
        }

        if (player.health <= 0) {
            isGameOver = true
            if (score > highScore) {
                highScore = score
                val editor = sharedPreferences.edit()
                editor.putInt("highScore", highScore)
                editor.apply()
            }
        }
    }

    private fun isCollision(bullet: PlayerBullet, enemy: Enemy): Boolean {
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

    private fun isCollision(bullet: EnemyBullet, player: Player): Boolean {
        val bulletRadius = 5f
        val playerCenterX = player.x + 50
        val playerCenterY = player.y
        val playerWidth = player.width
        val playerHeight = player.height

        val distanceX = bullet.x - playerCenterX
        val distanceY = bullet.y - playerCenterY

        val distance = Math.sqrt((distanceX * distanceX + distanceY * distanceY).toDouble()).toFloat()

        return distance < bulletRadius + Math.max(playerWidth, playerHeight) / 2
    }

    public override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (isMenuScreen) {
            drawMenuScreen(canvas)
            return
        }

        canvas.drawBitmap(background, 0f, backgroundY, paint)
        if (backgroundY > 0) {
            canvas.drawBitmap(background, 0f, backgroundY - screenHeight, paint)
        }

        drawHealthBar(canvas, paint)

        player.draw(canvas, paint)
        for (enemy in enemies) {
            enemy.draw(canvas, paint)
        }

        drawScore(canvas, paint)

        if (isGameOver) {
            drawGameOverScreen(canvas)
        }
    }

    private fun drawHealthBar(canvas: Canvas, paint: Paint) {
        paint.color = Color.GRAY
        canvas.drawRect(20f, 20f, 220f, 60f, paint)


        paint.color = when {
            player.health <= 40 -> Color.RED
            player.health <= 70 -> Color.YELLOW
            else -> Color.GREEN
        }
        canvas.drawRect(20f, 20f, 20f + player.health * 2f, 60f, paint)
    }

    private fun drawScore(canvas: Canvas, paint: Paint) {
        paint.color = Color.WHITE
        paint.textSize = 60f

        val scoreText = "Score: $score"
        val scoreTextWidth = paint.measureText(scoreText)

        val xPosition = screenWidth - scoreTextWidth - 20f
        val yPosition = 60f

        canvas.drawText(scoreText, xPosition, yPosition, paint)
    }

    private fun drawGameOverScreen(canvas: Canvas) {
        paint.color = Color.WHITE
        paint.textSize = 100f

        val gameOverText = "Game Over"
        val gameOverTextWidth = paint.measureText(gameOverText)
        canvas.drawText(gameOverText, (screenWidth - gameOverTextWidth) / 2, screenHeight / 4f, paint)

        val finalScoreText = "Final Score: $score"
        val finalScoreTextWidth = paint.measureText(finalScoreText)
        canvas.drawText(finalScoreText, (screenWidth - finalScoreTextWidth) / 2, screenHeight / 4f + 100f, paint)

        val highScoreText = "High Score: $highScore"
        val highScoreTextWidth = paint.measureText(highScoreText)
        canvas.drawText(highScoreText, (screenWidth - highScoreTextWidth) / 2, screenHeight / 4f + 200f, paint)

        paint.textSize = 50f
        paint.color = Color.BLACK
        canvas.drawRect(startButtonX, startButtonY, startButtonX + startButtonWidth, startButtonY + startButtonHeight, paint)
        paint.color = Color.WHITE
        val buttonText = "Start New Round"
        val buttonTextWidth = paint.measureText(buttonText)
        val buttonTextX = startButtonX + (startButtonWidth - buttonTextWidth) / 2
        val buttonTextY = startButtonY + (startButtonHeight + paint.textSize) / 2
        canvas.drawText(buttonText, buttonTextX, buttonTextY, paint)
    }

    private fun drawMenuScreen(canvas: Canvas) {
        canvas.drawBitmap(menuBackground, 0f, 0f, paint)


        paint.color = Color.WHITE
        paint.textSize = 100f
        val titleText = "SPACESHOOTER"
        val titleWidth = paint.measureText(titleText)
        canvas.drawText(titleText, (screenWidth - titleWidth) / 2, screenHeight / 4f, paint)


        paint.textSize = 50f
        paint.color = Color.BLACK
        canvas.drawRect(startButtonX, startButtonY, startButtonX + startButtonWidth, startButtonY + startButtonHeight, paint)
        paint.color = Color.WHITE
        val buttonText = "Start Game"
        val buttonTextWidth = paint.measureText(buttonText)
        val buttonTextX = startButtonX + (startButtonWidth - buttonTextWidth) / 2
        val buttonTextY = startButtonY + (startButtonHeight + paint.textSize) / 2
        canvas.drawText(buttonText, buttonTextX, buttonTextY, paint)
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
                if (!isMenuScreen && !isGameOver) {
                    player.x = event.x - player.width / 2
                }
            }
            MotionEvent.ACTION_DOWN -> {
                if (isMenuScreen) {
                    if (event.x in startButtonX..(startButtonX + startButtonWidth) && event.y in startButtonY..(startButtonY + startButtonHeight)) {
                        startGame()
                    }
                } else if (isGameOver) {
                    if (event.x in startButtonX..(startButtonX + startButtonWidth) && event.y in startButtonY..(startButtonY + startButtonHeight)) {
                        startNewRound()
                    }
                } else {
                    player.shoot()
                }
            }
        }
        return true
    }

    private fun startGame() {
        isMenuScreen = false
        score = 0
        player.health = 100
        player.x = screenWidth / 2 - player.width / 2
        player.y = screenHeight - player.height - 550f
        player.bullets.clear()
        enemies.clear()
    }

    private fun startNewRound() {
        // Reset game state
        player.health = 100
        player.x = screenWidth / 2 - player.width / 2
        player.y = screenHeight - player.height - 550f
        player.bullets.clear()
        enemies.clear()
        score = 0
        isGameOver = false


        enemySpawnTimer = 0
    }
}







