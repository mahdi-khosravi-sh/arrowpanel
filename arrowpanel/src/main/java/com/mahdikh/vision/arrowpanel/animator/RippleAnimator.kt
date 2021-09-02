package com.mahdikh.vision.arrowpanel.animator

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Path
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.mahdikh.vision.arrowpanel.widget.ArrowContainer
import kotlin.math.max

open class RippleAnimator : Animator() {
    private val clipPath: Path = Path()
    private var rippleDuration: Long = 650

    init {
        setInterpolator(FastOutSlowInInterpolator())
    }

    override fun animateShow(arrowContainer: ArrowContainer) {
        startRipple(true)
    }

    override fun animateHide(arrowContainer: ArrowContainer) {
        startRipple(false)
    }

    override fun draw(canvas: Canvas) {
        canvas.clipPath(clipPath)
    }

    fun setRippleDuration(rippleDuration: Long): RippleAnimator {
        this.rippleDuration = rippleDuration
        return this
    }

    fun getRippleDuration(): Long {
        return rippleDuration
    }

    private fun startRipple(show: Boolean) {
        val animator = ValueAnimator()
        animator.setFloatValues(0.0F, 1.0F)
        animator.duration = rippleDuration
        animator.interpolator = getInterpolator()

        animator.addUpdateListener {
            clipPath.reset()
            val value: Float = it.animatedValue as Float

            val height = getHeight()
            val width = getWidth()

            var max: Float = max(height, width).toFloat()
            max += max * 0.1F

            if (show) {
                clipPath.addCircle(getPivotX(), getPivotY(), max * value, Path.Direction.CCW)
            } else {
                clipPath.addCircle(getPivotX(), getPivotY(), max * (1 - value), Path.Direction.CCW)
            }
            invalidate()
        }
        animator.start()
    }
}
