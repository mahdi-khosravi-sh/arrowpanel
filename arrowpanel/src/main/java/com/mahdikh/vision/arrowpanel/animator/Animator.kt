package com.mahdikh.vision.arrowpanel.animator

import android.animation.TimeInterpolator
import android.graphics.Canvas
import android.view.View
import androidx.annotation.CallSuper
import com.mahdikh.vision.arrowpanel.touchanimator.TouchAnimator
import com.mahdikh.vision.arrowpanel.widget.ArrowContainer

abstract class Animator {
    private var touchAnimator: TouchAnimator? = null
    private var view: View? = null
    private var interpolator: TimeInterpolator? = null
    private var duration: Long = 250

    @CallSuper
    open fun initBeforeShow(view: View) {
        this.view = view
    }

    abstract fun animateShow(view: View)

    abstract fun animateHide(view: View)

    open fun animateOnTouch(view: View, action: Int) {
        touchAnimator?.animateOnTouch(view, action)
    }

    open fun draw(canvas: Canvas) {
    }

    fun invalidate() {
        view?.invalidate()
    }

    fun getWidth(): Int {
        view?.let {
            return it.measuredWidth
        }
        return 0
    }

    fun getHeight(): Int {
        view?.let {
            return it.measuredHeight
        }
        return 0
    }

    fun getPivotX(): Float {
        view?.let {
            return it.pivotX
        }
        return 0.0F
    }

    fun getPivotY(): Float {
        view?.let {
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

    fun setTouchAnimator(touchAnimator: TouchAnimator): Animator {
        this.touchAnimator = touchAnimator
        return this
    }
}