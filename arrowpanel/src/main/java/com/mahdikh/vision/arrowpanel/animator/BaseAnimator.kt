package com.mahdikh.vision.arrowpanel.animator

import android.animation.TimeInterpolator
import android.view.View
import androidx.annotation.CallSuper

abstract class BaseAnimator {
    var interpolator: TimeInterpolator? = null
    var duration: Long = 250
    var endHideAction: Runnable? = null

    protected open fun preAnimateShow(v: View) {}

    protected open fun preAnimateHide(v: View) {}

    protected open fun animateShowImpl(v: View) {}

    protected open fun animateHideImpl(v: View) {}

    @CallSuper
    open fun animateShow(v: View) {
        preAnimateShow(v)
        animateShowImpl(v)
    }

    @CallSuper
    open fun animateHide(v: View) {
        v.animate().withEndAction { onEndHide(v) }
        preAnimateHide(v)
        animateHideImpl(v)
    }

    open fun onEndHide(v: View) {
        endHideAction?.run()
    }
}