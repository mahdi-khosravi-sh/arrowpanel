package com.mahdikh.vision.arrowpanel.touchanimator

import android.animation.TimeInterpolator
import android.view.MotionEvent
import android.view.View
import androidx.annotation.CallSuper

abstract class TouchAnimator {
    var duration: Long = 200
    var interpolator: TimeInterpolator? = null

    @CallSuper
    open fun animateTouch(view: View, action: Int) {
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                animateTouchDownImpl(view)
            }
            MotionEvent.ACTION_UP -> {
                animateTouchUpImpl(view)
            }
        }
    }

    protected abstract fun animateTouchDownImpl(view: View)

    protected abstract fun animateTouchUpImpl(view: View)
}