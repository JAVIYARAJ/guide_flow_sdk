#!/bin/bash
set -e
PKG="com/rajjaviya/guideflow/sample"
SRC="app/src/main/java/$PKG"
RES="app/src/main/res"

mkdir -p "$SRC" "$RES/layout" "$RES/values"

# 1. Main Dashboard Activity
cat << 'KOTLIN' > "$SRC/MainActivity.kt"
package com.rajjaviya.guideflow.sample

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_dashboard)

        findViewById<Button>(R.id.btnActivityDemo).setOnClickListener {
            startActivity(Intent(this, ActivityDemo::class.java))
        }
        findViewById<Button>(R.id.btnFragmentDemo).setOnClickListener {
            startActivity(Intent(this, FragmentDemoActivity::class.java))
        }
        findViewById<Button>(R.id.btnListDemo).setOnClickListener {
            startActivity(Intent(this, ListDemoActivity::class.java))
        }
        findViewById<Button>(R.id.btnDialogDemo).setOnClickListener {
            DemoBottomSheet().show(supportFragmentManager, "demo_bottom_sheet")
        }
    }
}
KOTLIN

cat << 'XML' > "$RES/layout/activity_main_dashboard.xml"
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="24dp"
    android:gravity="center_vertical">

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="GuideFlow SDK Demos"
        android:textSize="24sp"
        android:textStyle="bold"
        android:layout_marginBottom="32dp"
        android:gravity="center" />

    <Button
        android:id="@+id/btnActivityDemo"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Activity & UI Components"
        android:layout_marginBottom="16dp" />

    <Button
        android:id="@+id/btnFragmentDemo"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Fragment Support"
        android:layout_marginBottom="16dp" />

    <Button
        android:id="@+id/btnListDemo"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="RecyclerView Auto-Scroll"
        android:layout_marginBottom="16dp" />

    <Button
        android:id="@+id/btnDialogDemo"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="BottomSheet Dialog Support" />
</LinearLayout>
XML

# 2. Activity Demo (The one we just made)
mv "$SRC/MainActivity.kt" "$SRC/MainActivity.kt.bak" || true # prevent overwrite before writing
cat << 'KOTLIN' > "$SRC/ActivityDemo.kt"
package com.rajjaviya.guideflow.sample

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import com.rajjaviya.guideflow.animation.AnimationType
import com.rajjaviya.guideflow.api.GuideFlow
import com.rajjaviya.guideflow.model.GuideStep
import com.rajjaviya.guideflow.model.TourConfig
import com.rajjaviya.guideflow.model.TourTheme
import com.rajjaviya.guideflow.spotlight.SpotlightShape
import com.rajjaviya.guideflow.tooltip.TooltipPosition
import com.rajjaviya.guideflow.listener.TourListener
import com.rajjaviya.guideflow.model.TourSession

class ActivityDemo : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        val btnStartLightTour = findViewById<Button>(R.id.btnStartLightTour)
        val btnStartDarkTour = findViewById<Button>(R.id.btnStartDarkTour)
        val fab = findViewById<FloatingActionButton>(R.id.fab)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        btnStartLightTour.setOnClickListener { startTour(TourTheme.light()) }
        btnStartDarkTour.setOnClickListener { startTour(TourTheme.dark()) }
        fab.setOnClickListener { Toast.makeText(this, "Action clicked!", Toast.LENGTH_SHORT).show() }
    }

    private fun startTour(theme: TourTheme) {
        GuideFlow.with(this)
            .setTourId("activity_demo_tour")
            .forceShow(true)
            .setTheme(theme)
            .setConfig(
                TourConfig(
                    spotlightShape = SpotlightShape.ROUNDED_RECT,
                    spotlightPulseAnimation = true,
                    dismissOnOverlayClick = false,
                    enablePreviousButton = true,
                )
            )
            .addStep(
                GuideStep(
                    targetView = findViewById(R.id.toolbar),
                    title = "App Toolbar",
                    description = "This spotlight highlights the entire toolbar cleanly.",
                    tooltipPosition = TooltipPosition.BOTTOM,
                    animationType = AnimationType.SLIDE_UP
                )
            )
            .addStep(
                GuideStep(
                    targetView = findViewById(R.id.welcomeCard),
                    title = "Smart Positioning",
                    description = "GuideFlow automatically calculates the best place to show the tooltip so it doesn't fall off-screen.",
                    tooltipPosition = TooltipPosition.AUTO,
                    animationType = AnimationType.FADE
                )
            )
            .addStep(
                GuideStep(
                    targetView = findViewById(R.id.btnSmall),
                    title = "Small Targets",
                    description = "Spotlights can adapt to any view size.",
                    tooltipPosition = TooltipPosition.TOP,
                    animationType = AnimationType.BOUNCE
                )
            )
            .addStep(
                GuideStep(
                    targetView = findViewById(R.id.iconProfile),
                    title = "Profile Icon",
                    description = "Perfect for profile pictures.",
                    tooltipPosition = TooltipPosition.START,
                    animationType = AnimationType.SLIDE_UP
                )
            )
            .addStep(
                GuideStep(
                    targetView = findViewById(R.id.inputLayout),
                    title = "Form Fields",
                    description = "Highlight text inputs to guide users through complex forms.",
                    tooltipPosition = TooltipPosition.TOP,
                    animationType = AnimationType.SLIDE_UP
                )
            )
            .addStep(
                GuideStep(
                    targetView = findViewById(R.id.fab),
                    title = "Floating Actions",
                    description = "Notice how the tooltip elegantly positions itself to the left (START) or top.",
                    tooltipPosition = TooltipPosition.AUTO,
                    animationType = AnimationType.BOUNCE
                )
            )
            .setListener(object : TourListener {
                override fun onTourCompleted(session: TourSession) {
                    Toast.makeText(this@ActivityDemo, "Tour Completed!", Toast.LENGTH_SHORT).show()
                }
                override fun onTourDismissed(atStepIndex: Int) {
                    Toast.makeText(this@ActivityDemo, "Tour Skipped at step $atStepIndex", Toast.LENGTH_SHORT).show()
                }
            })
            .start()
    }
}
KOTLIN

# 3. Fragment Demo
cat << 'KOTLIN' > "$SRC/FragmentDemoActivity.kt"
package com.rajjaviya.guideflow.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class FragmentDemoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val frameLayout = android.widget.FrameLayout(this)
        frameLayout.id = android.view.View.generateViewId()
        setContentView(frameLayout)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(frameLayout.id, DemoFragment())
                .commit()
        }
    }
}
KOTLIN

cat << 'KOTLIN' > "$SRC/DemoFragment.kt"
package com.rajjaviya.guideflow.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.rajjaviya.guideflow.api.GuideFlow
import com.rajjaviya.guideflow.model.GuideStep
import com.rajjaviya.guideflow.model.TourConfig
import com.rajjaviya.guideflow.spotlight.SpotlightShape

class DemoFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_demo, container, false)
        val btnStart = view.findViewById<Button>(R.id.btnStartFragmentTour)
        val text1 = view.findViewById<TextView>(R.id.text1)
        val text2 = view.findViewById<TextView>(R.id.text2)

        btnStart.setOnClickListener {
            GuideFlow.with(this)
                .forceShow(true)
                .setConfig(TourConfig(spotlightShape = SpotlightShape.ROUNDED_RECT))
                .addStep(GuideStep(targetView = text1, title = "Fragment Title", description = "GuideFlow perfectly handles Fragment lifecycles."))
                .addStep(GuideStep(targetView = text2, title = "Fragment Content", description = "The overlay respects the fragment's root view."))
                .start()
        }
        return view
    }
}
KOTLIN

cat << 'XML' > "$RES/layout/fragment_demo.xml"
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="24dp"
    android:background="#F3F4F6">

    <TextView
        android:id="@+id/text1"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Fragment Screen"
        android:textSize="24sp"
        android:textStyle="bold"
        android:layout_marginBottom="16dp" />

    <TextView
        android:id="@+id/text2"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="This demo proves that GuideFlow overlays work seamlessly within Fragments without bleeding outside of the Fragment container."
        android:textSize="16sp"
        android:layout_marginBottom="32dp" />

    <Button
        android:id="@+id/btnStartFragmentTour"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Start Fragment Tour"
        android:layout_gravity="center_horizontal" />
</LinearLayout>
XML

# 4. List Demo Activity
cat << 'KOTLIN' > "$SRC/ListDemoActivity.kt"
package com.rajjaviya.guideflow.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rajjaviya.guideflow.api.GuideFlow
import com.rajjaviya.guideflow.model.GuideStep
import com.rajjaviya.guideflow.model.TourConfig
import com.rajjaviya.guideflow.model.TourTheme

class ListDemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val rv = RecyclerView(this)
        rv.layoutManager = LinearLayoutManager(this)
        setContentView(rv)

        val adapter = DemoAdapter()
        rv.adapter = adapter

        // Give RecyclerView time to layout
        rv.post {
            GuideFlow.with(this)
                .forceShow(true)
                .setTheme(TourTheme.dark())
                .setConfig(TourConfig(scrollToTarget = true))
                .addStep(GuideStep(
                    targetView = rv.layoutManager!!.findViewByPosition(0)!!, 
                    title = "First Item", 
                    description = "This is item at the top."
                ))
                .addStep(GuideStep(
                    targetView = rv.layoutManager!!.findViewByPosition(5)!!, 
                    title = "Auto Scroll", 
                    description = "GuideFlow automatically scrolled down to find this item!"
                ))
                .addStep(GuideStep(
                    targetView = rv.layoutManager!!.findViewByPosition(15)!!, 
                    title = "Deep Scroll", 
                    description = "It even works for items far down the list!"
                ))
                .start()
        }
    }

    class DemoAdapter : RecyclerView.Adapter<DemoAdapter.ViewHolder>() {
        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val text: TextView = view.findViewById(android.R.id.text1)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
            view.setPadding(32, 48, 32, 48)
            return ViewHolder(view)
        }
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.text.text = "List Item #${position + 1}"
        }
        override fun getItemCount() = 25
    }
}
KOTLIN

# 5. BottomSheet Demo
cat << 'KOTLIN' > "$SRC/DemoBottomSheet.kt"
package com.rajjaviya.guideflow.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rajjaviya.guideflow.api.GuideFlow
import com.rajjaviya.guideflow.model.GuideStep

class DemoBottomSheet : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_demo, container, false)
        val btnStart = view.findViewById<Button>(R.id.btnStartFragmentTour)
        val text1 = view.findViewById<TextView>(R.id.text1)
        val text2 = view.findViewById<TextView>(R.id.text2)
        
        text1.text = "Bottom Sheet"
        text2.text = "GuideFlow overlays can attach to Dialogs and BottomSheets easily."

        btnStart.setOnClickListener {
            GuideFlow.with(this)
                .forceShow(true)
                .addStep(GuideStep(targetView = text1, title = "Sheet Title", description = "Scoped inside the bottom sheet."))
                .addStep(GuideStep(targetView = btnStart, title = "Sheet Action", description = "Perfect for complex dialog onboarding."))
                .start()
        }
        return view
    }
}
KOTLIN

# Update AndroidManifest.xml
cat << 'XML' > "$RES/AndroidManifest.xml"
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.GuideFlow">

        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        
        <activity android:name=".ActivityDemo" />
        <activity android:name=".FragmentDemoActivity" />
        <activity android:name=".ListDemoActivity" />

    </application>
</manifest>
XML
