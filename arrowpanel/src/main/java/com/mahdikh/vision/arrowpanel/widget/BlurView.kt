package com.mahdikh.vision.arrowpanel.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.LayerDrawable
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.AttributeSet
import android.view.View
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import java.util.*

class BlurView : View {
    private val noBlurViews: MutableList<View> = mutableListOf()
    private var overlapView: View? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun asyncBlur(
        sourceView: View,
        quality: Int,
        radius: Float,
        cleverBlur: Boolean,
        endAction: Runnable?
    ) {
        val visibility = visibility
        setVisibility(INVISIBLE)
        val visibilities: List<Int> = getNoBlurViewsVisibilities()
        setNoBlurViewsInvisible()

        Thread {
            val sourceBitmap: Bitmap? = getBitmap(sourceView, quality, cleverBlur)
            this@BlurView.post {
                setVisibility(visibility)
                setNoBlurViewsVisibilities(visibilities)
            }
            val blurBitmap = blur(context, sourceBitmap, radius)
            post {
                adjustBackground(blurBitmap)
                endAction?.run()
            }
        }.start()
    }

    fun asyncBlur(
        sourceView: View,
        @QualityDef quality: Int,
        radius: Float,
        withEndAction: Runnable?
    ) {
        asyncBlur(sourceView, quality, radius, true, withEndAction!!)
    }

    fun asyncBlur(sourceView: View, @QualityDef quality: Int, radius: Float) {
        asyncBlur(sourceView, quality, radius, true, null)
    }

    private fun blur(context: Context, source: Bitmap?, radius: Float): Bitmap {
        val outputBitmap = Bitmap.createBitmap(source!!)
        val renderScript = RenderScript.create(context)
        renderScript.setPriority(RenderScript.Priority.NORMAL)
        val tmpIn = Allocation.createFromBitmap(
            renderScript,
            source,
            Allocation.MipmapControl.MIPMAP_NONE,
            Allocation.USAGE_SCRIPT
        )
        val tmpOut = Allocation.createFromBitmap(renderScript, outputBitmap)
        val intrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
        intrinsicBlur.setRadius(radius)
        intrinsicBlur.setInput(tmpIn)
        intrinsicBlur.forEach(tmpOut)
        tmpOut.copyTo(outputBitmap)
        source.recycle()
        renderScript.destroy()
        return outputBitmap
    }

    fun blur(
        sourceView: View,
        @QualityDef quality: Int,
        @RadiusDef radius: Float,
        cleverBlur: Boolean
    ) {
        val visibility = visibility
        setVisibility(INVISIBLE)
        val visibilities = getNoBlurViewsVisibilities()
        setNoBlurViewsInvisible()
        val sourceBitmap = getBitmap(sourceView, quality, cleverBlur)
        setVisibility(visibility)
        setNoBlurViewsVisibilities(visibilities)
        val blurBitmap = blur(context, sourceBitmap, radius)
        adjustBackground(blurBitmap)
    }

    fun blur(sourceView: View, @QualityDef quality: Int, radius: Float) {
        blur(sourceView, quality, radius, true)
    }

    private fun getBitmap(sourceView: View, quality: Int, clever: Boolean): Bitmap? {
        val sourceBitmap: Bitmap? = if (clever) {
            val rect = Rect(left, top, right, bottom)
            drawToBitmap(sourceView, rect, quality.toFloat())
        } else {
            drawToBitmap(sourceView, null, quality.toFloat())
        }
        return sourceBitmap
    }

    private fun setNoBlurViewsVisibilities(visibilities: List<Int>) {
        for ((i, view) in noBlurViews.withIndex()) {
            view.visibility = visibilities[i]
        }
    }

    private fun getNoBlurViewsVisibilities(): List<Int> {
        val visibilities: MutableList<Int> = ArrayList()
        for (view in noBlurViews) {
            visibilities.add(view.visibility)
        }
        return visibilities
    }

    private fun setNoBlurViewsInvisible() {
        for (view in noBlurViews) {
            view.visibility = INVISIBLE
        }
    }

    private fun setVisibility(view: View, visibility: Int) {
        view.visibility = visibility
    }

    private fun adjustBackground(bitmap: Bitmap) {
        background = if (overlapView != null) {
            LayerDrawable(
                arrayOf(BitmapDrawable(resources, bitmap), overlapView!!.background)
            )
        } else {
            BitmapDrawable(resources, bitmap)
        }
    }

    fun addNoBlurView(view: View) {
        if (view == this) return
        if (!noBlurViews.contains(view)) {
            noBlurViews.add(view)
        }
    }

    fun removeNoBlurView(view: View) {
        noBlurViews.remove(view)
    }

    fun setOverlapView(overlapView: View?) {
        this.overlapView = overlapView
    }

    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    @IntRange(from = 0, to = 100)
    annotation class QualityDef

    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    @FloatRange(from = 1.0, to = 25.0)
    annotation class RadiusDef

    companion object {
        @JvmStatic
        private fun drawToBitmap(
            sourceView: View,
            rect: Rect?,
            @QualityDef quality: Float
        ): Bitmap? {
            var bitmap =
                Bitmap.createBitmap(sourceView.width, sourceView.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            sourceView.draw(canvas)
            if (rect != null) {
                bitmap =
                    Bitmap.createBitmap(bitmap, rect.left, rect.top, rect.width(), rect.height())
            }
            if (quality < 100) {
                val q = quality / 100f
                return Bitmap.createScaledBitmap(
                    bitmap,
                    (bitmap.width * q).toInt(), (bitmap.height * q).toInt(), false
                )
            }
            return bitmap
        }
    }
}