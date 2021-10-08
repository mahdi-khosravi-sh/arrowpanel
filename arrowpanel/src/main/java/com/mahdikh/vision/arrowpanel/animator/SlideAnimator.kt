package com.mahdikh.vision.arrowpanel.animator

import android.view.Gravity
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.annotation.IntDef

open class SlideAnimator : FadeAnimator {
    @SlideEdgeDef
    var slideEdge: Int
    var slideTranslation: Float = -25.0F
    var hideReverse: Boolean = false

    constructor() {
        slideEdge = Gravity.BOTTOM
    }

    constructor(@SlideEdgeDef slideEdge: Int) {
        this.slideEdge = slideEdge
    }

    init {
        interpolator = OvershootInterpolator()
        duration = 350
    }

    override fun animateShowImpl(v: View) {
        val x = v.x
        val y = v.y

        when (getExplicitEdge(v)) {
            Gravity.TOP -> {
                v.translationY = y - slideTranslation
            }
            Gravity.BOTTOM -> {
                v.translationY = y + slideTranslation
            }
            Gravity.RIGHT -> {
                v.translationX = x + slideTranslation
            }
            Gravity.LEFT -> {
                v.translationX = x - slideTranslation
            }
        }
        fadeIn(v)
        v.animate().apply {
            translationX(x)
            translationY(y)
            setUpdateListener { v.invalidate() }
            duration = this@SlideAnimator.duration
            interpolator = this@SlideAnimator.interpolator
        }.start()
    }

    override fun animateHideImpl(v: View) {
        var x = v.x
        var y = v.y

        if (hideReverse) {
            when (getExplicitEdge(v)) {
                Gravity.TOP -> {
                    y += slideTranslation
                }
                Gravity.BOTTOM -> {
                    y -= slideTranslation
                }
                Gravity.RIGHT -> {
                    x -= slideTranslation
                }
                Gravity.LEFT -> {
                    x += slideTranslation
                }
            }
        } else {
            when (getExplicitEdge(v)) {
                Gravity.TOP -> {
                    y -= slideTranslation
                }
                Gravity.BOTTOM -> {
                    y += slideTranslation
                }
                Gravity.RIGHT -> {
                    x += slideTranslation
                }
                Gravity.LEFT -> {
                    x -= slideTranslation
                }
            }
        }
        fadeOut(v)
        v.animate().apply {
            translationX(x)
            translationY(y)
            setUpdateListener { v.invalidate() }
            duration = this@SlideAnimator.duration
            interpolator = this@SlideAnimator.interpolator
        }.start()
    }

    private fun getExplicitEdge(view: View): Int {
        return when (slideEdge) {
            Gravity.START -> {
                if (view.rootView.layoutDirection == View.LAYOUT_DIRECTION_LTR) {
                    Gravity.LEFT
                } else {
                    Gravity.RIGHT
                }
            }
            Gravity.END -> {
                if (view.rootView.layoutDirection == View.LAYOUT_DIRECTION_LTR) {
                    Gravity.RIGHT
                } else {
                    Gravity.LEFT
                }
            }
            else -> {
                slideEdge
            }
        }
    }

    @kotlin.annotation.Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    @IntDef(
        Gravity.TOP,
        Gravity.BOTTOM,
        Gravity.LEFT,
        Gravity.RIGHT,
        Gravity.START,
        Gravity.END,
        Gravity.NO_GRAVITY
    )
    annotation class SlideEdgeDef
}