package com.mahdikh.vision.arrowpanel.widget;

import android.view.View;

public interface ArrowInterface {
    void dismiss();

    void cancel();

    interface OnShowListener {
        void onShow(ArrowInterface arrowInterface);
    }

    interface OnDismissListener {
        void onDismiss(ArrowInterface arrowInterface);
    }

    interface OnCancelListener {
        void onCancel(ArrowInterface arrowInterface);
    }

    interface OnChildClickListener {
        void onClick(View view, ArrowInterface arrowInterface);
    }

    interface OnChildLongClickListener {
        boolean onLongClick(View view, ArrowInterface arrowInterface);
    }
}
