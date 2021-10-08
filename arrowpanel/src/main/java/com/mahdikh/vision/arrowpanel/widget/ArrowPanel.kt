package com.mahdikh.vision.arrowpanel.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.Build
import android.util.DisplayMetrics
import android.view.*
import android.view.View.OnClickListener
import android.view.View.OnLongClickListener
import android.view.WindowManager.LayoutParams
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.annotation.LayoutRes
import androidx.annotation.LongDef
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.mahdikh.vision.arrowpanel.animator.BaseAnimator
import com.mahdikh.vision.arrowpanel.touchanimator.TouchAnimator

open class ArrowPanel constructor(context: Context) : FrameLayout(context),
    PanelInterface {
    private val targetLocation: IntArray = IntArray(2)
    private var blurView: BlurView? = null
    open var targetView: View? = null
    var arrowLayout: ArrowLayout
        private set

    open var blurQuality: Int = 10
    open var blurRadius: Float = 5.0F
    open var arrowMargin: Int = 5
    open var drawTargetView: Boolean = true
    private var mCanceled = false
    private var mDismissed = false

    open var type: Int = TYPE_DECOR

    open var cancelable: Boolean = true
    open var cancelableOnTouchOutside: Boolean = true
        set(value) {
            field = value
            isFocusable = value
        }

    open var orientation = ORIENTATION_HORIZONTAL or ORIENTATION_VERTICAL

    @DurationDef
    open var timeOutDuration: Long = DURATION_INFINITE
        set(value) {
            if (value == DURATION_INFINITE && field != DURATION_INFINITE) {
                removeCallbacks(dismissRunnable)
            }
            field = value
        }

    private var onShowListener: PanelInterface.OnShowListener? = null
    private var onDismissListener: PanelInterface.OnDismissListener? = null
    private var onCancelListener: PanelInterface.OnCancelListener? = null
    private var childClickListener: OnClickListener? = null
    private var childLongClickListener: OnLongClickListener? = null
    private val dismissRunnable = Runnable { dismiss() }

    init {
        alpha = 0.0F
        isClickable = true
        isFocusable = true
        isFocusableInTouchMode = true
        clipChildren = false
        fitsSystemWindows = false
        arrowLayout = ArrowLayout(context)
        super.setLayoutDirection(LAYOUT_DIRECTION_LTR)
        super.setWillNotDraw(false)
    }

    open fun show() {
        if (type == TYPE_WINDOW) {
            addAsWindow()
            post {
                showArrowLayout()
            }
        } else {
            addInRootViewGroup()
            showArrowLayout()
            requestFocus()
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

    override fun dismiss() {
        if (!mDismissed) {
            arrowLayout.hide()
            removeView()
            mDismissed = true
            onDismissListener?.onDismiss(this)
        }
    }

    override fun cancel() {
        if (!mCanceled) {
            mCanceled = true
            onCancelListener?.onCancel(this)
            dismiss()
        }
    }

    open fun removeView() {
        animate().apply {
            alpha(0.0F)
            duration = 250
            withEndAction {
                blurView?.let {
                    removeView(blurView)
                    blurView = null
                }
                if (type == TYPE_WINDOW && isAttachedToWindow) {
                    getWindowManager(context).removeViewImmediate(this@ArrowPanel)
                } else {
                    getRootViewGroup()?.removeView(this@ArrowPanel)
                }
            }
        }.start()
    }

    private fun showArrowLayout() {
        adjustArrowLayoutLocation()
        animate().alpha(1.0F).duration = 150

        if (type == TYPE_WINDOW) {
            arrowLayout.requestLayout()
        }

        arrowLayout.show()
        onShowListener?.onShow(this)

        blurView?.let {
            val sourceView = getRootViewGroup()
            if (sourceView != null) {
                it.blur(sourceView, blurQuality, blurRadius, false)
            }
        }

        if (timeOutDuration != DURATION_INFINITE) {
            postDelayed(dismissRunnable, timeOutDuration)
        }
    }

    private fun adjustArrowLayoutLocation() {
        measure(0, 0)

        val pWidth = rootView.width
        val pHeight = rootView.height

        var arrowLayoutWidth = arrowLayout.measuredWidth
        var arrowLayoutHeight = arrowLayout.measuredHeight

        if (arrowLayoutHeight > pHeight) {
            arrowLayoutHeight = pHeight
        }
        if (arrowLayoutWidth > pWidth) {
            arrowLayoutWidth = pWidth
        }

        targetView?.let { target ->
            if (type == TYPE_WINDOW) {
                target.getLocationOnScreen(targetLocation)
            } else {
                target.getLocationInWindow(targetLocation)
            }
            arrowLayout.targetView = target

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
                x = (pWidth - arrowLayout.measuredWidth - arrowMargin).toFloat()
            }
            if (x < 0) {
                x = 0.0F + arrowMargin
            }
            arrowLayout.y = y
            arrowLayout.x = x
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
                                if (arrowLayoutHeight > targetLocation[1]) {
                                    targetLocation[1].toFloat()
                                } else {
                                    (targetLocation[1] - arrowLayoutHeight - arrowMargin).toFloat()
                                }
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
                x = (pWidth - arrowLayout.measuredWidth - arrowMargin).toFloat()
            }
            if (x < 0) {
                x = 0.0F + arrowMargin
            }
            arrowLayout.y = y
            arrowLayout.x = x
        }
        arrowLayout.setTargetLocation(targetLocation[0], targetLocation[1])
    }

    private fun addAsWindow() {
        val context = context
        val manager = getWindowManager(context)
        val size = getScreenSize(context)
        val params = LayoutParams(
            size.x, size.y,
            WindowManager.LayoutParams.TYPE_APPLICATION,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.LEFT or Gravity.TOP
        addView(arrowLayout)
        manager.addView(this, params)
    }

    private fun addInRootViewGroup() {
        getRootViewGroup()?.let { rootView ->
            addView(arrowLayout)
            rootView.addView(this)
        } ?: kotlin.run {
            type = TYPE_WINDOW
            addAsWindow()
        }
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

    override fun onDrawForeground(canvas: Canvas) {
        super.onDrawForeground(canvas)
        if (drawTargetView) {
            targetView?.let { targetView ->
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

    override fun setLayoutDirection(layoutDirection: Int) {
        arrowLayout.layoutDirection = layoutDirection
    }

    protected open fun createBlurView() {
        blurView = BlurView(context).also {
            it.addNoBlurView(this)
            it.setOverlapView(this)
            if (drawTargetView) {
                targetView?.let { target ->
                    it.addNoBlurView(target)
                }
            }
        }
        addView(blurView, 0)
    }

    open fun onBackPressed() {
        if (cancelable) {
            cancel()
        }
    }

    open fun resetTimeout() {
        removeCallbacks(dismissRunnable)
        if (timeOutDuration != DURATION_INFINITE) {
            postDelayed(dismissRunnable, timeOutDuration)
        }
    }

    open fun setAnimator(animator: BaseAnimator?) {
        arrowLayout.animator = animator
    }

    open fun setTouchAnimator(touchAnimator: TouchAnimator?) {
        arrowLayout.touchAnimator = touchAnimator
    }

    open fun getAnimator(): BaseAnimator? {
        return arrowLayout.animator
    }

    open fun isShowing(): Boolean {
        return isAttachedToWindow and isVisible
    }

    open fun setInteractionTouchOutside(interaction: Boolean) {
        isClickable = !interaction
    }

    open fun setContentView(layoutId: Int): View {
        return arrowLayout.inflateLayout(layoutId)
    }

    open fun setContentView(view: View): View {
        arrowLayout.removeAllViews()
        arrowLayout.addView(view)
        return view
    }

    open fun getContentView(): View {
        return arrowLayout
    }

    open fun findView(id: Int): View {
        return arrowLayout.findViewById(id)
    }

    protected open fun getRootViewGroup(): ViewGroup? {
        if (context is FragmentActivity) {
            return (context as FragmentActivity).window.decorView.rootView as ViewGroup
        }
        return null
    }

    open fun setStrokeWidth(strokeWidth: Float) {
        arrowLayout.setStrokeWidth(strokeWidth)
    }

    open fun setStrokeColor(@ColorInt strokeColor: Int) {
        arrowLayout.setStrokeColor(strokeColor)
    }

    open fun setFillColor(@ColorInt fillColor: Int) {
        arrowLayout.setFillColor(fillColor)
    }

    open fun setCornerRadius(cornerRadius: Float) {
        arrowLayout.cornerRadius = cornerRadius
    }

    open fun setArrowWidth(arrowWidth: Int) {
        arrowLayout.arrowWidth = arrowWidth
    }

    open fun setArrowHeight(arrowHeight: Int) {
        arrowLayout.arrowHeight = arrowHeight
    }

    open fun setShadow(radius: Float, dx: Float, dy: Float, shadowColor: Int) {
        arrowLayout.setShadow(radius, dx, dy, shadowColor)
    }

    open fun clearShadow() {
        arrowLayout.clearShadow()
    }

    open fun setDrawBlurEffect(drawBlurEffect: Boolean) {
        if (drawBlurEffect) {
            createBlurView()
        } else if (blurView != null) {
            removeView(blurView)
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

    open fun setDrawArrow(drawArrow: Boolean) {
        arrowLayout.drawArrow = drawArrow
    }

    open fun setLocation(x: Int, y: Int) {
        targetLocation[0] = x
        targetLocation[1] = y
    }

    open fun setLocation(motionEvent: MotionEvent) {
        targetLocation[0] = motionEvent.rawX.toInt()
        targetLocation[1] = motionEvent.rawY.toInt()
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

    open fun setOnChildClickListener(
        onChildClickListener: PanelInterface.OnChildClickListener?,
        vararg ids: Int
    ) {
        childClickListener = if (onChildClickListener == null) {
            null
        } else {
            OnClickListener { v -> onChildClickListener.onClick(v, this) }
        }
        val size = ids.size
        for (i in 0 until size) {
            arrowLayout.findViewById<View>(ids[i]).setOnClickListener(childClickListener)
        }
    }

    open fun setOnChildLongClickListener(
        onChildLongClickListener: PanelInterface.OnChildLongClickListener?,
        vararg ids: Int
    ) {
        childLongClickListener = if (onChildLongClickListener == null) {
            null
        } else {
            OnLongClickListener { v -> onChildLongClickListener.onLongClick(v, this) }
        }
        val size = ids.size
        for (i in 0 until size) {
            arrowLayout.findViewById<View>(ids[i]).setOnLongClickListener(childLongClickListener)
        }
    }

    open fun setPivotToArrow(pivotToArrow: Boolean) {
        arrowLayout.pivotToArrow = pivotToArrow
    }

    class Builder(context: Context) {
        private val panelParams = PanelParams()
        private val arrowPanel = ArrowPanel(context)

        fun setContentView(view: View): Builder {
            panelParams.mView = view
            panelParams.mViewLayoutResId = 0
            return this
        }

        fun setContentView(@LayoutRes layoutId: Int): Builder {
            panelParams.mViewLayoutResId = layoutId
            panelParams.mView = null
            return this
        }

        fun setType(type: Int): Builder {
            arrowPanel.type = type
            return this
        }

        fun setTargetView(targetView: View?): Builder {
            panelParams.targetView = targetView
            return this
        }

        fun setDrawTargetView(drawTargetView: Boolean): Builder {
            arrowPanel.drawTargetView = drawTargetView
            return this
        }

        fun setLocation(x: Int, y: Int): Builder {
            arrowPanel.setLocation(x, y)
            return this
        }

        fun setLocation(motionEvent: MotionEvent): Builder {
            arrowPanel.setLocation(motionEvent)
            return this
        }

        fun setOrientation(orientation: Int): Builder {
            arrowPanel.orientation = orientation
            return this
        }

        fun setAnimator(animator: BaseAnimator?): Builder {
            arrowPanel.setAnimator(animator)
            return this
        }

        fun setTouchAnimator(touchAnimator: TouchAnimator?): Builder {
            arrowPanel.setTouchAnimator(touchAnimator)
            return this
        }

        fun setShadow(radius: Float, dx: Float, dy: Float, shadowColor: Int): Builder {
            arrowPanel.setShadow(radius, dx, dy, shadowColor)
            return this
        }

        fun clearShadow(): Builder {
            arrowPanel.clearShadow()
            return this
        }

        fun setCornerRadius(cornerRadius: Float): Builder {
            arrowPanel.setCornerRadius(cornerRadius)
            return this
        }

        fun setStrokeWidth(strokeWidth: Float): Builder {
            arrowPanel.setStrokeWidth(strokeWidth)
            return this
        }

        fun setStrokeColor(@ColorInt strokeColor: Int): Builder {
            arrowPanel.setStrokeColor(strokeColor)
            return this
        }

        fun setFillColor(@ColorInt fillColor: Int): Builder {
            arrowPanel.setFillColor(fillColor)
            return this
        }

        fun setDrawArrow(drawArrow: Boolean): Builder {
            arrowPanel.setDrawArrow(drawArrow)
            return this
        }

        fun setArrowMargin(margin: Int): Builder {
            arrowPanel.arrowMargin = margin
            return this
        }

        fun setArrowWidth(arrowWidth: Int): Builder {
            arrowPanel.setArrowWidth(arrowWidth)
            return this
        }

        fun setArrowHeight(arrowHeight: Int): Builder {
            arrowPanel.setArrowHeight(arrowHeight)
            return this
        }

        fun setPivotToArrow(pivotToArrow: Boolean): Builder {
            arrowPanel.setPivotToArrow(pivotToArrow)
            return this
        }

        fun setDim(dimColor: Int, @DimDef dimAmount: Float = 0.6F): Builder {
            arrowPanel.setDim(dimColor, dimAmount)
            return this
        }

        fun setDrawBlurEffect(blurEffect: Boolean): Builder {
            panelParams.drawBlurEffect = blurEffect
            return this
        }

        fun setBlurQuality(blurQuality: Int): Builder {
            arrowPanel.blurQuality = blurQuality
            return this
        }

        fun setBlurRadius(blurRadius: Float): Builder {
            arrowPanel.blurRadius = blurRadius
            return this
        }

        fun setCancelable(cancel: Boolean): Builder {
            arrowPanel.cancelable = cancel
            return this
        }

        fun setCancelableOnTouchOutside(cancel: Boolean): Builder {
            arrowPanel.cancelableOnTouchOutside = cancel
            return this
        }

        fun setInteractionWhenTouchOutside(interaction: Boolean): Builder {
            arrowPanel.setInteractionTouchOutside(interaction)
            return this
        }

        fun setTimeOutDuration(duration: Long): Builder {
            arrowPanel.timeOutDuration = duration
            return this
        }

        fun setOnShowListener(onShowListener: PanelInterface.OnShowListener?): Builder {
            arrowPanel.setOnShowListener(onShowListener)
            return this
        }

        fun setOnDismissListener(onDismissListener: PanelInterface.OnDismissListener?): Builder {
            arrowPanel.setOnDismissListener(onDismissListener)
            return this
        }

        fun setOnCancelListener(onCancelListener: PanelInterface.OnCancelListener?): Builder {
            arrowPanel.setOnCancelListener(onCancelListener)
            return this
        }

        fun setOnChildClickListener(
            onChildClickListener: PanelInterface.OnChildClickListener?,
            vararg ids: Int
        ): Builder {
            panelParams.onChildClickListener = onChildClickListener
            panelParams.clickIds = ids
            return this
        }

        fun setOnChildLongClickListener(
            onChildLongClickListener: PanelInterface.OnChildLongClickListener?,
            vararg ids: Int
        ): Builder {
            panelParams.onChildLongClickListener = onChildLongClickListener
            panelParams.longClickIds = ids
            return this
        }

        fun build(): ArrowPanel {
            panelParams.apply(arrowPanel)
            return arrowPanel
        }

        fun show(): ArrowPanel {
            val p = build()
            p.show()
            return p
        }

        fun show(targetView: View): ArrowPanel {
            val p = build()
            p.show(targetView)
            return p
        }

        fun show(motionEvent: MotionEvent): ArrowPanel {
            val p = build()
            p.show(motionEvent)
            return p
        }
    }

    companion object {
        const val TYPE_DECOR = 0
        const val TYPE_WINDOW = 1

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

        @JvmStatic
        private fun getScreenSize(context: Context): Point {
            val point = Point()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                context.display?.let {
                    val metrics = DisplayMetrics()
                    it.getRealMetrics(metrics)
                    point.set(metrics.widthPixels, metrics.heightPixels)
                } ?: kotlin.run {
                    val manager = getWindowManager(context)
                    val windowMetrics = manager.currentWindowMetrics
                    val bounds = windowMetrics.bounds
                    point.set(bounds.width(), bounds.height())
                }
            } else {
                val manager = getWindowManager(context)
                val metrics = DisplayMetrics()
                manager.defaultDisplay.getRealMetrics(metrics)
                point.set(metrics.widthPixels, metrics.heightPixels)
            }
            return point
        }

        @JvmStatic
        private fun getWindowManager(context: Context): WindowManager {
            return context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        }
    }

    @kotlin.annotation.Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    @FloatRange(from = 0.0, to = 1.0)
    annotation class DimDef
}