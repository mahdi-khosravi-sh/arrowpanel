package com.mahdikh.vision.arrowpanel.touchanimator

import android.view.MotionEvent
import android.view.View

class RotationFlatTouchAnimator : RotationTouchAnimator() {
    override fun onTouchEvent(view: View, ev: MotionEvent) {
        val percent = ev.x / view.width - 0.5F
        val r: Float = percent * factor
        view.rotation = r
    }
}