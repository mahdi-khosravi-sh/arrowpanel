package com.mahdikh.vision.arrowpanel.animator

import android.view.View

class RotateInUpAnimator : RotationAnimator() {
    override fun preAnimateShow(v: View) {
        super.preAnimateShow(v)
        v.apply {
            val w = measuredWidth
            val h = measuredHeight

            pivotX = w - w / 4F
            pivotY = h - h / 4F
        }
    }
}