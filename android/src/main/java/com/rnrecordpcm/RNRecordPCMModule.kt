package com.rnrecordpcm

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import com.facebook.react.bridge.Callback
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.modules.core.DeviceEventManagerModule

@ReactModule(name = RNRecordPCMModule.NAME)
class RNRecordPCMModule(reactContext: ReactApplicationContext) : NativeRNRecordPCMSpec(reactContext) {
	private var audioRecord: AudioRecord? = null
    private val reactContext: ReactApplicationContext = reactContext
    private var eventEmitter: DeviceEventManagerModule.RCTDeviceEventEmitter? = null
    private var running = false
    private var bufferSize = 0
    private var recordingThread: Thread? = null

	override fun getName(): String {
		return NAME
	}

	@ReactMethod
    override fun init(options: ReadableMap) {
        eventEmitter = reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)

        if (running || recordingThread?.isAlive == true) {
            return
        }

        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null

        var sampleRateInHz = 44100
        if (options.hasKey("sampleRate")) {
            sampleRateInHz = options.getInt("sampleRate")
        }

        var channelConfig = AudioFormat.CHANNEL_IN_MONO
        if (options.hasKey("channelsPerFrame")) {
            val channelsPerFrame = options.getInt("channelsPerFrame")
            if (channelsPerFrame == 2) {
                channelConfig = AudioFormat.CHANNEL_IN_STEREO
            }
        }

        var audioFormat = AudioFormat.ENCODING_PCM_16BIT
        if (options.hasKey("bitsPerChannel")) {
            val bitsPerChannel = options.getInt("bitsPerChannel")
            if (bitsPerChannel == 8) {
                audioFormat = AudioFormat.ENCODING_PCM_8BIT
            }
        }

        bufferSize = if (options.hasKey("bufferSize")) {
            options.getInt("bufferSize")
        } else {
            8192
        }

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRateInHz,
            channelConfig,
            audioFormat,
            bufferSize * 2
        )

        recordingThread = Thread { recording() }.apply { name = "RecordingThread" }
    }

	@ReactMethod
    override fun start() {
        if (!running && audioRecord != null && recordingThread != null) {
            running = true
            audioRecord!!.startRecording()
            recordingThread!!.start()
        }
    }

	@ReactMethod
    override fun stop() {
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
        running = false
    }

	private fun recording() {
        val buffer = ShortArray(bufferSize)
        try {
            while (running) {
                val data = Arguments.createArray()
                val readSize = audioRecord?.read(buffer, 0, bufferSize) ?: break
                if (readSize > 0) {
                    for (i in 0 until readSize) {
                        data.pushInt(buffer[i].toInt())
                    }
                    eventEmitter?.emit("recording", data)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            stop()
        }
    }

    @ReactMethod
    override fun addRecordingEventListener(listener: Callback) {
        // No-op or add custom behavior if needed
        // The listener is unused because DeviceEventEmitter handles event propagation
    }

	companion object {
		const val NAME = "RNRecordPCM"
	}
}