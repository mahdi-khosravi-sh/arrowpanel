package com.mahdikh.vision.arrowpanel.touchanimator

import android.view.MotionEvent
import android.view.View

class RotationXTouchAnimator : RotationTouchAnimator {
    constructor() : super()
    constructor(factor: Float) : super(factor)

    override fun onTouchEvent(view: View, ev: MotionEvent) {
        val percentY = ev.y / view.height - 0.5F
        val rx: Float = percentY * factor
        view.rotationX = rx
    }
}