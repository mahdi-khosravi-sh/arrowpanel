package com.mahdikh.vision.arrowpanel.touchanimator

import android.animation.TimeInterpolator
import com.mahdikh.vision.arrowpanel.widget.ArrowContainer

abstract class TouchAnimator {
    private var duration: Long = 300
    private var interpolator: TimeInterpolator? = null

    abstract fun animateOnTouch(arrowContainer: ArrowContainer, action: Int)

    fun setDuration(duration: Long): TouchAnimator {
        this.duration = duration
        return this
    }

    fun getDuration(): Long {
        return duration
    }

    fun setInterpolator(interpolator: TimeInterpolator?): TouchAnimator {
        this.interpolator = interpolator
        return this
    }

    fun getInterpolator(): TimeInterpolator? {
        return interpolator
    }
}