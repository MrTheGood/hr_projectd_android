package nl.hr.projectrage

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {
    companion object {
        const val requestAudio = 1
    }

    private val sharedPreferences by lazy { getSharedPreferences("App", 0) }
    private val speechRecognizer by lazy { SpeechRecognizer.createSpeechRecognizer(this) }
    private val speechRecognizerIntent by lazy {
        CodeWordListener(sharedPreferences) { matches ->
            displayResult(matches)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        speechRecognizer.setRecognitionListener(speechRecognizerIntent)


        val hasShownTutorial = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("hasShownTutorial", false)
        if (!hasShownTutorial) {
            val intent = Intent(this, OnboardingActivity::class.java)
            intent.putExtra("startMainActivity", true)
            startActivity(intent)
            finish()
        }


        settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        recordButton.setOnClickListener {
            startRecordingButtonPressed()
        }
    }

    override fun onStart() {
        super.onStart()
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), requestAudio)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == requestAudio) {
            if (resultCode != PackageManager.PERMISSION_GRANTED)
                AlertDialog.Builder(this)
                    .setTitle("No.")
                    .setMessage("Permissions Required.")
                    .setCancelable(false)
                    .setPositiveButton("OK") { _, _ -> TODO() }
                    .show()
        }
    }

    private fun startAudioRecording() {
        val speachIntent = RecognizerIntent.getVoiceDetailsIntent(this)
        //set speech language to something else than Local.default()
        speachIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "nl")
        speechRecognizer.startListening(speachIntent)
    }


    override fun onStop() {
        super.onStop()
        speechRecognizer.destroy()
    }


    // UI
    private fun startRecordingButtonPressed() {
        startAudioRecording()
        recordButton.visibility = View.INVISIBLE
        recordCaption.visibility = View.INVISIBLE
        recordingProgress.visibility = View.VISIBLE
        recordingProgressHint.visibility = View.VISIBLE
        recordingProgressHint.text = "Say '${sharedPreferences.getString("codeword", "kiwi")!!.toLowerCase()}'"
    }

    private fun displayResult(matches: Boolean) {
        resultIcon.setImageResource(if (matches) R.drawable.ic_success else R.drawable.ic_failure)
        Toast.makeText(this, if (matches) "Codeword detected!" else "Codeword not detected", Toast.LENGTH_LONG).show()

        resultIcon.visibility = View.VISIBLE
        recordingProgress.visibility = View.GONE
        recordingProgressHint.visibility = View.GONE

        Handler().postDelayed({
            resultIcon.visibility = View.GONE
            recordButton.visibility = View.VISIBLE
            recordCaption.visibility = View.VISIBLE
        }, 2500)
    }
}

class CodeWordListener(val sharedPreferences: SharedPreferences, val onResult: (match: Boolean) -> Unit) : RecognitionListener {
    override fun onReadyForSpeech(p0: Bundle?) {}
    override fun onRmsChanged(p0: Float) {}
    override fun onBufferReceived(p0: ByteArray?) {}
    override fun onPartialResults(p0: Bundle?) {}
    override fun onEvent(p0: Int, p1: Bundle?) {}
    override fun onBeginningOfSpeech() {}
    override fun onEndOfSpeech() {}
    override fun onError(p0: Int) {
        onResult(false)
    }

    override fun onResults(p0: Bundle?) {
        val results = (p0?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) ?: arrayListOf()) as ArrayList<String?>
        val codeword = sharedPreferences.getString("codeword", "kiwi")!!.toLowerCase()

        onResult(results.any { it?.toLowerCase()?.contains(codeword) ?: false })
    }
}