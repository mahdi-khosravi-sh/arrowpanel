package com.mahdikh.vision.arrowpanel.widget

import android.view.View

class PanelParams {
    var mViewLayoutResId: Int = 0
    var mView: View? = null
    var targetView: View? = null
    var drawBlurEffect = false

    var onChildClickListener: PanelInterface.OnChildClickListener? = null
    var onChildLongClickListener: PanelInterface.OnChildLongClickListener? = null
    var clickIds: IntArray? = null
    var longClickIds: IntArray? = null

    fun apply(panel: ArrowPanel) {
        mView?.let {
            panel.setContentView(it)
        } ?: kotlin.run {
            panel.setContentView(mViewLayoutResId)
        }

        val cIds = clickIds
        if (cIds != null) {
            panel.setOnChildClickListener(onChildClickListener, *cIds)
        }
        val lcIds = longClickIds
        if (lcIds != null) {
            panel.setOnChildLongClickListener(onChildLongClickListener, *lcIds)
        }

        panel.targetView = targetView

        if (drawBlurEffect) {
            panel.setDrawBlurEffect(true)
        }
    }
}