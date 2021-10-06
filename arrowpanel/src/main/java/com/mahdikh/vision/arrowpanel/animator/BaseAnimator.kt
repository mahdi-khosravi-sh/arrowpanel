package com.mahdikh.vision.arrowpanel.animator

import android.animation.TimeInterpolator
import android.view.View
import androidx.annotation.CallSuper

abstract class BaseAnimator {
    var interpolator: TimeInterpolator? = null
    var duration: Long = 250

    protected open fun preAnimateShow(view: View) {
        view.alpha = 0.0F
    }

    protected open fun preAnimateHide(view: View) {}

    protected open fun animateShowImpl(view: View) {
        view.animate()
            .alpha(1.0F)
            .setInterpolator(null)
            .setDuration(duration / 2)
            .start()
    }

    protected open fun animateHideImpl(view: View) {
        view.animate()
            .alpha(0.0F)
            .setInterpolator(null)
            .setDuration(duration)
            .start()
    }

    @CallSuper
    open fun animateShow(view: View) {
        preAnimateShow(view)
        animateShowImpl(view)
    }

    @CallSuper
    open fun animateHide(view: View) {
        preAnimateHide(view)
        animateHideImpl(view)
    }
}