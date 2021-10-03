package com.mahdikh.vision.arrowpanel.widget;

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
}
