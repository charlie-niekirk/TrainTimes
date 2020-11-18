package com.cniekirk.traintimes.utils

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.view.View
import androidx.core.graphics.drawable.toDrawable
import androidx.renderscript.Allocation
import androidx.renderscript.Element
import androidx.renderscript.RenderScript
import androidx.renderscript.ScriptIntrinsicBlur
import timber.log.Timber

object Blur {

    fun createBlur(activity: Activity, view: View, blurRadius: Float = 25f): BitmapDrawable? {

        val displayMetrics = activity.resources?.displayMetrics
        val displayWidth = displayMetrics?.widthPixels ?: 0
        val displayHeight = ((displayMetrics?.heightPixels ?: 0) - 64).coerceAtLeast(0)
        val viewWidth = view.width
        val viewHeight = view.height
        val width = if (viewWidth == 0) displayWidth else viewWidth
        val height = if (viewHeight == 0) displayHeight else viewHeight
        if (width == 0) return null
        if (height == 0) return null

        return try {
            val bitmap = createBitmapFromView(width, height, view, Color.parseColor("#ffffffff"))
            val blurredBitmap = renderBlur(activity.baseContext, bitmap, blurRadius)
            bitmap.recycle()
            blurredBitmap.toDrawable(activity.resources)
        } catch (ex: OutOfMemoryError) {
            Runtime.getRuntime().gc()
            Timber.e("Could not blur, out of memory")
            null
        } catch (ex: IllegalArgumentException) {
            Timber.e("Could not blur, invalid args for creating bitmap: ${ex.localizedMessage}")
            null
        }

    }

    private fun createBitmapFromView(width: Int, height: Int, view: View, color: Int): Bitmap {

        val returnedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        //val bgDrawable = view.background
        canvas.drawColor(color)
        view.draw(canvas)

        return returnedBitmap

    }

    private fun renderBlur(context: Context, inBitmap: Bitmap, blurRadius: Float): Bitmap {

        val bmp = inBitmap.copy(inBitmap.config, true)
        val renderScript = RenderScript.create(context)
        val input = Allocation.createFromBitmap(renderScript, inBitmap,
            Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT)
        val output = Allocation.createTyped(renderScript, input.type)
        val script = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))

        when (blurRadius) {
            in 0f..25f -> script.setRadius(blurRadius)
            else -> script.setRadius(25f)
        }

        script.setInput(input)
        script.forEach(output)
        output.copyTo(bmp)

        val paint = Paint()
        val filter = LightingColorFilter(-0x333333, 0x111111)
        paint.colorFilter = filter
        val canvas = Canvas(bmp)
        canvas.drawBitmap(bmp, 0f, 0f, paint)

        script.destroy()
        input.destroy()
        output.destroy()
        renderScript.destroy()

        return bmp

    }

}