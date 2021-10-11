package com.mahdikh.vision.arrowpanel.touchanimator

import android.view.View
import androidx.annotation.CallSuper

abstract class PivotTouchAnimator : TouchAnimator() {
    var centerPivot: Boolean = true

    @CallSuper
    override fun onTouchDown(view: View) {
        if (centerPivot) {
            view.apply {
                pivotX = measuredWidth / 2F
                pivotY = measuredHeight / 2F
            }
        }
    }
}