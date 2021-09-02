package com.mahdikh.vision.arrowpanel.animator

import android.view.Gravity
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.annotation.IntDef
import com.mahdikh.vision.arrowpanel.widget.ArrowContainer

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

    override fun initBeforeShow(arrowContainer: ArrowContainer) {
        super.initBeforeShow(arrowContainer)
        arrowContainer.alpha = 0.0F
    }

    private fun checkLayoutDirection(arrowContainer: ArrowContainer) {
        when (slideEdge) {
            Gravity.START -> {
                slideEdge =
                    if (arrowContainer.rootView.layoutDirection == View.LAYOUT_DIRECTION_LTR) {
                        Gravity.LEFT
                    } else {
                        Gravity.RIGHT
                    }
            }
            Gravity.END -> {
                slideEdge =
                    if (arrowContainer.rootView.layoutDirection == View.LAYOUT_DIRECTION_LTR) {
                        Gravity.RIGHT
                    } else {
                        Gravity.LEFT
                    }
            }
        }
    }

    override fun animateShow(arrowContainer: ArrowContainer) {
        val x = arrowContainer.x
        val y = arrowContainer.y

        checkLayoutDirection(arrowContainer)

        when (slideEdge) {
            Gravity.TOP -> {
                arrowContainer.translationY = y - slideTranslation
            }
            Gravity.BOTTOM -> {
                arrowContainer.translationY = y + slideTranslation
            }
            Gravity.RIGHT -> {
                arrowContainer.translationX = x + slideTranslation
            }
            Gravity.LEFT -> {
                arrowContainer.translationX = x - slideTranslation
            }
        }

        arrowContainer.animate()
            .alpha(1.0F)
            .translationX(x)
            .translationY(y)
            .setUpdateListener {
                arrowContainer.invalidate()
            }
            .setDuration(getDuration())
            .interpolator = getInterpolator()
    }

    override fun animateHide(arrowContainer: ArrowContainer) {
        var x = arrowContainer.x
        var y = arrowContainer.y

        checkLayoutDirection(arrowContainer)

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

        arrowContainer.animate()
            .alpha(0.0F)
            .translationX(x)
            .translationY(y)
            .setUpdateListener { arrowContainer.invalidate() }
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