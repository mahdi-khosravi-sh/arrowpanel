package com.mahdikh.vision.arrowpanel.animator

import android.view.Gravity
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.annotation.IntDef

open class SlideAnimator : BaseAnimator {
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

    override fun animateShowImpl(view: View) {
        val x = view.x
        val y = view.y

        when (getExplicitEdge(view)) {
            Gravity.TOP -> {
                view.translationY = y - slideTranslation
            }
            Gravity.BOTTOM -> {
                view.translationY = y + slideTranslation
            }
            Gravity.RIGHT -> {
                view.translationX = x + slideTranslation
            }
            Gravity.LEFT -> {
                view.translationX = x - slideTranslation
            }
        }
        view.animate().apply {
            alpha(1.0F)
            translationX(x)
            translationY(y)
            setUpdateListener { view.invalidate() }
            duration = this@SlideAnimator.duration
            interpolator = this@SlideAnimator.interpolator
        }.start()
    }

    override fun animateHideImpl(view: View) {
        var x = view.x
        var y = view.y

        if (hideReverse) {
            when (getExplicitEdge(view)) {
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
            when (getExplicitEdge(view)) {
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
        super.animateHideImpl(view)
        view.animate().apply {
            translationX(x)
            translationY(y)
            setUpdateListener { view.invalidate() }
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