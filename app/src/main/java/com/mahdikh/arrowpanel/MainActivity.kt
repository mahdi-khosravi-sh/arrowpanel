package com.mahdikh.arrowpanel

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.mahdikh.vision.arrowpanel.animator.ScaleAnimator
import com.mahdikh.vision.arrowpanel.animator.SlideAnimator
import com.mahdikh.vision.arrowpanel.touchanimator.RotationXYTouchAnimator
import com.mahdikh.vision.arrowpanel.widget.ArrowPanel
import com.mahdikh.vision.arrowpanel.widget.FragmentArrowPanel
import com.mahdikh.vision.arrowpanel.widget.PanelInterface

class MainActivity : AppCompatActivity() {
    private lateinit var motionEvent: MotionEvent
    private var frg: ImageFragment = ImageFragment()
    private var frgPanel: FragmentArrowPanel? = null
    private val touchListener = object : View.OnTouchListener {
        var x: Float = 0.0F
        var y: Float = 0.0F

        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    x = event.x
                    y = event.y
                }
                MotionEvent.ACTION_MOVE -> {
                    v.x = v.x + event.x - x
                    v.y = v.y + event.y - y
                }
            }
            return false
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViews()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun findViews() {
        findViewById<View>(R.id.btn).setOnTouchListener(touchListener)
        findViewById<View>(R.id.btn2).setOnTouchListener(touchListener)
        findViewById<View>(R.id.root).setOnClickListener { showNewPanel() }
        findViewById<View>(R.id.btn).setOnClickListener { showPanel(it) }
        findViewById<View>(R.id.btn2).setOnClickListener { showNewPanel(it) }
    }

    private fun showPanelFragment(v: View) {
        if (frgPanel == null) {
            frgPanel = FragmentArrowPanel(this).apply {
                setContentView(frg)
                setDim(Color.BLACK, 0.3F)
                setAnimator(ScaleAnimator())
                setFillColor(Color.WHITE)
                setTouchAnimator(RotationXYTouchAnimator())
                setPivotToArrow(true)
                showOnResume = true
                reusable = true
                show(v)
            }
        } else {
            frgPanel?.show(v)
        }
    }

    private fun showFaPanel() {
        ArrowPanel.Builder(this)
            .setOnChildClickListener(
                { v, arrowInterface ->
                    toast("${(v as TextView).text}")
                    arrowInterface.dismiss()
                },
                R.id.tv1, R.id.tv2, R.id.tv3,
                R.id.tv4, R.id.tv5, R.id.tv6,
                R.id.tv7, R.id.tv8, R.id.tv9,
                R.id.tv10
            )
            .setFillColor(Color.parseColor("#EE444444"))
            .setType(ArrowPanel.TYPE_DECOR)
            .setLocation(motionEvent)
            .clearShadow()
            .setAnimator(SlideAnimator(Gravity.BOTTOM))
            .setInteractionWhenTouchOutside(false)
            .setCancelableOnTouchOutside(true)
            .setCancelable(true)
            .setDrawArrow(true)
            .setArrowMargin(0)
            .setDrawBlurEffect(false)
            .setBlurQuality(10)
            .setBlurRadius(10F)
            .setPivotToArrow(true)
            .setContentView(R.layout.panel_fa)
            .show()
    }

    private fun showPanel(view: View) {
        ArrowPanel.Builder(this)
            .setContentView(R.layout.panel)
            .setDim(Color.BLACK, 0.0F)
            .setTargetView(null)
            .setDrawTargetView(true)
            .setInteractionWhenTouchOutside(false)
            .setCancelableOnTouchOutside(true)
            .setOnChildClickListener({ v, arrowInterface ->
                toast("${(v as TextView).text}")
                arrowInterface.dismiss()
            })
            .setOnChildLongClickListener({ v, arrowInterface ->
                toast("${(v as TextView).text}")
                arrowInterface.dismiss()
                true
            })
            .setAnimator(SlideAnimator(Gravity.BOTTOM).apply {
                hideReverse = false
            })
            .setTouchAnimator(RotationXYTouchAnimator())
            .setFillColor(Color.parseColor("#EE444444"))
            .clearShadow()
            .show(view)
    }

    @SuppressLint("SetTextI18n")
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        motionEvent = ev
        return super.dispatchTouchEvent(ev)
    }

    private fun toast(text: CharSequence) {
        Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
    }

    private fun showNewPanel(view: View) {
        DockPanel(this).show(view)
    }

    private fun showNewPanel() {
        DockPanel(this).show(motionEvent)
    }

    class DockPanel(context: Context?) :
        PanelInterface.OnChildClickListener, PanelInterface.OnShowListener {
        private var menuPanel: ArrowPanel?
        override fun onClick(view: View, panelInterface: PanelInterface) {
            val id = view.id
            if (id == R.id.itemAllWindows) {
                TransitionManager.beginDelayedTransition(
                    menuPanel!!.arrowLayout,
                    AutoTransition().setDuration(200)
                )
//                menuPanel!!.arrowLayout.layoutParams.height = menuPanel!!.arrowLayout.height
                val rv = menuPanel!!.findView(R.id.previewRecyclerView) as RecyclerView
                val ids = intArrayOf(
                    R.id.itemCloseAllWindow,
                    R.id.divider,
                    R.id.itemMinimizeWindows,
                    R.id.itemResotreMinimizedWindows,
                    R.id.itemCloseAllWindow,
                    R.id.itemRemoveFromDock,
                    R.id.itemNewWindow,
                    R.id.itemShowDetails
                )
                if (rv.visibility == View.VISIBLE) {
                    rv.visibility = View.GONE
//                    setVisibility(View.VISIBLE, *ids)
                } else {
                    rv.visibility = View.VISIBLE
//                    setVisibility(View.GONE, *ids)
                }
            }
        }

        fun setVisibility(visibility: Int, vararg ids: Int) {
            for (id in ids) {
                menuPanel!!.findView(id).visibility = visibility
            }
        }

        fun show(view: View?) {
            menuPanel!!.show(view!!)
        }

        fun show(event: MotionEvent) {
            menuPanel!!.show(event)
        }

        override fun onShow(panelInterface: PanelInterface) {
//            (menuPanel!!.arrowLayout.layoutParams as FrameLayout.LayoutParams).gravity =
//                Gravity.LEFT or Gravity.BOTTOM
//            val location = IntArray(2)
//            menuPanel!!.targetView!!.getLocationOnScreen(location)
//            menuPanel!!.arrowLayout.y = -(menuPanel!!.height - location[1]).toFloat()
        }

        init {
            val IDS = intArrayOf(
                R.id.itemCloseAllWindow,
                R.id.itemNewWindow,
                R.id.itemAllWindows,
                R.id.itemMinimizeWindows,
                R.id.itemResotreMinimizedWindows
            )
            menuPanel = ArrowPanel.Builder(context!!)
                .setContentView(R.layout.dock_item_menu_panel)
                .setOrientation(ArrowPanel.ORIENTATION_VERTICAL)
                .setAnimator(ScaleAnimator())
                .setFillColor(Color.parseColor("#EFFFFFFF"))
                .clearShadow()
                .setDrawTargetView(false)
                .setCornerRadius(12f)
                .setDim(Color.BLACK, 0.4f)
                .setOnChildClickListener(this, *IDS)
                .addOnShowListener(this)
                .build()
        }
    }
}