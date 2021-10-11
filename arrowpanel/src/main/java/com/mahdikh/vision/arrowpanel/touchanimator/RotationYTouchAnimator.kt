package com.mahdikh.vision.arrowpanel.touchanimator

import android.view.MotionEvent
import android.view.View

class RotationYTouchAnimator : RotationTouchAnimator() {
    override fun onTouchEvent(view: View, ev: MotionEvent) {
        val percentX = ev.x / view.width - 0.5F
        val ry: Float = percentX * factor
        view.rotationY = -ry
    }
}