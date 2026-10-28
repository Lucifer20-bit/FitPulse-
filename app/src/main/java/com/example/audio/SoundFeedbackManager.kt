package com.example.audio

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SoundFeedbackManager(context: Context) {
    private val appContext = context.applicationContext

    private var toneGenerator: ToneGenerator? = try {
        ToneGenerator(AudioManager.STREAM_MUSIC, 85)
    } catch (e: Exception) {
        null
    }

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = appContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        appContext.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    var isSoundEnabled: Boolean = true
    var isHapticsEnabled: Boolean = true

    /**
     * Plays a crisp warning tick when rest timer reaches 3s, 2s, 1s
     */
    fun playCountdownTick() {
        if (isSoundEnabled) {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 120)
            } catch (_: Exception) {}
        }
        if (isHapticsEnabled) {
            vibrate(50)
        }
    }

    /**
     * Plays a triumphant fanfare chime when rest timer finishes (0s)
     */
    fun playRestCompleteChime() {
        if (isSoundEnabled) {
            CoroutineScope(Dispatchers.Default).launch {
                try {
                    toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 400)
                    delay(300)
                    toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 350)
                } catch (_: Exception) {}
            }
        }
        if (isHapticsEnabled) {
            vibratePattern(longArrayOf(0, 150, 100, 250))
        }
    }

    /**
     * Plays celebratory victory fanfare when a Personal Record (PR) is broken!
     */
    fun playPrCelebration() {
        if (isSoundEnabled) {
            CoroutineScope(Dispatchers.Default).launch {
                try {
                    toneGenerator?.startTone(ToneGenerator.TONE_PROP_PROMPT, 150)
                    delay(150)
                    toneGenerator?.startTone(ToneGenerator.TONE_PROP_PROMPT, 150)
                    delay(150)
                    toneGenerator?.startTone(ToneGenerator.TONE_CDMA_HIGH_L, 400)
                } catch (_: Exception) {}
            }
        }
        if (isHapticsEnabled) {
            vibratePattern(longArrayOf(0, 100, 80, 100, 80, 300))
        }
    }

    /**
     * Fast crisp feedback when a set is completed or checked off
     */
    fun playSetCompleteTap() {
        if (isSoundEnabled) {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 80)
            } catch (_: Exception) {}
        }
        if (isHapticsEnabled) {
            vibrate(40)
        }
    }

    private fun vibrate(durationMs: Long) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (_: Exception) {}
    }

    private fun vibratePattern(pattern: LongArray) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createWaveform(pattern, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(pattern, -1)
            }
        } catch (_: Exception) {}
    }

    fun release() {
        try {
            toneGenerator?.release()
            toneGenerator = null
        } catch (_: Exception) {}
    }
}
