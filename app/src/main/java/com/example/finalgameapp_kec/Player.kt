package com.example.finalgameapp_kec

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint

class Player(var x: Float, var y: Float, context: Context, newWidth: Int, newHeight: Int) {
    private val bitmap: Bitmap = scaleBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.playerprefabplaceholder), newWidth, newHeight)

    val width: Int = bitmap.width
    val height: Int = bitmap.height

    // Function to scale the bitmap to desired size
    private fun scaleBitmap(bitmap: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false)
    }

    // Draw method
    fun draw(canvas: Canvas, paint: Paint) {
        canvas.drawBitmap(bitmap, x - width / 2f, y - height / 2f, paint)
    }

    // Move the player
    fun move(newX: Float) {
        x = newX
    }
}




