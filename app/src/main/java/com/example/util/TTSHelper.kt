package com.example.util

import android.content.Context
import android.speech.tts.TextToSpeech
import com.example.model.Language
import java.util.Locale

class TTSHelper(context: Context) {
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isInitialized = true
                tts?.language = Locale.US
            }
        }
    }

    fun speak(text: String, language: Language = Language.EN) {
        if (!isInitialized || tts == null) return

        val locale = when (language) {
            Language.ES -> Locale("es", "ES")
            Language.FR -> Locale.FRENCH
            Language.DE -> Locale.GERMAN
            Language.HI -> Locale("hi", "IN")
            Language.TA -> Locale("ta", "IN")
            Language.EN -> Locale.US
        }

        try {
            tts?.language = locale
        } catch (_: Exception) {
            tts?.language = Locale.US
        }

        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "math_tts_${System.currentTimeMillis()}")
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (_: Exception) {}
        tts = null
    }
}
