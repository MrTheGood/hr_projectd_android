package nl.hr.projectrage

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import nl.hr.projectrage.MainActivity.AudioConfig.audioFormat
import nl.hr.projectrage.MainActivity.AudioConfig.channelConfig
import nl.hr.projectrage.MainActivity.AudioConfig.minBufferSize
import nl.hr.projectrage.MainActivity.AudioConfig.sampleRateInHz
import java.util.*
import kotlin.concurrent.timerTask
import kotlin.math.abs


class MainActivity : AppCompatActivity() {
    object AudioConfig {
        const val sampleRateInHz = 8000
        const val channelConfig = AudioFormat.CHANNEL_IN_MONO
        const val audioFormat = AudioFormat.ENCODING_PCM_16BIT
        val minBufferSize = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat)
    }

    companion object {
        const val requestAudio = 1
    }


    private val audioRecord by lazy { AudioRecord(MediaRecorder.AudioSource.MIC, sampleRateInHz, channelConfig, audioFormat, minBufferSize) }
    private val timer by lazy { Timer() }
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        root.removeAllViews()
        for (i in 0..minBufferSize) {
            LayoutInflater.from(this).inflate(R.layout.thing, root, true)
        }
    }

    override fun onStart() {
        super.onStart()
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), requestAudio)
        } else startAudioRecording()
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

            startAudioRecording()
        }
    }

    private fun startAudioRecording() {
        audioRecord.startRecording()

        timer.scheduleAtFixedRate(timerTask {
            val buffer = ShortArray(minBufferSize)
            audioRecord.read(buffer, 0, minBufferSize)
            handler.post {
                var i = 0
                buffer.forEach {
                    root.getChildAt(i++).apply { layoutParams = layoutParams.apply { height = abs(it.toInt()) } }
                }
            }
        }, 0, 50)
    }


    override fun onStop() {
        super.onStop()

        audioRecord.stop()
        timer.cancel()
    }
}