package com.mahdikh.vision.arrowpanel.touchanimator

import android.view.MotionEvent
import android.view.View

class RotationYTouchAnimator : RotationTouchAnimator {
    constructor() : super()
    constructor(factor: Float) : super(factor)

    override fun onTouchEvent(view: View, ev: MotionEvent) {
        val percentX = ev.x / view.width - 0.5F
        val ry: Float = percentX * factor
        view.rotationY = -ry
    }
}