package com.mahdikh.vision.arrowpanel.animator

import android.view.View

class RotateInDownAnimator : RotationAnimator() {
    override fun preAnimateShow(v: View) {
        super.preAnimateShow(v)
        v.apply {
            pivotX = measuredWidth / 4F
            pivotY = measuredHeight / 4F
        }
    }
}