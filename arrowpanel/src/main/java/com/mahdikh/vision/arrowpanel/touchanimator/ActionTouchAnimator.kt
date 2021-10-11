package com.mahdikh.vision.arrowpanel.touchanimator

import android.animation.TimeInterpolator
import android.view.View

abstract class ActionTouchAnimator : PivotTouchAnimator() {
    var duration: Long = 200
    var interpolator: TimeInterpolator? = null

    override fun onTouchDown(view: View) {
        super.onTouchDown(view)
        animateTouchDownImpl(view)
    }

    override fun onTouchUp(view: View) {
        animateTouchUpImpl(view)
    }

    abstract fun animateTouchDownImpl(view: View)

    abstract fun animateTouchUpImpl(view: View)
}