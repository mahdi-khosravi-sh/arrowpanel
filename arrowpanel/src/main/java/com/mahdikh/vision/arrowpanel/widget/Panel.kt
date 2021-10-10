package com.mahdikh.vision.arrowpanel.widget

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
    open var interactionTouchOutside = false
    open var cancelableOnTouchOutside: Boolean = true
        set(value) {
            field = value
            isFocusable = value
        }

    private var showListeners: MutableList<PanelInterface.OnShowListener>? = null
    private var dismissListeners: MutableList<PanelInterface.OnDismissListener>? = null
    private var cancelListeners: MutableList<PanelInterface.OnCancelListener>? = null

    init {
        isFocusable = true
        isFocusableInTouchMode = true
        clipChildren = false
        fitsSystemWindows = false
        super.setClickable(false)
        super.setLayoutDirection(LAYOUT_DIRECTION_LTR)
        super.setWillNotDraw(false)
    }

    @CallSuper
    open fun show() {
        mCanceled = false
        mDismissed = false
        if (isShowing()) return
        onShow()
        showListeners?.let { it ->
            val size = it.size
            for (i in 0 until size) {
                it[i].onShow(this)
            }
        }
    }

    @CallSuper
    override fun dismiss() {
        if (!mDismissed) {
            onDismiss()
            dismissListeners?.let { it ->
                val size = it.size
                for (i in 0 until size) {
                    it[i].onDismiss(this)
                }
            }
            mDismissed = true
        }
    }

    @CallSuper
    override fun cancel() {
        if (!mCanceled) {
            mCanceled = true
            onCancel()
            cancelListeners?.let { it ->
                val size = it.size
                for (i in 0 until size) {
                    it[i].onCancel(this)
                }
            }
            dismiss()
        }
    }

    protected open fun onShow() {}

    protected open fun onDismiss() {}

    protected open fun onCancel() {}

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (cancelableOnTouchOutside && event.action == MotionEvent.ACTION_DOWN) {
            cancel()
        }
        if (interactionTouchOutside) {
            return super.onTouchEvent(event)
        }
        return true
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

    override fun setClickable(clickable: Boolean) {

    }

    open fun onBackPressed() {
        if (cancelable) {
            cancel()
        }
    }

    open fun isShowing(): Boolean {
        return isAttachedToWindow and isVisible
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

    fun addOnShowListener(onShowListener: PanelInterface.OnShowListener) {
        if (showListeners == null) {
            showListeners = mutableListOf()
        }
        showListeners?.add(onShowListener)
    }

    fun addOnDismissListener(onDismissListener: PanelInterface.OnDismissListener) {
        if (dismissListeners == null) {
            dismissListeners = mutableListOf()
        }
        dismissListeners?.add(onDismissListener)
    }

    fun addOnCancelListener(onCancelListener: PanelInterface.OnCancelListener) {
        if (cancelListeners == null) {
            cancelListeners = mutableListOf()
        }
        cancelListeners?.add(onCancelListener)
    }

    fun removeOnShowListener(onShowListener: PanelInterface.OnShowListener) {
        showListeners?.let {
            it.remove(onShowListener)
            if (it.size == 0) {
                showListeners = null
            }
        }
    }

    fun removeOnDismissListener(onDismissListener: PanelInterface.OnDismissListener) {
        dismissListeners?.let {
            it.remove(onDismissListener)
            if (it.isEmpty()) {
                dismissListeners = null
            }
        }
    }

    fun removeOnCancelListener(onCancelListener: PanelInterface.OnCancelListener) {
        cancelListeners?.let {
            it.remove(onCancelListener)
            if (it.isEmpty()) {
                cancelListeners = null
            }
        }
    }

    fun removeAllShowListeners() {
        (showListeners ?: return).clear()
        showListeners = null
    }

    fun removeAllDismissListeners() {
        (dismissListeners ?: return).clear()
        dismissListeners = null
    }

    fun removeAllCancelListeners() {
        (cancelListeners ?: return).clear()
        cancelListeners = null
    }

    @kotlin.annotation.Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    @FloatRange(from = 0.0, to = 1.0)
    annotation class DimDef
}