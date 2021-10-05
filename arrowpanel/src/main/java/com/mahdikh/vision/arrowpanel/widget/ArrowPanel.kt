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
import com.mahdikh.vision.arrowpanel.animator.Animator

open class ArrowPanel constructor(context: Context) : FrameLayout(context), ArrowInterface {
    private val targetLocation: IntArray = IntArray(2)
    private var blurView: BlurView? = null

    var targetView: View? = null
    var drawTargetView: Boolean = true
    var cancelableOnTouchOutside: Boolean = true
        set(value) {
            field = value
            isFocusable = value
        }
    var cancelable: Boolean = true
    var blurQuality: Int = 10
    var blurRadius: Float = 5.0F
    var arrowMargin: Int = 5
    var createAsWindow = false

    private var mCanceled = false
    private var mDismissed = false

    var orientation = ORIENTATION_HORIZONTAL or ORIENTATION_VERTICAL
    var arrowContainer: ArrowContainer
        private set

    @DurationDef
    var timeOutDuration: Long = DURATION_INFINITE
        set(value) {
            if (value == DURATION_INFINITE && field != DURATION_INFINITE) {
                removeCallbacks(dismissRunnable)
            }
            field = value
        }

    private var onShowListener: ArrowInterface.OnShowListener? = null
    private var onDismissListener: ArrowInterface.OnDismissListener? = null
    private var onCancelListener: ArrowInterface.OnCancelListener? = null
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
        arrowContainer = ArrowContainer(context)
        super.setLayoutDirection(LAYOUT_DIRECTION_LTR)
        super.setWillNotDraw(false)
    }

    protected open fun createBlurView() {
        blurView = BlurView(context).also {
            it.addNoBlurView(this)
            it.setOverlapView(this)
            it.layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
            )
            if (drawTargetView) {
                targetView?.let { target ->
                    it.addNoBlurView(target)
                }
            }
        }
        addView(blurView, 0)
    }

    fun setAnimator(animator: Animator?) {
        arrowContainer.animator = animator
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
        addView(arrowContainer)
        manager.addView(this, params)
    }

    private fun addInRootViewGroup() {
        getRootViewGroup()?.let { rootView ->
            layoutParams = LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            addView(arrowContainer)
            rootView.addView(this)
        } ?: kotlin.run {
            createAsWindow = true
            addAsWindow()
        }
    }

    open fun show() {
        if (createAsWindow) {
            addAsWindow()
            post { showArrowLayout() }
        } else {
            addInRootViewGroup()
            showArrowLayout()
            requestFocus()
        }
    }

    private fun showArrowLayout() {
        adjustArrowLayoutLocation()
        animate().alpha(1.0F).duration = 150
        arrowContainer.show()
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

    fun isShowing(): Boolean {
        return isAttachedToWindow and isVisible
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

    open fun onBackPressed() {
        if (cancelable) {
            cancel()
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
            if (createAsWindow) {
                target.getLocationOnScreen(targetLocation)
            } else {
                target.getLocationInWindow(targetLocation)
            }
            arrowContainer.targetView = target

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
        arrowContainer.setTargetLocation(targetLocation[0], targetLocation[1])
    }

    open fun removeView() {
        animate()
            .alpha(0.0F)
            .setDuration(200)
            .withEndAction {
                removeView(blurView)
                blurView = null
                if (createAsWindow) {
                    getWindowManager(context).removeViewImmediate(this)
                } else {
                    getRootViewGroup()?.removeView(this)
                }
            }
    }

    override fun dismiss() {
        if (!mDismissed) {
            arrowContainer.hide()
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

    protected open fun getRootViewGroup(): ViewGroup? {
        if (context is FragmentActivity) {
            return (context as FragmentActivity).window.decorView.rootView as ViewGroup
        }
        return null
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

    override fun setLayoutDirection(layoutDirection: Int) {
        arrowContainer.layoutDirection = layoutDirection
    }

    fun setOnShowListener(onShowListener: ArrowInterface.OnShowListener?) {
        this.onShowListener = onShowListener
    }

    fun setOnDismissListener(onDismissListener: ArrowInterface.OnDismissListener?) {
        this.onDismissListener = onDismissListener
    }

    fun setOnCancelListener(onCancelListener: ArrowInterface.OnCancelListener?) {
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

    open fun setOnChildClickListener(
        onChildClickListener: ArrowInterface.OnChildClickListener?,
        vararg ids: Int
    ) {
        childClickListener = if (onChildClickListener == null) {
            null
        } else {
            OnClickListener { v -> onChildClickListener.onClick(v, this) }
        }
        val size = ids.size
        for (i in 0 until size) {
            arrowContainer.findViewById<View>(ids[i]).setOnClickListener(childClickListener)
        }
    }

    open fun setOnChildLongClickListener(
        onChildLongClickListener: ArrowInterface.OnChildLongClickListener?,
        vararg ids: Int
    ) {
        childLongClickListener = if (onChildLongClickListener == null) {
            null
        } else {
            OnLongClickListener { v -> onChildLongClickListener.onLongClick(v, this) }
        }
        val size = ids.size
        for (i in 0 until size) {
            arrowContainer.findViewById<View>(ids[i]).setOnLongClickListener(childLongClickListener)
        }
    }

    open fun setLocation(x: Int, y: Int) {
        targetLocation[0] = x
        targetLocation[1] = y
    }

    open fun setLocation(motionEvent: MotionEvent) {
        targetLocation[0] = motionEvent.rawX.toInt()
        targetLocation[1] = motionEvent.rawY.toInt()
    }

    open class Builder(context: Context) {
        private val arrowPanel: ArrowPanel = ArrowPanel(context)

        open fun setDim(dimColor: Int, @DimDef dimAmount: Float = 0.6F): Builder {
            arrowPanel.setDim(dimColor, dimAmount)
            return this
        }

        open fun setDrawTargetView(drawTargetView: Boolean): Builder {
            arrowPanel.drawTargetView = drawTargetView
            return this
        }

        open fun setDrawBlurEffect(drawBlurEffect: Boolean): Builder {
            arrowPanel.setDrawBlurEffect(drawBlurEffect)
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
            arrowPanel.setStrokeWidth(strokeWidth)
            return this
        }

        open fun setStrokeColor(@ColorInt strokeColor: Int): Builder {
            arrowPanel.setStrokeColor(strokeColor)
            return this
        }

        open fun setFillColor(@ColorInt fillColor: Int): Builder {
            arrowPanel.setFillColor(fillColor)
            return this
        }

        open fun setCornerRadius(cornerRadius: Float): Builder {
            arrowPanel.setCornerRadius(cornerRadius)
            return this
        }

        open fun setArrowWidth(arrowWidth: Int): Builder {
            arrowPanel.setArrowWidth(arrowWidth)
            return this
        }

        open fun setArrowHeight(arrowHeight: Int): Builder {
            arrowPanel.setArrowHeight(arrowHeight)
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
            arrowPanel.cancelableOnTouchOutside = cancel
            return this
        }

        open fun setInteractionWhenTouchOutside(interaction: Boolean): Builder {
            arrowPanel.setInteractionWhenTouchOutside(interaction)
            return this
        }

        fun setOnShowListener(onShowListener: ArrowInterface.OnShowListener?): Builder {
            arrowPanel.setOnShowListener(onShowListener)
            return this
        }

        fun setOnDismissListener(onDismissListener: ArrowInterface.OnDismissListener?): Builder {
            arrowPanel.setOnDismissListener(onDismissListener)
            return this
        }

        fun setOnCancelListener(onCancelListener: ArrowInterface.OnCancelListener?): Builder {
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

        open fun setTargetView(targetView: View?): Builder {
            arrowPanel.targetView = targetView
            return this
        }

        open fun setLocation(x: Int, y: Int): Builder {
            arrowPanel.setLocation(x, y)
            return this
        }

        open fun setLocation(motionEvent: MotionEvent): Builder {
            arrowPanel.setLocation(motionEvent)
            return this
        }

        open fun setCreateAsWindow(asWindow: Boolean): Builder {
            arrowPanel.createAsWindow = asWindow
            return this
        }


        open fun setOnChildClickListener(
            onChildClickListener: ArrowInterface.OnChildClickListener?,
            vararg ids: Int
        ): Builder {
            arrowPanel.setOnChildClickListener(onChildClickListener, *ids)
            return this
        }

        open fun setOnChildLongClickListener(
            onChildLongClickListener: ArrowInterface.OnChildLongClickListener?,
            vararg ids: Int
        ): Builder {
            arrowPanel.setOnChildLongClickListener(onChildLongClickListener, *ids)
            return this
        }

        open fun build(): ArrowPanel {
            return arrowPanel
        }

        open fun show(): ArrowPanel {
            val p = build()
            p.show()
            return p
        }

        open fun show(targetView: View): ArrowPanel {
            val p = build()
            p.show(targetView)
            return p
        }

        open fun show(motionEvent: MotionEvent): ArrowPanel {
            val p = build()
            p.show(motionEvent)
            return p
        }
    }

    @kotlin.annotation.Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    @FloatRange(from = 0.0, to = 1.0)
    annotation class DimDef
}