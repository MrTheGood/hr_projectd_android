package nl.hr.projectrage

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import kotlinx.android.synthetic.main.activity_select_codeword.*

class SelectCodewordActivity : AppCompatActivity() {
    private var startMainActivityOnFinish = false
    private val sharedPreferences by lazy { application.getSharedPreferences("App", 0) }
    private lateinit var wordAnalysis: WordAnalysis

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_codeword)

        startMainActivityOnFinish = intent.extras != null && intent.extras!!.getBoolean("startMainActivity", false)

        wordAnalysis = WordAnalysis(resources)
        val text = sharedPreferences.getString("codeword", "kiwi")

        input.setText(text)
        inputLayout.helperText = "Codeword Quality: ${wordAnalysis.getScore(text!!)}"


        input.doAfterTextChanged {
            try {
                inputLayout.helperText = "Codeword Quality: ${wordAnalysis.getScore(it.toString().toLowerCase())}"
                inputLayout.error = null
            } catch (e: IllegalStateException) {
                inputLayout.error = e.message.takeIf {
                    it in listOf("Not a valid word", "Failed to determine quality")
                } ?: throw e
            }
        }


        confirmButton.setOnClickListener {
            if (inputLayout.error != null) {
                AlertDialog.Builder(this@SelectCodewordActivity)
                    .setTitle("Error!")
                    .setMessage("Please pick a valid word")
                    .setCancelable(false)
                    .setPositiveButton("OK", null)
                    .show()
                return@setOnClickListener
            }


            sharedPreferences.edit()
                .putString("codeword", input.text.toString())
                .apply()

            AlertDialog.Builder(this@SelectCodewordActivity)
                .setTitle("Done!")
                .setMessage("Text has been changed to \"${input.text}\"")
                .setCancelable(false)
                .setPositiveButton("OK") { _, _ ->
                    if (startMainActivityOnFinish)
                        startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                .show()
        }
    }
}
