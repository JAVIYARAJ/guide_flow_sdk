package com.rajjaviya.guideflow.sample

import android.os.Bundle
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import android.view.View
import androidx.core.graphics.toColorInt

class FragmentDemoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        
        val toolbar = MaterialToolbar(this).apply {
            title = "Fragment Support"
            setTitleTextColor(android.graphics.Color.WHITE)
            setBackgroundColor("#3B82F6".toColorInt())
        }
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }
        
        layout.addView(toolbar, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ))
        
        val frameLayout = FrameLayout(this).apply {
            id = View.generateViewId()
        }
        layout.addView(frameLayout, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            0,
            1f
        ))
        
        setContentView(layout)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(frameLayout.id, DemoFragment())
                .commit()
        }
    }
}
