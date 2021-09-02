package com.mahdikh.vision.arrowpanel.animator

import android.animation.TimeInterpolator
import android.graphics.Canvas
import androidx.annotation.CallSuper
import com.mahdikh.vision.arrowpanel.touchanimator.TouchAnimator
import com.mahdikh.vision.arrowpanel.widget.ArrowContainer

abstract class Animator {
    private var touchAnimator: TouchAnimator? = null
    private var arrowContainer: ArrowContainer? = null
    private var interpolator: TimeInterpolator? = null
    private var duration: Long = 250

    @CallSuper
    open fun initBeforeShow(arrowContainer: ArrowContainer) {
        this.arrowContainer = arrowContainer
    }

    abstract fun animateShow(arrowContainer: ArrowContainer)

    abstract fun animateHide(arrowContainer: ArrowContainer)

    open fun animateOnTouch(arrowContainer: ArrowContainer, action: Int) {
        touchAnimator?.animateOnTouch(arrowContainer, action)
    }

    open fun draw(canvas: Canvas) {
    }

    fun invalidate() {
        arrowContainer?.invalidate()
    }

    fun getWidth(): Int {
        arrowContainer?.let {
            return it.measuredWidth
        }
        return 0
    }

    fun getHeight(): Int {
        arrowContainer?.let {
            return it.measuredHeight
        }
        return 0
    }

    fun getPivotX(): Float {
        arrowContainer?.let {
            return it.pivotX
        }
        return 0.0F
    }

    fun getPivotY(): Float {
        arrowContainer?.let {
            return it.pivotY
        }
        return 0.0F
    }

    fun setInterpolator(interpolator: TimeInterpolator): Animator {
        this.interpolator = interpolator
        return this
    }

    fun getInterpolator(): TimeInterpolator? {
        return interpolator
    }

    fun setDuration(duration: Long): Animator {
        this.duration = duration
        return this
    }

    fun getDuration(): Long {
        return duration
    }

    fun getArrowContainer(): ArrowContainer? = arrowContainer

    fun setTouchAnimator(touchAnimator: TouchAnimator): Animator {
        this.touchAnimator = touchAnimator
        return this
    }
}