package com.mahdikh.vision.arrowpanel.animator

import android.view.View

open class FadeAnimator : BaseAnimator() {
    override fun preAnimateShow(v: View) {
        v.alpha = 0.0F
    }

    override fun animateShowImpl(v: View) {
        fadeIn(v)
    }

    override fun animateHideImpl(v: View) {
        fadeOut(v)
    }

    protected open fun fadeIn(v: View) {
        fadeIn(v, duration)
    }

    protected open fun fadeOut(v: View) {
        fadeOut(v, duration)
    }

    protected open fun fadeIn(v: View, duration: Long) {
        v.animate().apply {
            alpha(1.0F)
            interpolator = null
            this.duration = duration
        }.start()
    }

    protected open fun fadeOut(v: View, duration: Long) {
        v.animate().apply {
            alpha(0.0F)
            interpolator = null
            this.duration = duration
        }.start()
    }
}