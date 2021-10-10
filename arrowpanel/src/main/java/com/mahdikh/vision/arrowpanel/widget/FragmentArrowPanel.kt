package com.mahdikh.vision.arrowpanel.widget

import android.content.Context
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

open class FragmentArrowPanel(context: Context) : ArrowPanel(context) {
    var fragment: Fragment? = null

    override var type: Int = TYPE_DECOR
        get() = super.type
        set(value) {
            if (value != TYPE_WINDOW) {
                field = value
            }
        }

    @CallSuper
    fun setContentView(fragment: Fragment) {
        this.fragment = fragment
        if (fragment is PanelFragment) {
            fragment.panel = this
        }
    }

    @CallSuper
    override fun onShow() {
        val frg = fragment
        if (frg != null) {
            if (reusable && frg.isAdded && !frg.isVisible) {
                getFragmentManger()
                    .beginTransaction()
                    .show(frg)
                    .commit()
            } else if (!frg.isAdded) {
                onAddFragment(frg)
            }
        }
        super.onShow()
    }

    @CallSuper
    protected open fun onAddFragment(fragment: Fragment) {
        getFragmentManger()
            .beginTransaction()
            .add(arrowLayout.id, fragment)
            .commit()
    }

    @CallSuper
    protected open fun onRemoveFragment() {
        val frg = this.fragment
        if (frg != null) {
            getFragmentManger().beginTransaction().apply {
                if (reusable) {
                    hide(frg)
                } else {
                    remove(frg)
                }
            }.commit()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        onRemoveFragment()
    }

    private fun getFragmentManger(): FragmentManager {
        return (context as FragmentActivity).supportFragmentManager
    }
}