package com.mahdikh.vision.arrowpanel.widget

import android.content.Context
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

open class FragmentArrowPanel(context: Context) : ArrowPanel(context) {
    var mFragment: PanelFragment? = null
    var showOnResume = true

    override var type: Int = TYPE_DECOR
        get() = super.type
        set(value) {
            if (value != TYPE_WINDOW) {
                field = value
            }
        }

    @CallSuper
    fun setContentView(fragment: PanelFragment) {
        beginTransaction().remove(fragment).commitNow()
        fragment.panel = this
        this.mFragment = fragment
    }

    @CallSuper
    override fun onShow() {
        val frg = mFragment
        if (frg != null) {
            if (reusable && frg.isAdded && !frg.isVisible) {
                beginTransaction().show(frg).commit()
                super.onShow()
            } else if (!frg.isAdded) {
                if (showOnResume) {
                    frg.onResumeListener = PanelFragment.OnResumeListener {
                        super.onShow()
                        frg.onResumeListener = null
                    }
                    onAddFragment(frg)
                } else {
                    onAddFragment(frg)
                    super.onShow()
                }
            }
        } else {
            super.onShow()
        }
    }

    @CallSuper
    protected open fun onAddFragment(fragment: Fragment) {
        beginTransaction().add(arrowLayout.id, fragment).commit()
    }

    @CallSuper
    protected open fun onRemoveFragment() {
        val frg = this.mFragment
        if (frg != null) {
            val manager = getFragmentManger()
            if (!manager.isDestroyed) {
                manager.beginTransaction().apply {
                    setCustomAnimations(0, 0)
                    if (reusable) {
                        hide(frg)
                    } else {
                        remove(frg)
                    }
                }.commit()
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        onRemoveFragment()
    }

    protected open fun beginTransaction(): FragmentTransaction {
        return getFragmentManger().beginTransaction().setCustomAnimations(0, 0)
    }

    protected open fun getFragmentManger(): FragmentManager {
        return (context as FragmentActivity).supportFragmentManager
    }
}