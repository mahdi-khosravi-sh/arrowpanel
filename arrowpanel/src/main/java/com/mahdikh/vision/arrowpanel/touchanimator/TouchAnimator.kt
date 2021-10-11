package com.mahdikh.vision.arrowpanel.touchanimator

import android.view.MotionEvent
import android.view.View

abstract class TouchAnimator {
    fun dispatchTouchEvent(view: View, ev: MotionEvent) {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                onTouchDown(view)
            }
            MotionEvent.ACTION_UP -> {
                onTouchUp(view)
            }
        }
        onTouchEvent(view, ev)
    }

    open fun onTouchDown(view: View) {}

    open fun onTouchEvent(view: View, ev: MotionEvent) {}

    open fun onTouchUp(view: View) {}
}