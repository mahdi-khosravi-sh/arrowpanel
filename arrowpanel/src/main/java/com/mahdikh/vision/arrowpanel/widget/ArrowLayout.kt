package com.mahdikh.vision.arrowpanel.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.CallSuper
import androidx.annotation.ColorInt
import androidx.annotation.IntDef
import androidx.core.view.setPadding
import com.mahdikh.vision.arrowpanel.R
import com.mahdikh.vision.arrowpanel.animator.BaseAnimator
import com.mahdikh.vision.arrowpanel.touchanimator.TouchAnimator
import kotlin.math.min

open class ArrowLayout(context: Context) : FrameLayout(context) {
    val strokePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val arrowPath: Path = Path()
    private val path: Path = Path()
    open var animator: BaseAnimator? = null
    open var touchAnimator: TouchAnimator? = null
    open var pivotToArrow = true
    private val targetLocation: IntArray = IntArray(2)
    private var firstYAxis: Float = 0.0F
    private var firstXAxis: Float = 0.0F
    protected open var syncArrowPath = true
    open var drawArrow: Boolean = true
    open var cornerRadius: Float = 15.0F
    private var drawStroke = false
    var arrowEdge: Int = Gravity.NO_GRAVITY
        private set
    internal var orientation = ArrowPanel.ORIENTATION_HORIZONTAL or ArrowPanel.ORIENTATION_VERTICAL

    var maxHeight: Int = -1
    var maxWidth: Int = -1

    var arrowWidth: Int = 20
    var arrowHeight: Int = 15
        set(value) {
            field = value
            setPadding(value)
        }

    open var targetView: View? = null
        set(value) {
            field = value
            invalidate()
        }

    var endHideAction: Runnable? = null

    init {
        setPadding(arrowHeight)

        isClickable = true
        isFocusable = true
        super.setWillNotDraw(false)

        paint.color = Color.WHITE

        strokePaint.style = Paint.Style.STROKE
        strokePaint.strokeCap = Paint.Cap.ROUND
        strokePaint.strokeJoin = Paint.Join.ROUND
        strokePaint.color = Color.WHITE

        adjustAttrsFromTheme(context)
    }

    private fun adjustAttrsFromTheme(context: Context) {
        val a: TypedArray = context.obtainStyledAttributes(R.styleable.ArrowLayout)
        var shadowColor = Color.parseColor("#33000000")
        var shadowRadius = 5.0F
        var shadowDx = 0.0F
        var shadowDy = 0.0F

        val indexCount = a.indexCount
        var index: Int

        for (i in 0..indexCount) {
            index = a.getIndex(i)
            when (index) {
                R.styleable.ArrowLayout_ap_fillColor -> {
                    setFillColor(a.getColor(index, Color.WHITE))
                }
                R.styleable.ArrowLayout_ap_strokeColor -> {
                    setStrokeColor(a.getColor(index, Color.WHITE))
                }
                R.styleable.ArrowLayout_ap_strokeWidth -> {
                    setStrokeWidth(a.getDimension(index, 0.0F))
                }
                R.styleable.ArrowLayout_ap_arrowWidth -> {
                    arrowWidth = a.getDimensionPixelSize(index, 20)
                }
                R.styleable.ArrowLayout_ap_arrowHeight -> {
                    arrowHeight = a.getDimensionPixelSize(index, 15)
                }
                R.styleable.ArrowLayout_ap_shadowRadius -> {
                    shadowRadius = a.getDimension(index, 10.0F)
                }
                R.styleable.ArrowLayout_ap_shadowDy -> {
                    shadowDy = a.getFloat(index, 0.0F)
                }
                R.styleable.ArrowLayout_ap_shadowDx -> {
                    shadowDx = a.getFloat(index, 0.0f)
                }
                R.styleable.ArrowLayout_ap_shadowColor -> {
                    shadowColor = a.getColor(index, shadowColor)
                }
                R.styleable.ArrowLayout_ap_cornerRadius -> {
                    cornerRadius = a.getDimension(index, 15.0F)
                }
            }
        }
        if (shadowRadius > 0) {
            setShadow(shadowRadius, shadowDx, shadowDy, shadowColor)
        }
        a.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val h = makeMeasureSpec(maxHeight, heightMeasureSpec)
        val w = makeMeasureSpec(maxWidth, widthMeasureSpec)
        super.onMeasure(w, h)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        touchAnimator?.dispatchTouchEvent(this, ev)
        return super.dispatchTouchEvent(ev)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        adjustPath()
        canvas.drawPath(path, paint)
        if (drawStroke) {
            canvas.drawPath(path, strokePaint)
        }
    }

    @CallSuper
    open fun show() {
        syncArrowPath = true
        firstYAxis = y
        firstXAxis = x
        animator?.animateShow(this)
    }

    @CallSuper
    open fun hide() {
        syncArrowPath = false
        animator?.let {
            it.endHideAction = endHideAction
            it.animateHide(this)
        } ?: kotlin.run {
            endHideAction?.run()
        }
    }

    fun runOnHidden(runnable: Runnable?) {
        this.endHideAction = runnable
    }

    private fun adjustArrowPathHorizontal(targetWidth: Int) {
        val p = parent as ViewGroup
        val pHalfWidth = p.width / 2F

        if (targetLocation[0] + targetWidth / 2F < pHalfWidth) {
            adjustArrowPath(Gravity.LEFT)
        } else {
            val w = width
            if (firstXAxis + w > targetLocation[0] + targetWidth) {
                if (firstXAxis < targetLocation[0]) {
                    adjustArrowPath(Gravity.RIGHT)
                    return
                } else {
                    if (firstXAxis + w > targetLocation[0] + targetWidth) {
                        adjustArrowPath(Gravity.LEFT)
                    } else {
                        adjustArrowPath(Gravity.RIGHT)
                    }
                }
            } else {
                adjustArrowPath(Gravity.RIGHT)
            }
        }


//        if ((x > targetLocation[0] && x < targetLocation[0] + targetWidth) or (x > targetLocation[0] + targetWidth / 2)) {
//            if (targetLocation[0] + targetWidth / 2F < pWidth / 2) {
//                adjustArrowPath(Gravity.LEFT)
//            } else {
//                adjustArrowPath(Gravity.RIGHT)
//            }
//        } else {
//            if (targetLocation[0] + targetWidth / 2F < pWidth / 2) {
//                adjustArrowPath(Gravity.LEFT)
//            } else {
//                adjustArrowPath(Gravity.RIGHT)
//            }
//        }
    }

    private fun adjustArrowPathVertical() {
        val bottom = y + height
        val target = targetView
        if (target != null) {
            val targetBottom = targetLocation[1] + target.height
            if (bottom <= targetBottom && bottom >= targetLocation[1]) {
                adjustArrowPath(Gravity.BOTTOM)
                return
            }
        }
        if (bottom <= targetLocation[1]) {
            adjustArrowPath(Gravity.BOTTOM)
        } else if (y >= targetLocation[1]) {
            adjustArrowPath(Gravity.TOP)
        }
    }

    private fun adjustPath() {
        path.reset()

        path.fillType = Path.FillType.WINDING
        val halfStrokeWidth = strokePaint.strokeWidth / 2F
        path.addRoundRect(
            paddingLeft + halfStrokeWidth,
            paddingTop + halfStrokeWidth,
            width - paddingRight - halfStrokeWidth,
            height - paddingBottom - halfStrokeWidth,
            cornerRadius, cornerRadius, Path.Direction.CCW
        )

        if (drawArrow) {
            if (syncArrowPath) {
                targetView?.let { target ->
                    if (orientation == ArrowPanel.ORIENTATION_HORIZONTAL) {
                        adjustArrowPathHorizontal(target.width)
                    } else if (orientation == ArrowPanel.ORIENTATION_VERTICAL) {
                        adjustArrowPathVertical()
                    } else {
                        if (height < target.height) {
                            adjustArrowPathHorizontal(target.width)
                        } else {
                            adjustArrowPathVertical()
                        }
                    }
                } ?: kotlin.run {
                    if (firstYAxis + height <= targetLocation[1]) {
                        adjustArrowPath(Gravity.BOTTOM)
                    } else if (firstYAxis >= targetLocation[1]) {
                        adjustArrowPath(Gravity.TOP)
                    } else {
                        if (firstXAxis < targetLocation[0]) {
                            adjustArrowPath(Gravity.RIGHT)
                        } else {
                            adjustArrowPath(Gravity.LEFT)
                        }
                    }
                }
            }
            path.addPath(arrowPath)
        }
        path.op(path, Path.Op.UNION)
    }

    @SuppressLint("RtlHardcoded")
    private fun adjustArrowPath(@ArrowEdgeDef edge: Int) {
        if (!arrowPath.isEmpty) {
            arrowPath.reset()
        }
        arrowEdge = edge

        var targetWidth = 0
        var targetHeight = 0
        val halfStroke = strokePaint.strokeWidth / 2F

        targetView?.let {
            targetWidth = it.width
            targetHeight = it.height
        }

        if (edge == Gravity.TOP || edge == Gravity.BOTTOM) {
            var centerPointX = targetLocation[0] - x + targetWidth / 2F
            var edgePointX: Float = -1.0F
            var edgePointY: Float = -1.0F

            when {
                centerPointX < 0 -> {
                    centerPointX = 0.0F
                }
                centerPointX > width -> {
                    centerPointX = width.toFloat()
                }
            }

            var leftPointX: Float = centerPointX - arrowWidth / 2F
            var rightPointX: Float = leftPointX + arrowWidth

            var arrowInLeftSide = false
            when {
                leftPointX < arrowHeight + cornerRadius -> {
                    arrowInLeftSide = true
                    leftPointX = cornerRadius + arrowHeight
                    rightPointX = leftPointX + arrowWidth

                    leftPointX = arrowHeight.toFloat()

                    edgePointX = arrowHeight.toFloat() + halfStroke
                    edgePointY = height - arrowHeight - cornerRadius
                    rightPointX -= cornerRadius
                    centerPointX -= cornerRadius
                    if (centerPointX > arrowHeight) {
                        centerPointX = arrowHeight.toFloat()
                    } else if (centerPointX < 0) {
                        centerPointX = 0.0F
                    }
                    //Left
                }
                rightPointX > width - arrowHeight - cornerRadius -> {
                    arrowInLeftSide = false
                    rightPointX = width - cornerRadius - arrowHeight
                    leftPointX = rightPointX - arrowWidth
                    //Rights

                    edgePointX = width - arrowHeight.toFloat() - halfStroke
                    edgePointY = height - arrowHeight - cornerRadius
                    rightPointX += cornerRadius
                    leftPointX += cornerRadius
                    centerPointX += cornerRadius
                    if (centerPointX < edgePointX) {
                        centerPointX = edgePointX
                    } else if (centerPointX > width) {
                        centerPointX = width.toFloat()
                    }
                }
            }

            val centerPointY: Float
            val pointY: Float

            when (edge) {
                Gravity.TOP -> {
                    pointY = arrowHeight.toFloat() + halfStroke
                    centerPointY = 0.0F

                    if (arrowInLeftSide) {
                        if (cornerRadius > arrowWidth) {
                            arrowPath.moveTo(cornerRadius * 1.3F, pointY)
                            arrowPath.lineTo(rightPointX, pointY)
                        } else {
                            arrowPath.moveTo(rightPointX, pointY)
                        }
                        arrowPath.lineTo(centerPointX, centerPointY)
                        arrowPath.lineTo(leftPointX, pointY)
                    } else {
                        arrowPath.moveTo(rightPointX, pointY)
                        arrowPath.lineTo(centerPointX, centerPointY)
                        arrowPath.lineTo(leftPointX, pointY)

                        if (cornerRadius > arrowWidth) {
                            arrowPath.lineTo(leftPointX - (cornerRadius - arrowWidth), pointY)
                        }
                    }

                    if (edgePointX != -1.0F && edgePointY != -1.0F) {
                        arrowPath.lineTo(edgePointX, edgePointY)
                    }
                }
                else -> {
                    pointY = (height - arrowHeight).toFloat() - halfStroke
                    centerPointY = height.toFloat()
                    if (edgePointX != -1.0F && edgePointY != -1.0F) {
                        arrowPath.moveTo(edgePointX, height - edgePointY)
                        if (!arrowInLeftSide && cornerRadius > arrowWidth) {
                            arrowPath.lineTo(leftPointX - (cornerRadius - arrowWidth), pointY)
                        }
                        arrowPath.lineTo(leftPointX, pointY)
                    } else {
                        arrowPath.moveTo(leftPointX, pointY)
                    }

                    arrowPath.lineTo(centerPointX, centerPointY)
                    arrowPath.lineTo(rightPointX, pointY)

                    if (arrowInLeftSide && cornerRadius > arrowWidth) {
                        arrowPath.lineTo(cornerRadius * 1.3F, pointY)
                    }
                }
            }

            if (pivotToArrow) {
                pivotX = rightPointX - arrowWidth / 2
                pivotY = centerPointY
            }
        } else {
            var centerPointY = targetLocation[1] - y + targetHeight / 2F
            if (centerPointY < 0) {
                centerPointY = 0.0F
            } else if (centerPointY > height) {
                centerPointY = height.toFloat()
            }

            var topPointY: Float = centerPointY - arrowWidth / 2F
            var bottomPointY: Float = topPointY + arrowWidth

            when {
                topPointY < arrowHeight + cornerRadius -> {
                    topPointY = cornerRadius + arrowHeight
                    bottomPointY = topPointY + arrowWidth
                }
                bottomPointY > height - cornerRadius - arrowHeight -> {
                    bottomPointY = height - cornerRadius - arrowHeight
                    topPointY = bottomPointY - arrowWidth
                }
            }

            val centerPointX: Float
            val pointX: Float

            if (edge == Gravity.LEFT) {
                pointX = arrowHeight + halfStroke
                centerPointX = 0.0F
            } else {
                pointX = width - arrowHeight - halfStroke
                centerPointX = width.toFloat()
            }

            arrowPath.moveTo(pointX, topPointY)
            arrowPath.lineTo(centerPointX, centerPointY)
            arrowPath.lineTo(pointX, bottomPointY)

            if (pivotToArrow) {
                pivotX = pointX
                pivotY = centerPointY
            }
        }
        arrowPath.close()
    }

    fun inflateLayout(layoutId: Int): View {
        return inflate(context, layoutId, this)
    }

    open fun setFillColor(@ColorInt color: Int) {
        paint.color = color
    }

    open fun setStrokeColor(@ColorInt color: Int) {
        strokePaint.color = color
    }

    open fun setStrokeWidth(strokeWidth: Float) {
        strokePaint.strokeWidth = strokeWidth
        drawStroke = strokeWidth > 0.0F
    }

    @CallSuper
    open fun setTargetLocation(x: Int, y: Int) {
        targetLocation[0] = x
        targetLocation[1] = y
    }

    open fun clearShadow() {
        strokePaint.clearShadowLayer()
        paint.clearShadowLayer()
    }

    open fun setShadow(radius: Float, dx: Float, dy: Float, shadowColor: Int) {
        paint.setShadowLayer(radius, dx, dy, shadowColor)
    }

    companion object {
        @JvmStatic
        fun newInstance(context: Context): ArrowLayout {
            return ArrowLayout(context).apply {
                id = View.generateViewId()
                layoutParams = LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT
                )
            }
        }

        @JvmStatic
        private fun makeMeasureSpec(maxSize: Int, measureSpec: Int): Int {
            var s = measureSpec
            if (maxSize > 0) {
                val size = MeasureSpec.getSize(s)
                when (MeasureSpec.getMode(s)) {
                    MeasureSpec.AT_MOST -> {
                        s = MeasureSpec.makeMeasureSpec(min(size, maxSize), MeasureSpec.AT_MOST)
                    }
                    MeasureSpec.UNSPECIFIED -> {
                        s = MeasureSpec.makeMeasureSpec(maxSize, MeasureSpec.AT_MOST)
                    }
                    MeasureSpec.EXACTLY -> {
                        s = MeasureSpec.makeMeasureSpec(min(size, maxSize), MeasureSpec.EXACTLY)
                    }
                }
            }
            return s
        }
    }

    @kotlin.annotation.Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    @IntDef(Gravity.TOP, Gravity.BOTTOM, Gravity.LEFT, Gravity.RIGHT, Gravity.NO_GRAVITY)
    annotation class ArrowEdgeDef
}