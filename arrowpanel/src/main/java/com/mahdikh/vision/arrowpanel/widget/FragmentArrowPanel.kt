package com.mahdikh.vision.arrowpanel.widget

import android.content.Context
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

    fun setContentView(fragment: Fragment) {
        this.fragment = fragment
        addFragment(fragment)
    }

    private fun addFragment(fragment: Fragment) {
        arrowLayout.removeAllViews()
        if (fragment is PanelFragment) {
            fragment.panel = this
        }
        getFragmentManger()
            .beginTransaction()
            .add(arrowLayout.id, fragment)
            .commit()
    }

    protected open fun onRemoveFragment() {
        val fragment = this.fragment
        if (fragment != null) {
            getFragmentManger()
                .beginTransaction()
                .remove(fragment)
                .commit()
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