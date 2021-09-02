package com.mahdikh.vision.arrowpanel.touchanimator

import android.view.MotionEvent
import com.mahdikh.vision.arrowpanel.widget.ArrowContainer

class AlphaTouchAnimator : TouchAnimator() {
    private var toAlpha: Float = 0.85F

    fun setToAlpha(toAlpha: Float): AlphaTouchAnimator {
        this.toAlpha = toAlpha
        return this
    }

    override fun animateOnTouch(arrowContainer: ArrowContainer, action: Int) {
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                arrowContainer.animate()
                    .alpha(toAlpha)
                    .setDuration(getDuration())
                    .interpolator = getInterpolator()
            }
            MotionEvent.ACTION_UP -> {
                arrowContainer.animate()
                    .alpha(1.0F)
                    .setDuration(getDuration())
                    .interpolator = getInterpolator()
            }
        }
    }
}