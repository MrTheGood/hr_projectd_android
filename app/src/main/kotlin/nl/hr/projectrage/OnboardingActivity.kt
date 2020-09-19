package nl.hr.projectrage

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import kotlinx.android.synthetic.main.activity_onboarding.*
import nl.hr.projectrage.MainActivity

/**
 * Created by maartendegoede on 2019-05-22.
 * Copyright © 2019 insertCode.eu. All rights reserved.
 */
class OnboardingActivity : AppCompatActivity() {
    private var startMainActivityOnFinish = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        startMainActivityOnFinish = intent.extras != null && intent.extras!!.getBoolean("startMainActivity", false)

        onboardingPager!!.adapter = OnboardingPageAdapter()
        onboardingPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val pagesCount = onboardingPager.adapter!!.itemCount
                if (position + 1 == pagesCount) {
                    onboardingSkipButton.visibility = View.INVISIBLE
                    onboardingNextButton.text = "Finish"
                } else {
                    onboardingSkipButton.visibility = View.VISIBLE
                    onboardingNextButton.text = "Next"
                }
            }
        })

        onboardingNextButton.setOnClickListener {
            val pagesCount = onboardingPager.adapter!!.itemCount
            if (onboardingPager.currentItem + 1 == pagesCount) finishOnboarding() else onboardingPager.currentItem = onboardingPager.currentItem + 1
        }

        onboardingSkipButton.setOnClickListener { finishOnboarding() }
    }

    private fun finishOnboarding() {
        PreferenceManager.getDefaultSharedPreferences(this)
            .edit()
            .putBoolean("hasShownTutorial", true)
            .apply()
        if (startMainActivityOnFinish) startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}