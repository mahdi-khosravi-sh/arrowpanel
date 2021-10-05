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
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.annotation.IntDef
import androidx.core.view.setPadding
import com.mahdikh.vision.arrowpanel.R
import com.mahdikh.vision.arrowpanel.animator.Animator

class ArrowContainer(context: Context) : FrameLayout(context) {
    private val strokePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val path: Path = Path()
    private val arrowPath: Path = Path()

    private val targetLocation: IntArray = IntArray(2)
    private var arrowEdge: Int = Gravity.NO_GRAVITY

    private var firstYAxis: Float = 0.0F
    private var firstXAxis: Float = 0.0F

    private var syncArrowLocation = true
    var drawArrow: Boolean = true
    var cornerRadius: Float = 15.0F

    var arrowWidth: Int = 20
    var arrowHeight: Int = 15
        set(value) {
            field = value
            setPadding(value)
        }

    var targetView: View? = null
        set(value) {
            field = value
            invalidate()
        }

    var animator: Animator? = null
        set(value) {
            field = value
            if (!isAttachedToWindow) {
                field?.initBeforeShow(this)
            }
        }

    init {
        setPadding(arrowHeight)

        isClickable = true
        isFocusable = true
        setWillNotDraw(false)

        paint.color = Color.WHITE

        strokePaint.style = Paint.Style.STROKE
        strokePaint.strokeCap = Paint.Cap.ROUND
        strokePaint.strokeJoin = Paint.Join.ROUND
        strokePaint.color = Color.WHITE

        layoutParams = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        )
        adjustAttrsFromTheme(context)
    }

    private fun adjustAttrsFromTheme(context: Context) {
        val a: TypedArray = context.obtainStyledAttributes(R.styleable.ArrowContainer)
        var shadowColor = Color.parseColor("#33000000")
        var shadowRadius = 5.0F
        var shadowDx = 0.0F
        var shadowDy = 0.0F

        val indexCount = a.indexCount
        var index: Int

        for (i in 0..indexCount) {
            index = a.getIndex(i)
            when (index) {
                R.styleable.ArrowContainer_arrowTip_fillColor -> {
                    setFillColor(a.getColor(index, Color.WHITE))
                }
                R.styleable.ArrowContainer_arrowTip_strokeColor -> {
                    setStrokeColor(a.getColor(index, Color.WHITE))
                }
                R.styleable.ArrowContainer_arrowTip_strokeWidth -> {
                    setStrokeWidth(a.getDimension(index, 0.0F))
                }
                R.styleable.ArrowContainer_arrowTip_arrowWidth -> {
                    arrowWidth = a.getDimensionPixelSize(index, 20)
                }
                R.styleable.ArrowContainer_arrowTip_arrowHeight -> {
                    arrowHeight = a.getDimensionPixelSize(index, 15)
                }
                R.styleable.ArrowContainer_arrowTip_shadowRadius -> {
                    shadowRadius = a.getDimension(index, 10.0F)
                }
                R.styleable.ArrowContainer_arrowTip_shadowDy -> {
                    shadowDy = a.getFloat(index, 0.0F)
                }
                R.styleable.ArrowContainer_arrowTip_shadowDx -> {
                    shadowDx = a.getFloat(index, 0.0f)
                }
                R.styleable.ArrowContainer_arrowTip_shadowColor -> {
                    shadowColor = a.getColor(index, shadowColor)
                }
                R.styleable.ArrowContainer_arrowTip_cornerRadius -> {
                    cornerRadius = a.getDimension(index, 15.0F)
                }
            }
        }
        if (shadowRadius > 0) {
            setShadow(shadowRadius, shadowDx, shadowDy, shadowColor)
        }
        a.recycle()
    }

    fun inflateLayout(layoutId: Int): View {
        return inflate(context, layoutId, this)
    }

    fun setFillColor(@ColorInt color: Int) {
        paint.color = color
    }

    fun setStrokeColor(@ColorInt color: Int) {
        strokePaint.color = color
    }

    fun setStrokeWidth(strokeWidth: Float) {
        strokePaint.strokeWidth = strokeWidth
    }

    fun setTargetLocation(x: Int, y: Int) {
        targetLocation[0] = x
        targetLocation[1] = y
    }

    fun getArrowEdge(): Int {
        return arrowEdge
    }

    fun clearShadow() {
        strokePaint.clearShadowLayer()
        paint.clearShadowLayer()
    }

    fun setShadow(radius: Float, dx: Float, dy: Float, shadowColor: Int) {
        paint.setShadowLayer(radius, dx, dy, shadowColor)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val action = ev.action
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_UP) {
            animator?.animateOnTouch(this, action)
        }
        return super.dispatchTouchEvent(ev)
    }

    fun show() {
        firstYAxis = y
        firstXAxis = x
        animator?.animateShow(this)
    }

    fun hide() {
        syncArrowLocation = false
        animator?.animateHide(this)
    }

    override fun draw(canvas: Canvas) {
        animator?.draw(canvas)
        super.draw(canvas)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        adjustPath()
        canvas.drawPath(path, paint)

        if (strokePaint.strokeWidth > 0.0F) {
            canvas.drawPath(path, strokePaint)
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
            if (syncArrowLocation) {
                targetView?.let { target ->
                    if (y + measuredHeight <= targetLocation[1]) {
                        adjustPath(Gravity.BOTTOM)
                    } else if (y >= targetLocation[1]) {
                        adjustPath(Gravity.TOP)
                    } else {
                        if ((x > targetLocation[0] && x < targetLocation[0] + target.width) or (x > targetLocation[0] + target.width / 2)) {
                            adjustPath(Gravity.LEFT)
                        } else {
                            adjustPath(Gravity.RIGHT)
                        }
                    }
                } ?: kotlin.run {
                    if (firstYAxis + measuredHeight <= targetLocation[1]) {
                        adjustPath(Gravity.BOTTOM)
                    } else if (firstYAxis >= targetLocation[1]) {
                        adjustPath(Gravity.TOP)
                    } else {
                        if (firstXAxis < targetLocation[0]) {
                            adjustPath(Gravity.RIGHT)
                        } else {
                            adjustPath(Gravity.LEFT)
                        }
                    }
                }
            }
            path.addPath(arrowPath)
        }
        path.op(path, Path.Op.UNION)
    }

    @SuppressLint("RtlHardcoded")
    private fun adjustPath(@ArrowEdgeDef edge: Int) {
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
                centerPointX > measuredWidth -> {
                    centerPointX = measuredWidth.toFloat()
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
                    edgePointY = measuredHeight - arrowHeight - cornerRadius
                    rightPointX -= cornerRadius
                    centerPointX -= cornerRadius
                    if (centerPointX > arrowHeight) {
                        centerPointX = arrowHeight.toFloat()
                    } else if (centerPointX < 0) {
                        centerPointX = 0.0F
                    }
                    //Left
                }
                rightPointX > measuredWidth - arrowHeight - cornerRadius -> {
                    arrowInLeftSide = false
                    rightPointX = measuredWidth - cornerRadius - arrowHeight
                    leftPointX = rightPointX - arrowWidth
                    //Rights

                    edgePointX = measuredWidth - arrowHeight.toFloat() - halfStroke
                    edgePointY = measuredHeight - arrowHeight - cornerRadius
                    rightPointX += cornerRadius
                    leftPointX += cornerRadius
                    centerPointX += cornerRadius
                    if (centerPointX < edgePointX) {
                        centerPointX = edgePointX
                    } else if (centerPointX > measuredWidth) {
                        centerPointX = measuredWidth.toFloat()
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
                    pointY = (measuredHeight - arrowHeight).toFloat() - halfStroke
                    centerPointY = measuredHeight.toFloat()
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

            pivotX = rightPointX - arrowWidth / 2
            pivotY = centerPointY
        } else {
            var centerPointY = targetLocation[1] - y + targetHeight / 2F
            if (centerPointY < 0) {
                centerPointY = 0.0F
            } else if (centerPointY > measuredHeight) {
                centerPointY = measuredHeight.toFloat()
            }

            var topPointY: Float = centerPointY - arrowWidth / 2F
            var bottomPointY: Float = topPointY + arrowWidth

            when {
                topPointY < arrowHeight + cornerRadius -> {
                    topPointY = cornerRadius + arrowHeight
                    bottomPointY = topPointY + arrowWidth
                }
                bottomPointY > measuredHeight - cornerRadius - arrowHeight -> {
                    bottomPointY = measuredHeight - cornerRadius - arrowHeight
                    topPointY = bottomPointY - arrowWidth
                }
            }

            val centerPointX: Float
            val pointX: Float

            if (edge == Gravity.LEFT) {
                pointX = arrowHeight + halfStroke
                centerPointX = 0.0F
            } else {
                pointX = measuredWidth - arrowHeight - halfStroke
                centerPointX = measuredWidth.toFloat()
            }

            arrowPath.moveTo(pointX, topPointY)
            arrowPath.lineTo(centerPointX, centerPointY)
            arrowPath.lineTo(pointX, bottomPointY)

            pivotX = pointX
            pivotY = centerPointY
        }
        arrowPath.close()
    }

    @kotlin.annotation.Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    @IntDef(Gravity.TOP, Gravity.BOTTOM, Gravity.LEFT, Gravity.RIGHT, Gravity.NO_GRAVITY)
    annotation class ArrowEdgeDef
}