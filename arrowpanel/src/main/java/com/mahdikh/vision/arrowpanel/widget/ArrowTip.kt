package com.mahdikh.vision.arrowpanel.widget

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.annotation.LongDef
import com.mahdikh.vision.arrowpanel.R
import com.mahdikh.vision.arrowpanel.animator.ScaleAnimator
import com.mahdikh.vision.arrowpanel.touchanimator.ScaleTouchAnimator

class ArrowTip private constructor(context: Context) : ArrowPanel(context) {
    private val textView: TextView
    private val dismissRunnable = Runnable { dismiss() }

    @DurationDef
    var timeOutDuration: Long = DURATION_INFINITE
        set(value) {
            if (value == DURATION_INFINITE && field != DURATION_INFINITE) {
                removeCallbacks(dismissRunnable)
            }
            field = value
        }

    init {
        setDim(Color.BLACK, 0.0f)
        interactionTouchOutside = true
        val view = setContentView(R.layout.arrowtip_simple_text)
        textView = view.findViewById(R.id.textView)

        setAnimator(ScaleAnimator().apply { duration = 350 })
        setTouchAnimator(ScaleTouchAnimator())
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

    fun resetTimeout() {
        removeCallbacks(dismissRunnable)
        if (timeOutDuration != DURATION_INFINITE) {
            postDelayed(dismissRunnable, timeOutDuration)
        }
    }

    override fun onShow() {
        super.onShow()
        if (timeOutDuration != DURATION_INFINITE) {
            postDelayed(dismissRunnable, timeOutDuration)
        }
    }

    companion object {
        const val DURATION_INFINITE: Long = -1
        const val DURATION_SHORT: Long = 4000
        const val DURATION_MEDIUM: Long = 5500
        const val DURATION_LONG: Long = 7000

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
            @DurationDef duration: Long
        ): ArrowTip {
            return make(context, text, null, duration)
        }

        @JvmStatic
        fun make(
            context: Context,
            text: String,
            targetView: View?,
            @DurationDef duration: Long
        ): ArrowTip {
            val tip = ArrowTip(context)
            tip.targetView = targetView
            tip.timeOutDuration = duration
            tip.setText(text)
            return tip
        }

        @kotlin.annotation.Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
        @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
        @LongDef(DURATION_INFINITE, DURATION_SHORT, DURATION_MEDIUM, DURATION_LONG)
        annotation class DurationDef
    }
}