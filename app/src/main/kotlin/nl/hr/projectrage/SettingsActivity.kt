package nl.hr.projectrage

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {
    private val sharedPreferences by lazy { application.getSharedPreferences("App", 0) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val text = sharedPreferences.getString("codeword", "kiwi")

        input.setText(text)


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
