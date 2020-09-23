package nl.hr.projectrage

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import kotlinx.android.synthetic.main.activity_introduction.*

/**
 * Created by maartendegoede on 2019-05-22.
 * Copyright © 2019 insertCode.eu. All rights reserved.
 */
class IntroductionActivity : AppCompatActivity() {
    private var startSelectCodewordActivityOnFinish = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_introduction)

        startSelectCodewordActivityOnFinish = intent.extras != null && intent.extras!!.getBoolean("startSelectCodewordActivity", false)

        introductionPager!!.adapter = IntroductionPageAdapter()
        introductionPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val pagesCount = introductionPager.adapter!!.itemCount
                if (position + 1 == pagesCount) {
                    introductionSkipButton.visibility = View.INVISIBLE
                    introductionNextButton.text = "Finish"
                } else {
                    introductionSkipButton.visibility = View.VISIBLE
                    introductionNextButton.text = "Next"
                }
            }
        })

        introductionNextButton.setOnClickListener {
            val pagesCount = introductionPager.adapter!!.itemCount
            if (introductionPager.currentItem + 1 == pagesCount) finishOnboarding() else introductionPager.currentItem = introductionPager.currentItem + 1
        }

        introductionSkipButton.setOnClickListener { finishOnboarding() }
    }

    private fun finishOnboarding() {
        PreferenceManager.getDefaultSharedPreferences(this)
            .edit()
            .putBoolean("hasShownTutorial", true)
            .apply()
        if (startSelectCodewordActivityOnFinish)
            startActivity(Intent(this, SelectCodewordActivity::class.java).apply {
                putExtra("startMainActivity", true)
            })
        finish()
    }
}