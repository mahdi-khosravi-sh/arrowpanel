package com.mahdikh.arrowpanel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mahdikh.vision.arrowpanel.widget.PanelFragment

class ImageFragment : PanelFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.image_fragment, container, false)
        findViews(v)
        return v
    }

    private fun findViews(v: View) {
        v.findViewById<View>(R.id.imageView).setOnClickListener { dismiss() }
    }
}