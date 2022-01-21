package com.mahdikh.vision.arrowpanel.widget

import android.view.View

class PanelParams {
    var mViewLayoutResId: Int = -1
    var mView: View? = null
    var targetView: View? = null

    var onChildClickListener: PanelInterface.OnChildClickListener? = null
    var onChildLongClickListener: PanelInterface.OnChildLongClickListener? = null
    var clickIds: IntArray? = null
    var longClickIds: IntArray? = null

    var isAttachedContentView = false

    fun attachContentView(panel: ArrowPanel) {
        val instance = mView
        if (instance != null) {
            isAttachedContentView = true
            panel.setContentView(instance)
        } else {
            if (mViewLayoutResId != -1) {
                isAttachedContentView = true
                panel.setContentView(mViewLayoutResId)
                return
            }
            throw NullPointerException()
        }
    }

    fun apply(panel: ArrowPanel) {
        if (!isAttachedContentView) {
            val instance = mView
            if (instance != null) {
                panel.setContentView(instance)
            } else {
                panel.setContentView(mViewLayoutResId)
            }
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
    }
}