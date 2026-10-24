package com.example.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.GameAudioManager
import com.example.data.BoardTheme
import com.example.data.GamePreferences
import com.example.data.ThemePresets
import com.example.model.CubeColor
import com.example.model.GameMode
import com.example.util.SmartLinkHelper

@Composable
fun MainMenuScreen(
    preferences: GamePreferences,
    audioManager: GameAudioManager,
    onStartGame: (GameMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val highScoreClassic by preferences.highScoreClassic.collectAsState()
    val highScoreTimeAttack by preferences.highScoreTimeAttack.collectAsState()
    val coins by preferences.coins.collectAsState()
    val selectedThemeId by preferences.selectedThemeId.collectAsState()
    val unlockedThemeIds by preferences.unlockedThemeIds.collectAsState()
    val unlockedAchievements by preferences.unlockedAchievements.collectAsState()
    val sfxEnabled by preferences.sfxEnabled.collectAsState()
    val musicEnabled by preferences.musicEnabled.collectAsState()
    val sfxVolume by preferences.sfxVolume.collectAsState()
    val musicVolume by preferences.musicVolume.collectAsState()
    val hapticsEnabled by preferences.hapticsEnabled.collectAsState()
    val reducedMotion by preferences.reducedMotion.collectAsState()

    val currentTheme = ThemePresets.getById(selectedThemeId)
    val dailyCompleted = preferences.isDailyCompletedToday()

    var showModeSelect by remember { mutableStateOf(false) }
    var showThemes by remember { mutableStateOf(false) }
    var showAchievements by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var showHowToPlay by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        audioManager.playMenuMusic()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "btnPulse")
    val btnScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(750, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "btnScale"
    )

    AtmosphericBackground(theme = currentTheme, reducedMotion = reducedMotion) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 14.dp)
                .testTag("main_menu_screen"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 520.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Bar: Coins Pill & Quick Audio Toggles
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Coins pill
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(Color(0xFF0F172A).copy(alpha = 0.85f), RoundedCornerShape(20.dp))
                            .border(1.2.dp, Color(0xFFFBBF24).copy(alpha = 0.65f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                            .clickable { showThemes = true }
                            .testTag("menu_coins_pill")
                    ) {
                        Text("🪙", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$coins",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFACC15)
                        )
                    }

                    // Audio quick toggles
                    Row {
                        IconButton(
                            onClick = {
                                val next = !sfxEnabled
                                preferences.setSfxEnabled(next)
                                audioManager.sfxEnabled = next
                                if (next) audioManager.playSound(GameAudioManager.SoundEffect.BTN_TAP)
                            },
                            modifier = Modifier
                                .size(42.dp)
                                .background(if (sfxEnabled) Color(0xFF1E3A8A) else Color(0xFF1E293B), CircleShape)
                                .border(1.dp, Color(0xFF334155), CircleShape)
                                .testTag("quick_sfx_toggle")
                        ) {
                            Icon(
                                imageVector = if (sfxEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                                contentDescription = "Toggle SFX",
                                tint = if (sfxEnabled) Color(0xFF38BDF8) else Color(0xFF94A3B8),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = {
                                val next = !musicEnabled
                                preferences.setMusicEnabled(next)
                                audioManager.musicEnabled = next
                            },
                            modifier = Modifier
                                .size(42.dp)
                                .background(if (musicEnabled) Color(0xFF1E3A8A) else Color(0xFF1E293B), CircleShape)
                                .border(1.dp, Color(0xFF334155), CircleShape)
                                .testTag("quick_music_toggle")
                        ) {
                            Icon(
                                imageVector = if (musicEnabled) Icons.Default.MusicNote else Icons.Default.MusicOff,
                                contentDescription = "Toggle Music",
                                tint = if (musicEnabled) Color(0xFF38BDF8) else Color(0xFF94A3B8),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Center: Animated Emblem & Logo & High Scores
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    // 3D Cubes Emblem
                    Canvas(modifier = Modifier.size(105.dp)) {
                        val pad = 4.dp.toPx()
                        val cubeSize = (size.minDimension - pad * 3) / 2f
                        val cr = CornerRadius(cubeSize * 0.22f, cubeSize * 0.22f)

                        drawGlossyCube(CubeColor.RED, Offset(pad, pad), Size(cubeSize, cubeSize), cr)
                        drawGlossyCube(CubeColor.YELLOW, Offset(pad * 2 + cubeSize, pad), Size(cubeSize, cubeSize), cr)
                        drawGlossyCube(CubeColor.GREEN, Offset(pad, pad * 2 + cubeSize), Size(cubeSize, cubeSize), cr)
                        drawGlossyCube(CubeColor.BLUE, Offset(pad * 2 + cubeSize, pad * 2 + cubeSize), Size(cubeSize, cubeSize), cr)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "BLOCK FURY",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 2.5.sp
                    )
                    Text(
                        text = "CUBE MASTER",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF38BDF8),
                        letterSpacing = 6.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "PLACE. SMASH. DOMINATE.",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFACC15),
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // High Score Card
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.85f)),
                        border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFF3B82F6).copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth(0.92f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("CLASSIC BEST", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
                                Text("$highScoreClassic", fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color(0xFF38BDF8))
                            }
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(30.dp)
                                    .background(Color(0xFF334155))
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("TIME ATTACK", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
                                Text("$highScoreTimeAttack", fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color(0xFFF97316))
                            }
                        }
                    }
                }

                // Bottom: Primary PLAY Button, Secondary Special Offers, and Utility Grid
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    // Primary Action: PLAY NOW
                    Button(
                        onClick = {
                            audioManager.playSound(GameAudioManager.SoundEffect.BTN_TAP)
                            showModeSelect = true
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(60.dp)
                            .scale(if (reducedMotion) 1f else btnScale)
                            .testTag("menu_btn_play"),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(30.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "PLAY NOW",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Secondary Action: 🎁 SPECIAL OFFERS Button (Adsterra SmartLink Integration)
                    OutlinedButton(
                        onClick = {
                            audioManager.playSound(GameAudioManager.SoundEffect.BTN_TAP)
                            SmartLinkHelper.openSmartLink(context)
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.82f)
                            .height(44.dp)
                            .semantics {
                                contentDescription = "Open special external offers"
                            }
                            .testTag("btn_special_offers"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color(0xFF0F172A).copy(alpha = 0.75f),
                            contentColor = Color(0xFFFBBF24)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.2.dp,
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFFEC4899),
                                    Color(0xFF8B5CF6),
                                    Color(0xFF38BDF8)
                                )
                            )
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.CardGiftcard,
                            contentDescription = null,
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "🎁 SPECIAL OFFERS",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFFBBF24),
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action Icons Row: Themes, Trophies, Rules, Settings
                    Row(
                        modifier = Modifier.fillMaxWidth(0.92f),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MenuIconButton(
                            icon = Icons.Default.Palette,
                            label = "Themes",
                            testTag = "menu_btn_themes",
                            onClick = {
                                audioManager.playSound(GameAudioManager.SoundEffect.BTN_TAP)
                                showThemes = true
                            }
                        )
                        MenuIconButton(
                            icon = Icons.Default.EmojiEvents,
                            label = "Trophies",
                            testTag = "menu_btn_achievements",
                            onClick = {
                                audioManager.playSound(GameAudioManager.SoundEffect.BTN_TAP)
                                showAchievements = true
                            }
                        )
                        MenuIconButton(
                            icon = Icons.Default.Help,
                            label = "Rules",
                            testTag = "menu_btn_how_to_play",
                            onClick = {
                                audioManager.playSound(GameAudioManager.SoundEffect.BTN_TAP)
                                showHowToPlay = true
                            }
                        )
                        MenuIconButton(
                            icon = Icons.Default.Settings,
                            label = "Settings",
                            testTag = "menu_btn_settings",
                            onClick = {
                                audioManager.playSound(GameAudioManager.SoundEffect.BTN_TAP)
                                showSettings = true
                            }
                        )
                    }
                }
            }
        }
    }

    // Dialogs
    if (showModeSelect) {
        ModeSelectDialog(
            onDismiss = { showModeSelect = false },
            dailyCompleted = dailyCompleted,
            onSelectMode = { mode ->
                showModeSelect = false
                audioManager.playSound(GameAudioManager.SoundEffect.BTN_TAP)
                onStartGame(mode)
            }
        )
    }

    if (showThemes) {
        ThemesDialog(
            coins = coins,
            currentThemeId = selectedThemeId,
            unlockedThemeIds = unlockedThemeIds,
            onSelectTheme = { id ->
                preferences.selectTheme(id)
                audioManager.playSound(GameAudioManager.SoundEffect.BTN_TAP)
            },
            onUnlockTheme = { theme ->
                if (preferences.spendCoins(theme.cost)) {
                    preferences.unlockTheme(theme.id)
                    preferences.selectTheme(theme.id)
                    audioManager.playSound(GameAudioManager.SoundEffect.COIN_REWARD)
                }
            },
            onDismiss = { showThemes = false }
        )
    }

    if (showAchievements) {
        AchievementsDialog(
            unlockedIds = unlockedAchievements,
            onDismiss = { showAchievements = false }
        )
    }

    if (showHowToPlay) {
        HowToPlayDialog(onDismiss = { showHowToPlay = false })
    }

    if (showSettings) {
        SettingsDialog(
            onDismiss = { showSettings = false },
            sfxEnabled = sfxEnabled,
            musicEnabled = musicEnabled,
            sfxVolume = sfxVolume,
            musicVolume = musicVolume,
            hapticsEnabled = hapticsEnabled,
            reducedMotion = reducedMotion,
            onToggleSfx = { enabled ->
                preferences.setSfxEnabled(enabled)
                audioManager.sfxEnabled = enabled
                if (enabled) audioManager.playSound(GameAudioManager.SoundEffect.BTN_TAP)
            },
            onToggleMusic = { enabled ->
                preferences.setMusicEnabled(enabled)
                audioManager.musicEnabled = enabled
            },
            onChangeSfxVol = { vol ->
                preferences.setSfxVolume(vol)
                audioManager.sfxVolume = vol
            },
            onChangeMusicVol = { vol ->
                preferences.setMusicVolume(vol)
                audioManager.musicVolume = vol
            },
            onToggleHaptics = { preferences.setHapticsEnabled(it) },
            onToggleReducedMotion = { preferences.setReducedMotion(it) },
            onResetProgress = {
                preferences.resetAllProgress()
                audioManager.playSound(GameAudioManager.SoundEffect.BTN_TAP)
            }
        )
    }
}

@Composable
fun MenuIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    testTag: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .background(Color(0xFF0F172A).copy(alpha = 0.85f), RoundedCornerShape(14.dp))
                .border(1.2.dp, Color(0xFF334155), RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color(0xFF38BDF8),
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, fontSize = 11.sp, color = Color(0xFFCBD5E1), fontWeight = FontWeight.SemiBold)
    }
}

