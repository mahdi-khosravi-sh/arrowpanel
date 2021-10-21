package com.mahdikh.vision.arrowpanel.widget;

import android.view.View;

public interface PanelInterface {
    void dismiss();

    void cancel();

    interface OnShowListener {
        void onShow(PanelInterface panelInterface);
    }

    interface OnDismissListener {
        void onDismiss(PanelInterface panelInterface);
    }

    interface OnCancelListener {
        void onCancel(PanelInterface panelInterface);
    }

    interface OnChildClickListener {
        void onClick(View view, PanelInterface panelInterface);
    }

    interface OnChildLongClickListener {
        boolean onLongClick(View view, PanelInterface panelInterface);
    }
}
