package com.mahdikh.vision.arrowpanel.animator

import android.view.View

open class FlippedSlideDownAnimator : FlipHorizontallyAnimator() {
    var translation = -100.0F

    override fun preAnimateShow(v: View) {
        super.preAnimateShow(v)
        v.translationY = v.y + translation
    }

    override fun animateShowImpl(v: View) {
        super.animateShowImpl(v)
        v.animate().apply {
            translationY(v.translationY - translation)
            interpolator = this@FlippedSlideDownAnimator.interpolator
            duration = this@FlippedSlideDownAnimator.duration
        }.start()
    }
}