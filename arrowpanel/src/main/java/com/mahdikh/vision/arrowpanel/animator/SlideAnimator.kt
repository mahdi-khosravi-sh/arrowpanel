package com.mahdikh.vision.arrowpanel.animator

import android.view.Gravity
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.annotation.IntDef

open class SlideAnimator : Animator {
    @SlideEdgeDef
    var slideEdge: Int
    var slideTranslation: Float = 25.0F
    var hideReverse: Boolean = true

    init {
        setInterpolator(OvershootInterpolator())
        setDuration(350)
    }

    constructor() {
        slideEdge = Gravity.TOP
    }

    constructor(@SlideEdgeDef slideEdge: Int) {
        this.slideEdge = slideEdge
    }

    override fun initBeforeShow(view: View) {
        super.initBeforeShow(view)
        view.alpha = 0.0F
    }

    private fun checkLayoutDirection(view: View) {
        when (slideEdge) {
            Gravity.START -> {
                slideEdge =
                    if (view.rootView.layoutDirection == View.LAYOUT_DIRECTION_LTR) {
                        Gravity.LEFT
                    } else {
                        Gravity.RIGHT
                    }
            }
            Gravity.END -> {
                slideEdge =
                    if (view.rootView.layoutDirection == View.LAYOUT_DIRECTION_LTR) {
                        Gravity.RIGHT
                    } else {
                        Gravity.LEFT
                    }
            }
        }
    }

    override fun animateShow(view: View) {
        val x = view.x
        val y = view.y

        checkLayoutDirection(view)

        when (slideEdge) {
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

        view.animate()
            .alpha(1.0F)
            .translationX(x)
            .translationY(y)
            .setUpdateListener {
                view.invalidate()
            }
            .setDuration(getDuration())
            .interpolator = getInterpolator()
    }

    override fun animateHide(view: View) {
        var x = view.x
        var y = view.y

        checkLayoutDirection(view)

        if (hideReverse) {
            when (slideEdge) {
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
            when (slideEdge) {
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

        view.animate()
            .alpha(0.0F)
            .translationX(x)
            .translationY(y)
            .setUpdateListener { view.invalidate() }
            .setDuration(getDuration())
            .interpolator = getInterpolator()
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