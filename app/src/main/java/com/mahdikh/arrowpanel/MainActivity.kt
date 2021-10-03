package com.mahdikh.arrowpanel

import android.graphics.Color
import android.os.Bundle
import android.view.animation.OvershootInterpolator
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.mahdikh.vision.arrowpanel.animator.ScaleAnimator
import com.mahdikh.vision.arrowpanel.widget.ArrowPanel

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.btn).setOnClickListener { view ->
            ArrowPanel.Builder(this)
                .setContentView(R.layout.panel)
                .setDim(Color.BLACK, 0.55F)
                .setAnimator(
                    ScaleAnimator()
                        .setInterpolator(OvershootInterpolator())
                        .setDuration(250)
                )
                .setTargetView(view)
                .setDrawTargetView(true)
                .setCreateAsWindow(true)
                .setBlurRadius(5F)
                .setBlurQuality(30)
                .setDrawBlurEffect(true)
                .show()
        }
    }
}