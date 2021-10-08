package com.mahdikh.vision.arrowpanel.animator

import android.view.View

open class UnMagnifyAnimator : SimpleScaleAnimator() {
    init {
        fromScale = 1.25F
    }

    override fun preAnimateShow(v: View) {
        super.preAnimateShow(v)
        v.apply {
            pivotX = measuredWidth / 2F
            pivotY = measuredHeight / 2F
        }
    }
}