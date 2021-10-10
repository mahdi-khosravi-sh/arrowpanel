package com.mahdikh.vision.arrowpanel.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.annotation.CallSuper
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.core.view.isVisible

abstract class Panel(context: Context) : FrameLayout(context), PanelInterface {
    private var mCanceled = false
    private var mDismissed = false
    open var cancelable: Boolean = true
    open var cancelableOnTouchOutside: Boolean = true
        set(value) {
            field = value
            isFocusable = value
        }

    private var onShowListener: PanelInterface.OnShowListener? = null
    private var onDismissListener: PanelInterface.OnDismissListener? = null
    private var onCancelListener: PanelInterface.OnCancelListener? = null

    init {
        isClickable = true
        isFocusable = true
        isFocusableInTouchMode = true
        clipChildren = false
        fitsSystemWindows = false
        super.setLayoutDirection(LAYOUT_DIRECTION_LTR)
        super.setWillNotDraw(false)
    }

    @CallSuper
    open fun show() {
        if (!isShowing()) {
            onShow()
            onShowListener?.onShow(this)
        }
    }

    @CallSuper
    override fun dismiss() {
        if (!mDismissed) {
            onDismiss()
            onDismissListener?.onDismiss(this)
            mDismissed = true
        }
    }

    @CallSuper
    override fun cancel() {
        if (!mCanceled) {
            mCanceled = true
            onCancel()
            onCancelListener?.onCancel(this)
            dismiss()
        }
    }

    protected open fun onShow() {}

    protected open fun onDismiss() {}

    protected open fun onCancel() {}

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (cancelableOnTouchOutside && event!!.action == MotionEvent.ACTION_DOWN) {
            cancel()
        }
        return super.onTouchEvent(event)
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        cancel()
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if (event?.keyCode == KeyEvent.KEYCODE_BACK) {
            if (event.action == KeyEvent.ACTION_UP) {
                onBackPressed()
                return true
            }
        }
        return super.dispatchKeyEvent(event)
    }

    override fun setLayoutDirection(layoutDirection: Int) {

    }

    open fun onBackPressed() {
        if (cancelable) {
            cancel()
        }
    }

    open fun isShowing(): Boolean {
        return isAttachedToWindow and isVisible
    }

    open fun setInteractionTouchOutside(interaction: Boolean) {
        isClickable = !interaction
    }

    open fun setDim(@ColorInt dimColor: Int, @DimDef dimAmount: Float = 0.6F) {
        setBackgroundColor(
            Color.argb(
                (dimAmount * 255).toInt(),
                Color.red(dimColor),
                Color.green(dimColor),
                Color.blue(dimColor),
            )
        )
    }

    open fun setOnShowListener(onShowListener: PanelInterface.OnShowListener?) {
        this.onShowListener = onShowListener
    }

    open fun setOnDismissListener(onDismissListener: PanelInterface.OnDismissListener?) {
        this.onDismissListener = onDismissListener
    }

    open fun setOnCancelListener(onCancelListener: PanelInterface.OnCancelListener?) {
        this.onCancelListener = onCancelListener
    }

    @kotlin.annotation.Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    @FloatRange(from = 0.0, to = 1.0)
    annotation class DimDef
}