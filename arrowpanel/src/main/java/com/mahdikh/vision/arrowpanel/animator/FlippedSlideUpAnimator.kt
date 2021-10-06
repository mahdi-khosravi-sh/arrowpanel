package com.mahdikh.vision.arrowpanel.animator

import android.view.View

class FlippedSlideUpAnimator : FlipHorizontallyAnimator() {
    var translation = 100.0F

    override fun preAnimateShow(view: View) {
        super.preAnimateShow(view)
        view.translationY = view.y + translation
    }

    override fun animateShowImpl(view: View) {
        super.animateShowImpl(view)

        view.animate().apply {
            translationY(view.translationY - translation)
            interpolator = this@FlippedSlideUpAnimator.interpolator
            duration = this@FlippedSlideUpAnimator.duration
        }.start()
    }
}