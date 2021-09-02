package com.mahdikh.vision.arrowpanel.touchanimator

import android.view.MotionEvent
import com.mahdikh.vision.arrowpanel.widget.ArrowContainer

class ScaleTouchAnimator : TouchAnimator() {
    private var toScale: Float = 0.9F

    fun setToScale(toScale: Float): ScaleTouchAnimator {
        this.toScale = toScale
        return this
    }

    override fun animateOnTouch(arrowContainer: ArrowContainer, action: Int) {
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                arrowContainer.animate()
                    .scaleX(toScale)
                    .scaleY(toScale)
                    .setInterpolator(getInterpolator())
                    .duration = getDuration()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                arrowContainer.animate()
                    .scaleX(1.0F)
                    .scaleY(1.0F)
                    .setInterpolator(getInterpolator())
                    .duration = getDuration()
            }
        }
    }
}