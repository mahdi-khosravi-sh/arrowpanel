package com.mahdikh.vision.arrowpanel.animator

import android.view.View

open class ZoomInAnimator : SimpleScaleAnimator() {
    init {
        fromScale = 0.8F
    }

    override fun preAnimateShow(v: View) {
        super.preAnimateShow(v)
        v.apply {
            pivotX = measuredWidth / 2F
            pivotY = measuredHeight / 2F
        }
    }
}