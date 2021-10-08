package com.mahdikh.arrowpanel

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.mahdikh.vision.arrowpanel.animator.SlideAnimator
import com.mahdikh.vision.arrowpanel.widget.ArrowPanel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.btn).setOnClickListener { view ->
            ArrowPanel.Builder(this)
                .setContentView(R.layout.panel)
                .setAnimator(SlideAnimator(Gravity.START))
                .setDim(Color.BLACK, 0.6F)
                .setTargetView(view)
                .setPivotToArrow(false)
                .setType(ArrowPanel.TYPE_DECOR)
                .show()
        }
    }
}