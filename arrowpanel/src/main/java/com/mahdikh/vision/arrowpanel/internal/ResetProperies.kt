package com.mahdikh.vision.arrowpanel.internal

import android.view.View

internal fun clear(view: View) {
    view.apply {
        alpha = 1.0F
        scaleY = 1.0F
        scaleX = 1.0F
        rotation = 0.0F
        rotationX = 0.0F
        rotationY = 0.0F
        translationX = 0.0F
        translationY = 0.0F
        pivotX = width / 2F
        pivotY = height / 2F
    }
}