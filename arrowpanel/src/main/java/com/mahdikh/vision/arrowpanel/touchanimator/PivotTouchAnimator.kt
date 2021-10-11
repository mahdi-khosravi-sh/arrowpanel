package com.mahdikh.vision.arrowpanel.touchanimator

import android.view.View
import androidx.annotation.CallSuper

abstract class PivotTouchAnimator : TouchAnimator() {
    var centerPivot: Boolean = true

    @CallSuper
    override fun onTouchDown(view: View) {
        if (centerPivot) {
            view.apply {
                pivotX = width / 2F
                pivotY = width / 2F
            }
        }
    }
}