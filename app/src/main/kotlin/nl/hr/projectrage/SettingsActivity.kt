package nl.hr.projectrage

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {
    private val sharedPreferences by lazy { application.getSharedPreferences("App", 0) }
    private lateinit var wordAnalysis: WordAnalysis

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        wordAnalysis = WordAnalysis(resources)
        val text = sharedPreferences.getString("codeword", "kiwi")

        input.setText(text)


        input.doAfterTextChanged {
            try {
                qualityIndicator.text = "Codeword Quality: ${wordAnalysis.getScore(it.toString())}"
            } catch (e: IllegalStateException) {
                qualityIndicator.text = e.message.takeIf {
                    it in listOf("Not a valid word", "Failed to determine quality")
                } ?: throw e
            }
        }

        confirmButton.setOnClickListener {
            sharedPreferences.edit()
                .putString("codeword", input.text.toString())
                .apply()

            AlertDialog.Builder(this@SettingsActivity)
                .setTitle("Done!")
                .setMessage("Text has been changed to \"${input.text}\"")
                .setCancelable(false)
                .setPositiveButton("OK") { _, _ -> finish() }
                .show()
        }
    }
}
