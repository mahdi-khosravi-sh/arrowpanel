package com.mahdikh.vision.arrowpanel.animator

import android.animation.Keyframe
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.View

class WiggleAnimator : BaseAnimator() {
    override fun preAnimateShow(view: View) {
        view.pivotX = view.measuredHeight / 2F
        view.pivotY = view.measuredWidth / 2F
    }

    override fun animateShowImpl(view: View) {
        val pRotation = PropertyValuesHolder.ofKeyframe(
            "rotation",
            Keyframe.ofFloat(0.2F, 4.0F),
            Keyframe.ofFloat(0.4F, -4.0F),
            Keyframe.ofFloat(0.6F, 1.0F),
            Keyframe.ofFloat(0.8F, -1.0F),
            Keyframe.ofFloat(1.0F, 0.0F)
        )
        ObjectAnimator.ofPropertyValuesHolder(view, pRotation).apply {
            duration = this@WiggleAnimator.duration
            interpolator = this@WiggleAnimator.interpolator
        }.start()
    }
}