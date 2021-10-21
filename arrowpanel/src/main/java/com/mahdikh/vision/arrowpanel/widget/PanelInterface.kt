package com.mahdikh.vision.arrowpanel.widget

import android.view.View

interface PanelInterface {
    fun dismiss()

    fun cancel()

    fun interface OnShowListener {
        fun onShow(panelInterface: PanelInterface)
    }

    fun interface OnDismissListener {
        fun onDismiss(panelInterface: PanelInterface)
    }

    fun interface OnCancelListener {
        fun onCancel(panelInterface: PanelInterface)
    }

    fun interface OnChildClickListener {
        fun onClick(view: View, panelInterface: PanelInterface)
    }

    fun interface OnChildLongClickListener {
        fun onLongClick(view: View, panelInterface: PanelInterface): Boolean
    }
}