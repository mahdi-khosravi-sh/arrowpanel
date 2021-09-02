package com.mahdikh.vision.arrowpanel.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Color
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.annotation.LayoutRes
import androidx.annotation.LongDef
import androidx.fragment.app.FragmentActivity
import com.mahdikh.vision.arrowpanel.animator.Animator

open class ArrowPanel constructor(context: Context) : FrameLayout(context) {
    private val targetLocation: IntArray = IntArray(2)
    var drawTargetView: Boolean = true
    var cancelableOnTouchOutside: Boolean = true
    var cancelable: Boolean = true
    var blurQuality: Int = 10
    var blurRadius: Float = 5.0F
    var arrowMargin: Int = 5

    @DurationDef
    var timeOutDuration: Long = DURATION_INFINITE
        set(value) {
            if (value == DURATION_INFINITE && field != DURATION_INFINITE) {
                removeCallbacks(dismissRunnable)
            }
            field = value
        }
    var orientation = ORIENTATION_HORIZONTAL or ORIENTATION_VERTICAL

    var targetView: View? = null
    private var blurView: BlurView? = null
    private var arrowContainer: ArrowContainer

    private var onShowListener: OnShowListener? = null
    private var onDismissListener: OnDismissListener? = null
    private var onCancelListener: OnCancelListener? = null
    private val dismissRunnable = Runnable { dismiss() }

    init {
        alpha = 0.0F
        isClickable = true
        isFocusable = true
        clipChildren = false
        arrowContainer = ArrowContainer(context).also {
            it.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        }
        layoutDirection = LAYOUT_DIRECTION_LTR
        super.setWillNotDraw(false)
    }

    protected open fun createBlurView() {
        blurView = BlurView(context).also {
            it.addNoBlurView(this)
            it.setOverlapView(this)
            it.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        }
        addView(blurView, 0)
    }

    fun setAnimator(animator: Animator?) {
        arrowContainer.animator = animator
    }

    open fun show() {
        getRootViewGroup()?.let { rootView ->
            addView(arrowContainer)
            rootView.addView(this)

            if (targetView != null) {
                arrowContainer.targetView = targetView
            } else {
                arrowContainer.setTargetLocation(targetLocation[0], targetLocation[1])
            }

            adjustArrowLayoutLocation()

            animate().alpha(1.0F).duration = 150

            arrowContainer.show()

            onShowListener?.onShow(this)
            blurView?.blur(rootView, blurQuality, blurRadius, false)

            if (timeOutDuration != DURATION_INFINITE) {
                postDelayed(dismissRunnable, timeOutDuration)
            }
        }
    }

    fun setContentView(layoutId: Int): View {
        return arrowContainer.inflateLayout(layoutId)
    }

    fun setContentView(view: View): View {
        arrowContainer.removeAllViews()
        arrowContainer.addView(view)
        return view
    }

    open fun resetTimeout() {
        removeCallbacks(dismissRunnable)
        if (timeOutDuration != DURATION_INFINITE) {
            postDelayed(dismissRunnable, timeOutDuration)
        }
    }

    open fun show(targetView: View) {
        this.targetView = targetView
        show()
    }

    open fun show(motionEvent: MotionEvent) {
        targetLocation[0] = motionEvent.rawX.toInt()
        targetLocation[1] = motionEvent.rawY.toInt()
        show()
    }

    private fun adjustArrowLayoutLocation() {
        measure(0, 0)

        val pWidth = rootView.width
        val pHeight = rootView.height

        val arrowLayoutWidth = arrowContainer.measuredWidth
        val arrowLayoutHeight = arrowContainer.measuredHeight

        targetView?.let { target ->
            target.getLocationInWindow(targetLocation)

            var y: Float
            var x: Float
            var centerY = false

            when (orientation) {
                ORIENTATION_HORIZONTAL -> {
                    y = targetLocation[1] + target.measuredHeight / 2F - (arrowLayoutHeight / 2F)
                    centerY = true
                }
                else -> {
                    when {
                        arrowLayoutHeight < targetLocation[1] || arrowLayoutHeight < pHeight - targetLocation[1] -> {
                            y = if (targetLocation[1] <= pHeight / 3) {
                                (targetLocation[1] + target.height + arrowMargin).toFloat()
                            } else {
                                (targetLocation[1] - arrowLayoutHeight - arrowMargin).toFloat()
                            }
                        }
                        else -> {
                            val percentYOfTargetView: Float = targetLocation[1] * 1F / pHeight
                            y = (pHeight - arrowLayoutHeight) * percentYOfTargetView
                            centerY = true
                        }
                    }
                }
            }

            when {
                y < 0 -> {
                    y = if (arrowLayoutHeight < pHeight - (target.y + target.height)) {
                        target.y + target.height + arrowMargin
                    } else {
                        0F + arrowMargin
                    }
                }
                y + arrowLayoutHeight > pHeight -> {
                    y = (pHeight - arrowLayoutHeight - arrowMargin).toFloat()
                    if (y < targetLocation[1] + target.height) {
                        centerY = true
                    }
                }
            }

            x = if (centerY) {
                val rightSpace = pWidth - targetLocation[0] - target.width / 2
                val leftSpace = pWidth - rightSpace

                if (leftSpace > rightSpace) {
                    (targetLocation[0] - arrowLayoutWidth - arrowMargin).toFloat()
                } else {
                    (targetLocation[0] + target.width + arrowMargin).toFloat()
                }
            } else {
                (targetLocation[0] + target.width / 2F) - (arrowLayoutWidth / 2F)
            }

            if (x < 0) {
                x = 0.0F + arrowMargin
            } else if (x + arrowLayoutWidth > pWidth) {
                x = (pWidth - arrowContainer.measuredWidth - arrowMargin).toFloat()
            }
            if (x < 0) {
                x = 0.0F + arrowMargin
            }
            arrowContainer.y = y
            arrowContainer.x = x
        } ?: kotlin.run {
            var y: Float
            var x: Float
            var centerY = false

            when (orientation) {
                ORIENTATION_HORIZONTAL -> {
                    y = targetLocation[1] - (arrowLayoutHeight / 2F)
                    centerY = true
                }
                else -> {
                    when {
                        arrowLayoutHeight < targetLocation[1] || arrowLayoutHeight < pHeight - targetLocation[1] -> {
                            y = if (targetLocation[1] <= pHeight / 3) {
                                (targetLocation[1] + arrowMargin).toFloat()
                            } else {
                                (targetLocation[1] - arrowLayoutHeight - arrowMargin).toFloat()
                            }
                        }
                        else -> {
                            val percentYOfTargetView: Float = targetLocation[1] * 1F / pHeight
                            y = (pHeight - arrowLayoutHeight) * percentYOfTargetView
                            centerY = true
                        }
                    }
                }
            }

            when {
                y < 0 -> {
                    y = if (arrowLayoutHeight < pHeight) {
                        arrowMargin.toFloat()
                    } else {
                        0F + arrowMargin
                    }
                }
                y + arrowLayoutHeight > pHeight -> {
                    y = (pHeight - arrowLayoutHeight - arrowMargin).toFloat()
                    if (y < targetLocation[1]) {
                        centerY = true
                    }
                }
            }

            x = if (centerY) {
                val rightSpace = pWidth - targetLocation[0]
                val leftSpace = pWidth - rightSpace

                if (leftSpace > rightSpace) {
                    (targetLocation[0] - arrowLayoutWidth - arrowMargin).toFloat()
                } else {
                    (targetLocation[0] + arrowMargin).toFloat()
                }
            } else {
                (targetLocation[0]) - (arrowLayoutWidth / 2F)
            }

            if (x < 0) {
                x = 0.0F + arrowMargin
            } else if (x + arrowLayoutWidth > pWidth) {
                x = (pWidth - arrowContainer.measuredWidth - arrowMargin).toFloat()
            }
            if (x < 0) {
                x = 0.0F + arrowMargin
            }
            arrowContainer.y = y
            arrowContainer.x = x
        }
    }

    open fun removeFromRootViewByAnimate() {
        animate()
            .alpha(0.0F)
            .setDuration(200)
            .withEndAction {
                removeView(blurView)
                blurView = null
                getRootViewGroup()?.removeView(this)
            }
    }

    open fun dismiss() {
        arrowContainer.hide()
        removeFromRootViewByAnimate()
        onDismissListener?.onDismiss(this)
    }

    open fun cancel() {
        arrowContainer.hide()
        removeFromRootViewByAnimate()
        onCancelListener?.onCancel(this)
    }

    protected open fun getRootViewGroup(): ViewGroup? {
        val v = (context as FragmentActivity).window.decorView.rootView
        if (v is ViewGroup) {
            return v
        }
        return null
    }

    override fun onDrawForeground(canvas: Canvas) {
        super.onDrawForeground(canvas)
        if (drawTargetView) {
            targetView?.let { targetView ->
                targetView.getLocationInWindow(targetLocation)
                canvas.save()
                canvas.translate(
                    targetLocation[0].toFloat(),
                    targetLocation[1].toFloat()
                )
                targetView.draw(canvas)
                canvas.restore()
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        cancel()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (cancelableOnTouchOutside && event!!.action == MotionEvent.ACTION_DOWN) {
            cancel()
        }
        return super.onTouchEvent(event)
    }

    fun setOnShowListener(onShowListener: OnShowListener?) {
        this.onShowListener = onShowListener
    }

    fun setOnDismissListener(onDismissListener: OnDismissListener?) {
        this.onDismissListener = onDismissListener
    }

    fun setOnCancelListener(onCancelListener: OnCancelListener?) {
        this.onCancelListener = onCancelListener
    }

    open fun getBlurView(): BlurView? {
        return blurView
    }

    companion object {
        const val DURATION_INFINITE: Long = -1
        const val DURATION_SHORT: Long = 4000
        const val DURATION_MEDIUM: Long = 5500
        const val DURATION_LONG: Long = 7000

        const val ORIENTATION_HORIZONTAL = 1
        const val ORIENTATION_VERTICAL = 2

        @kotlin.annotation.Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
        @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
        @LongDef(DURATION_INFINITE, DURATION_SHORT, DURATION_MEDIUM, DURATION_LONG)
        annotation class DurationDef
    }

    open fun setStrokeWidth(strokeWidth: Float) {
        arrowContainer.setStrokeWidth(strokeWidth)
    }

    open fun setStrokeColor(@ColorInt strokeColor: Int) {
        arrowContainer.setStrokeColor(strokeColor)
    }

    open fun setFillColor(@ColorInt fillColor: Int) {
        arrowContainer.setFillColor(fillColor)
    }

    open fun setCornerRadius(cornerRadius: Float) {
        arrowContainer.cornerRadius = cornerRadius
    }

    open fun setArrowColor(@ColorInt arrowColor: Int) {
        arrowContainer.setArrowColor(arrowColor)
    }

    open fun setArrowWidth(arrowWidth: Int) {
        arrowContainer.arrowWidth = arrowWidth
    }

    open fun setArrowHeight(arrowHeight: Int) {
        arrowContainer.arrowHeight = arrowHeight
    }

    fun clearShadow() {
        arrowContainer.clearShadow()
    }

    fun setShadow(radius: Float, dx: Float, dy: Float, shadowColor: Int) {
        arrowContainer.setShadow(radius, dx, dy, shadowColor)
    }

    open fun setInteractionWhenTouchOutside(interaction: Boolean) {
        isClickable = !interaction
    }

    open fun setDrawBlurEffect(drawBlurEffect: Boolean) {
        if (drawBlurEffect) {
            createBlurView()
        } else {
            blurView = null
        }
    }

    open fun setDim(dimColor: Int, @DimDef dimAmount: Float = 0.6F) {
        setBackgroundColor(
            Color.argb(
                (dimAmount * 255).toInt(),
                Color.red(dimColor),
                Color.green(dimColor),
                Color.blue(dimColor),
            )
        )
    }

    fun setDrawArrow(drawArrow: Boolean) {
        arrowContainer.drawArrow = drawArrow
    }

    fun getContentView(): View {
        return arrowContainer
    }

    fun findViewInContentView(id: Int): View {
        return arrowContainer.findViewById(id)
    }

    open fun setOnChildClickListener(onClickListener: OnClickListener?, vararg ids: Int) {
        val size = ids.size
        for (i in 0 until size) {
            arrowContainer.findViewById<View>(ids[i]).setOnClickListener(onClickListener)
        }
    }

    open fun setOnChildLongClickListener(
        onLongClickListener: OnLongClickListener?,
        vararg ids: Int
    ) {
        val size = ids.size
        for (i in 0 until size) {
            arrowContainer.findViewById<View>(ids[i]).setOnLongClickListener(onLongClickListener)
        }
    }

    open class Builder(context: Context) {
        private val arrowPanel: ArrowPanel = ArrowPanel(context)

        init {
            arrowPanel.layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
            )
        }

        open fun setDim(dimColor: Int, @DimDef dimAmount: Float = 0.6F): Builder {
            arrowPanel.setBackgroundColor(
                Color.argb(
                    (dimAmount * 255).toInt(),
                    Color.red(dimColor),
                    Color.green(dimColor),
                    Color.blue(dimColor),
                )
            )
            return this
        }

        open fun setDrawTargetView(drawTargetView: Boolean): Builder {
            arrowPanel.drawTargetView = drawTargetView
            return this
        }

        open fun setDrawBlurEffect(drawBlurEffect: Boolean): Builder {
            if (drawBlurEffect) {
                arrowPanel.createBlurView()
            } else {
                arrowPanel.blurView = null
            }
            return this
        }

        open fun setBlurQuality(quality: Int): Builder {
            arrowPanel.blurQuality = quality
            return this
        }

        open fun setBlurRadius(radius: Float): Builder {
            arrowPanel.blurRadius = radius
            return this
        }

        open fun setStrokeWidth(strokeWidth: Float): Builder {
            arrowPanel.arrowContainer.setStrokeWidth(strokeWidth)
            return this
        }

        open fun setStrokeColor(@ColorInt strokeColor: Int): Builder {
            arrowPanel.arrowContainer.setStrokeColor(strokeColor)
            return this
        }

        open fun setFillColor(@ColorInt fillColor: Int): Builder {
            arrowPanel.arrowContainer.setFillColor(fillColor)
            return this
        }

        open fun setCornerRadius(cornerRadius: Float): Builder {
            arrowPanel.arrowContainer.cornerRadius = cornerRadius
            return this
        }

        open fun setArrowColor(@ColorInt arrowColor: Int): Builder {
            arrowPanel.arrowContainer.setArrowColor(arrowColor)
            return this
        }

        open fun setArrowWidth(arrowWidth: Int): Builder {
            arrowPanel.arrowContainer.arrowWidth = arrowWidth
            return this
        }

        open fun setArrowHeight(arrowHeight: Int): Builder {
            arrowPanel.arrowContainer.arrowHeight = arrowHeight
            return this
        }

        open fun setArrowMargin(margin: Int): Builder {
            arrowPanel.arrowMargin = margin
            return this
        }

        open fun setContentView(view: View): Builder {
            arrowPanel.setContentView(view)
            return this
        }

        open fun setContentView(@LayoutRes layoutId: Int): Builder {
            arrowPanel.setContentView(layoutId)
            return this
        }

        open fun setCancelable(cancel: Boolean): Builder {
            arrowPanel.cancelable = cancel
            return this
        }

        open fun setCancelableOnTouchOutside(cancel: Boolean): Builder {
            arrowPanel.isFocusable = cancel
            arrowPanel.cancelableOnTouchOutside = cancel
            return this
        }

        open fun setInteractionWhenTouchOutside(interaction: Boolean): Builder {
            arrowPanel.isClickable = !interaction
            return this
        }

        fun setOnShowListener(onShowListener: OnShowListener?): Builder {
            arrowPanel.setOnShowListener(onShowListener)
            return this
        }

        fun setOnDismissListener(onDismissListener: OnDismissListener?): Builder {
            arrowPanel.setOnDismissListener(onDismissListener)
            return this
        }

        fun setOnCancelListener(onCancelListener: OnCancelListener?): Builder {
            arrowPanel.setOnCancelListener(onCancelListener)
            return this
        }

        fun clearShadow(): Builder {
            arrowPanel.clearShadow()
            return this
        }

        fun setShadow(radius: Float, dx: Float, dy: Float, shadowColor: Int): Builder {
            arrowPanel.setShadow(radius, dx, dy, shadowColor)
            return this
        }

        fun setTimeOutDuration(duration: Long): Builder {
            arrowPanel.timeOutDuration = duration
            return this
        }

        open fun setOrientation(orientation: Int): Builder {
            arrowPanel.orientation = orientation
            return this
        }

        open fun setAnimator(animator: Animator?): Builder {
            arrowPanel.setAnimator(animator)
            return this
        }

        open fun setDrawArrow(drawArrow: Boolean): Builder {
            arrowPanel.setDrawArrow(drawArrow)
            return this
        }

        open fun build(): ArrowPanel {
            return arrowPanel
        }
    }

    @kotlin.annotation.Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    @FloatRange(from = 0.0, to = 1.0)
    annotation class DimDef

    interface OnShowListener {
        fun onShow(arrowPanel: ArrowPanel)
    }

    interface OnDismissListener {
        fun onDismiss(arrowPanel: ArrowPanel)
    }

    interface OnCancelListener {
        fun onCancel(arrowPanel: ArrowPanel)
    }
}