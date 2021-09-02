package com.mahdikh.vision.arrowpanel.widget

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import com.mahdikh.vision.arrowpanel.R
import com.mahdikh.vision.arrowpanel.animator.ScaleAnimator
import com.mahdikh.vision.arrowpanel.touchanimator.ScaleTouchAnimator

class ArrowTip private constructor(context: Context) : ArrowPanel(context) {
    private val textView: TextView

    init {
        setDim(Color.BLACK, 0.0f)
        setInteractionWhenTouchOutside(true)
        val view = setContentView(R.layout.arrowtip_simple_text)
        textView = view.findViewById(R.id.textView)

        setAnimator(
            ScaleAnimator().setInterpolator(OvershootInterpolator()).setDuration(350)
                .setTouchAnimator(
                    ScaleTouchAnimator().setDuration(250).setInterpolator(OvershootInterpolator())
                )
        )
    }

    fun setText(text: String) {
        textView.text = text
    }

    fun setText(text: CharSequence) {
        textView.text = text
    }

    fun setText(resId: Int) {
        textView.setText(resId)
    }

    fun getText(): String {
        return textView.text.toString()
    }

    companion object {
        @JvmStatic
        fun make(context: Context, text: String, targetView: View): ArrowTip {
            return make(context, text, targetView, DURATION_INFINITE)
        }

        @JvmStatic
        fun make(context: Context, text: String): ArrowTip {
            return make(context, text, null, DURATION_INFINITE)
        }

        @JvmStatic
        fun make(
            context: Context,
            text: String,
            @ArrowPanel.Companion.DurationDef duration: Long
        ): ArrowTip {
            return make(context, text, null, duration)
        }

        @JvmStatic
        fun make(
            context: Context,
            text: String,
            targetView: View?,
            @ArrowPanel.Companion.DurationDef duration: Long
        ): ArrowTip {
            val tip = ArrowTip(context)
            tip.targetView = targetView
            tip.timeOutDuration = duration
            tip.setText(text)
            return tip
        }
    }
}