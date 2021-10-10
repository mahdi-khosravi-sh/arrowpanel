package com.mahdikh.vision.arrowpanel.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.Build
import android.util.DisplayMetrics
import android.view.*
import android.view.View.OnClickListener
import android.view.View.OnLongClickListener
import android.view.WindowManager.LayoutParams
import androidx.annotation.ColorInt
import androidx.annotation.LayoutRes
import androidx.annotation.LongDef
import androidx.fragment.app.FragmentActivity
import com.mahdikh.vision.arrowpanel.animator.BaseAnimator
import com.mahdikh.vision.arrowpanel.touchanimator.TouchAnimator

open class ArrowPanel(context: Context) : Panel(context) {
    val arrowLayout = ArrowLayout.newInstance(context)
    private val targetLocation: IntArray = IntArray(2)
    private var blurView: BlurView? = null
    open var targetView: View? = null
    open var drawBlurEffect = false
    open var blurQuality: Int = 10
    open var blurRadius: Float = 5.0F
    open var arrowMargin: Int = 5
    open var drawTargetView: Boolean = true
    open var type: Int = TYPE_DECOR
    open var orientation = ORIENTATION_HORIZONTAL or ORIENTATION_VERTICAL
    
    var reusable = false

    @DurationDef
    open var timeOutDuration: Long = DURATION_INFINITE
        set(value) {
            if (value == DURATION_INFINITE && field != DURATION_INFINITE) {
                removeCallbacks(dismissRunnable)
            }
            field = value
        }

    private var childClickListener: OnClickListener? = null
    private var childLongClickListener: OnLongClickListener? = null
    private val dismissRunnable = Runnable { dismiss() }

    init {
        alpha = 0.0F
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

    override fun onShow() {
        if (drawBlurEffect) {
            createBlurView()
        }

        if (type == TYPE_WINDOW) {
            addAsWindow()
        } else {
            addInRootViewGroup()
        }
        post {
            arrowLayout.requestLayout()
            adjustArrowLayoutLocation()
            showArrowLayout()
            requestFocus()
        }
    }

    override fun onDismiss() {
        arrowLayout.hide()
        hide()
    }

    private fun hide() {
        animate()
            .alpha(0.0F)
            .setDuration(250)
            .withEndAction {
                val bv = blurView
                if (bv != null) {
                    removeView(blurView)
                    blurView = null
                }
                if (type == TYPE_WINDOW && isAttachedToWindow) {
                    getWindowManager(context).removeViewImmediate(this@ArrowPanel)
                } else {
                    getRootViewGroup()?.removeView(this@ArrowPanel)
                }
            }
            .start()
    }

    private fun showArrowLayout() {
        animate().alpha(1.0F).setDuration(150).start()
        arrowLayout.show()
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
            if (arrowLayout.parent == null) {
                addView(arrowLayout)
            }
            rootView.addView(this)
        } ?: kotlin.run {
            type = TYPE_WINDOW
            addAsWindow()
        }
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

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (!reusable) {
            setOnChildClickListener(null)
            setOnChildLongClickListener(null)
            removeAllViews()
            removeAllShowListeners()
            removeAllCancelListeners()
            removeAllDismissListeners()
            blurView = null
            targetView = null
        }
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

        fun setDrawBlurEffect(drawBlurEffect: Boolean): Builder {
            arrowPanel.drawBlurEffect = drawBlurEffect
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

        fun addOnShowListener(onShowListener: PanelInterface.OnShowListener): Builder {
            arrowPanel.addOnShowListener(onShowListener)
            return this
        }

        fun addOnDismissListener(onDismissListener: PanelInterface.OnDismissListener): Builder {
            arrowPanel.addOnDismissListener(onDismissListener)
            return this
        }

        fun addOnCancelListener(onCancelListener: PanelInterface.OnCancelListener): Builder {
            arrowPanel.addOnCancelListener(onCancelListener)
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
}