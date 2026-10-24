package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Build
import android.util.Log
import com.example.R

class GameAudioManager(private val context: Context) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager

    private var soundPool: SoundPool? = null
    private val soundMap = mutableMapOf<SoundEffect, Int>()

    private var currentMediaPlayer: MediaPlayer? = null
    private var currentMusicResId: Int? = null

    var sfxVolume: Float = 0.8f
        set(value) {
            field = value.coerceIn(0f, 1f)
        }

    var musicVolume: Float = 0.6f
        set(value) {
            field = value.coerceIn(0f, 1f)
            updateMusicVolume()
        }

    var sfxEnabled: Boolean = true
    var musicEnabled: Boolean = true
        set(value) {
            field = value
            if (!value) {
                stopMusic()
            } else {
                currentMusicResId?.let { playMusic(it) }
            }
        }

    private var isDucked = false
    private var isAppPaused = false

    enum class SoundEffect {
        STARTUP,
        BTN_TAP,
        PIECE_SELECT,
        BLOCK_PLACE,
        INVALID_PLACE,
        CLEAR_SINGLE,
        CLEAR_DOUBLE,
        CLEAR_MULTI,
        COMBO_UP,
        COMBO_FURY,
        COIN_REWARD,
        HIGH_SCORE,
        PAUSE,
        RESUME,
        GAME_OVER,
        CHALLENGE_COMPLETE
    }

    init {
        initSoundPool()
    }

    private fun initSoundPool() {
        try {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            soundPool = SoundPool.Builder()
                .setMaxStreams(10)
                .setAudioAttributes(audioAttributes)
                .build()

            loadSounds()
        } catch (e: Exception) {
            Log.e("GameAudioManager", "Error initializing SoundPool", e)
        }
    }

    private fun loadSounds() {
        soundPool?.let { pool ->
            soundMap[SoundEffect.STARTUP] = pool.load(context, R.raw.sfx_startup, 1)
            soundMap[SoundEffect.BTN_TAP] = pool.load(context, R.raw.sfx_btn_tap, 1)
            soundMap[SoundEffect.PIECE_SELECT] = pool.load(context, R.raw.sfx_piece_select, 1)
            soundMap[SoundEffect.BLOCK_PLACE] = pool.load(context, R.raw.sfx_block_place, 1)
            soundMap[SoundEffect.INVALID_PLACE] = pool.load(context, R.raw.sfx_invalid_place, 1)
            soundMap[SoundEffect.CLEAR_SINGLE] = pool.load(context, R.raw.sfx_clear_single, 1)
            soundMap[SoundEffect.CLEAR_DOUBLE] = pool.load(context, R.raw.sfx_clear_double, 1)
            soundMap[SoundEffect.CLEAR_MULTI] = pool.load(context, R.raw.sfx_clear_multi, 1)
            soundMap[SoundEffect.COMBO_UP] = pool.load(context, R.raw.sfx_combo_up, 1)
            soundMap[SoundEffect.COMBO_FURY] = pool.load(context, R.raw.sfx_combo_fury, 1)
            soundMap[SoundEffect.COIN_REWARD] = pool.load(context, R.raw.sfx_coin_reward, 1)
            soundMap[SoundEffect.HIGH_SCORE] = pool.load(context, R.raw.sfx_high_score, 1)
            soundMap[SoundEffect.PAUSE] = pool.load(context, R.raw.sfx_pause, 1)
            soundMap[SoundEffect.RESUME] = pool.load(context, R.raw.sfx_resume, 1)
            soundMap[SoundEffect.GAME_OVER] = pool.load(context, R.raw.sfx_game_over, 1)
            soundMap[SoundEffect.CHALLENGE_COMPLETE] = pool.load(context, R.raw.sfx_challenge_complete, 1)
        }
    }

    fun playSound(effect: SoundEffect, rate: Float = 1.0f) {
        if (!sfxEnabled || isAppPaused) return
        val soundId = soundMap[effect] ?: return
        val vol = (sfxVolume * if (isDucked) 0.3f else 1.0f).coerceIn(0f, 1f)
        soundPool?.play(soundId, vol, vol, 1, 0, rate)
    }

    fun playMenuMusic() {
        playMusic(R.raw.bgm_menu)
    }

    fun playGameplayMusic(isFury: Boolean = false) {
        val targetRes = if (isFury) R.raw.bgm_fury else R.raw.bgm_gameplay
        playMusic(targetRes)
    }

    fun playMusic(resId: Int) {
        currentMusicResId = resId
        if (!musicEnabled || isAppPaused) return

        if (currentMediaPlayer != null && currentMusicResId == resId && currentMediaPlayer?.isPlaying == true) {
            return
        }

        try {
            currentMediaPlayer?.apply {
                if (isPlaying) stop()
                release()
            }
            currentMediaPlayer = null

            val mp = MediaPlayer.create(context, resId)
            mp?.isLooping = true
            currentMediaPlayer = mp
            updateMusicVolume()
            mp?.start()
        } catch (e: Exception) {
            Log.e("GameAudioManager", "Error playing music", e)
        }
    }

    fun stopMusic() {
        try {
            currentMediaPlayer?.apply {
                if (isPlaying) stop()
                release()
            }
            currentMediaPlayer = null
        } catch (e: Exception) {
            Log.e("GameAudioManager", "Error stopping music", e)
        }
    }

    private fun updateMusicVolume() {
        val vol = if (musicEnabled && !isAppPaused) {
            (musicVolume * if (isDucked) 0.3f else 1.0f).coerceIn(0f, 1f)
        } else {
            0f
        }
        currentMediaPlayer?.setVolume(vol, vol)
    }

    fun onPause() {
        isAppPaused = true
        try {
            if (currentMediaPlayer?.isPlaying == true) {
                currentMediaPlayer?.pause()
            }
            soundPool?.autoPause()
        } catch (e: Exception) {
            Log.e("GameAudioManager", "Error in onPause", e)
        }
    }

    fun onResume() {
        isAppPaused = false
        try {
            soundPool?.autoResume()
            if (musicEnabled && currentMediaPlayer != null) {
                updateMusicVolume()
                currentMediaPlayer?.start()
            } else if (musicEnabled && currentMusicResId != null) {
                playMusic(currentMusicResId!!)
            }
        } catch (e: Exception) {
            Log.e("GameAudioManager", "Error in onResume", e)
        }
    }

    fun release() {
        try {
            currentMediaPlayer?.apply {
                if (isPlaying) stop()
                release()
            }
            currentMediaPlayer = null
            soundPool?.release()
            soundPool = null
        } catch (e: Exception) {
            Log.e("GameAudioManager", "Error in release", e)
        }
    }
}
