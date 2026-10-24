package com.example.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.AchievementList
import com.example.data.BoardTheme
import com.example.data.ThemePresets
import com.example.model.GameMode

@Composable
fun PauseDialog(
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onHome: () -> Unit,
    sfxEnabled: Boolean,
    musicEnabled: Boolean,
    onToggleSfx: (Boolean) -> Unit,
    onToggleMusic: (Boolean) -> Unit,
    onOpenSpecialOffers: (() -> Unit)? = null
) {
    Dialog(onDismissRequest = onResume) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.96f)),
            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF3B82F6)),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(12.dp)
                .testTag("pause_dialog")
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "PAUSED",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Sound & Music quick toggles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(
                        onClick = { onToggleSfx(!sfxEnabled) },
                        modifier = Modifier
                            .size(52.dp)
                            .background(if (sfxEnabled) Color(0xFF1E3A8A) else Color(0xFF1E293B), CircleShape)
                            .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f), CircleShape)
                            .testTag("pause_toggle_sfx")
                    ) {
                        Icon(
                            imageVector = if (sfxEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                            contentDescription = "Toggle SFX",
                            tint = if (sfxEnabled) Color(0xFF38BDF8) else Color(0xFF94A3B8)
                        )
                    }

                    IconButton(
                        onClick = { onToggleMusic(!musicEnabled) },
                        modifier = Modifier
                            .size(52.dp)
                            .background(if (musicEnabled) Color(0xFF1E3A8A) else Color(0xFF1E293B), CircleShape)
                            .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f), CircleShape)
                            .testTag("pause_toggle_music")
                    ) {
                        Icon(
                            imageVector = if (musicEnabled) Icons.Default.MusicNote else Icons.Default.MusicOff,
                            contentDescription = "Toggle Music",
                            tint = if (musicEnabled) Color(0xFF38BDF8) else Color(0xFF94A3B8)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onResume,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("pause_btn_resume"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("RESUME", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onRestart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("pause_btn_restart"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("RESTART", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onHome,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("pause_btn_home"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF64748B))
                ) {
                    Text("MAIN MENU", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }

                // Optional secondary placement: Special Offers
                if (onOpenSpecialOffers != null) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0xFF334155))
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .clickable(onClick = onOpenSpecialOffers)
                            .semantics { contentDescription = "Open special external offers" }
                            .padding(vertical = 4.dp, horizontal = 8.dp)
                            .testTag("pause_special_offers"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CardGiftcard,
                            contentDescription = null,
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Special Offers",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFBBF24)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GameOverDialog(
    score: Int,
    bestScore: Int,
    isNewRecord: Boolean,
    linesCleared: Int,
    highestCombo: Int,
    blocksPlaced: Int,
    coinsEarned: Int,
    onPlayAgain: () -> Unit,
    onHome: () -> Unit
) {
    // Count-up animation for the final score
    var targetScore by remember { mutableStateOf(0) }
    LaunchedEffect(score) {
        targetScore = score
    }
    val animatedScore by animateIntAsState(
        targetValue = targetScore,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "score_count_up"
    )

    Dialog(onDismissRequest = {}) {
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.98f)),
            border = androidx.compose.foundation.BorderStroke(2.5.dp, if (isNewRecord) Color(0xFFFACC15) else Color(0xFFEF4444)),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(12.dp)
                .testTag("game_over_dialog")
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isNewRecord) {
                    Box(
                        modifier = Modifier
                            .background(
                                Brush.horizontalGradient(listOf(Color(0xFFF59E0B), Color(0xFFFACC15))),
                                RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "★ NEW RECORD! ★",
                            color = Color.Black,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            letterSpacing = 1.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Text(
                    text = "GAME OVER",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Count-up score display
                Text(
                    text = "$animatedScore",
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF38BDF8)
                )
                Text(
                    text = "FINAL SCORE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Detailed stats card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        StatRow(label = "Best Score", value = "$bestScore", color = Color(0xFFFACC15))
                        StatRow(label = "Lines Cleared", value = "$linesCleared", color = Color(0xFF34D399))
                        StatRow(label = "Highest Combo", value = "x$highestCombo", color = Color(0xFFF472B6))
                        StatRow(label = "Blocks Placed", value = "$blocksPlaced", color = Color(0xFF60A5FA))
                        StatRow(label = "Coins Earned", value = "+$coinsEarned 🪙", color = Color(0xFFFBBF24))
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onPlayAgain,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("game_over_btn_play_again"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("PLAY AGAIN", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onHome,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("game_over_btn_home"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF64748B))
                ) {
                    Text("MAIN MENU", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun StatRow(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 13.sp, color = Color(0xFFCBD5E1))
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun ModeSelectDialog(
    onDismiss: () -> Unit,
    onSelectMode: (GameMode) -> Unit,
    dailyCompleted: Boolean
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF3B82F6)),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(12.dp)
                .testTag("mode_select_dialog")
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SELECT MODE",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Classic Mode
                ModeCard(
                    title = "CLASSIC MODE",
                    tagline = "Endless puzzle experience with no timer",
                    iconColor = Color(0xFF38BDF8),
                    accentColor = Color(0xFF1E3A8A),
                    onClick = { onSelectMode(GameMode.CLASSIC) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Time Attack
                ModeCard(
                    title = "TIME ATTACK (120s)",
                    tagline = "Fast & furious! Clear as many lines in 2 minutes",
                    iconColor = Color(0xFFF97316),
                    accentColor = Color(0xFF7C2D12),
                    onClick = { onSelectMode(GameMode.TIME_ATTACK) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Daily Challenge
                ModeCard(
                    title = "DAILY CHALLENGE",
                    tagline = if (dailyCompleted) "Completed for today! (Reward claimed)" else "Clear 15 lines today for 50 Gold Coins!",
                    iconColor = Color(0xFFFACC15),
                    accentColor = Color(0xFF713F12),
                    isBadge = dailyCompleted,
                    badgeText = "COMPLETED",
                    onClick = { onSelectMode(GameMode.DAILY_CHALLENGE) }
                )
            }
        }
    }
}

@Composable
fun ModeCard(
    title: String,
    tagline: String,
    iconColor: Color,
    accentColor: Color,
    isBadge: Boolean = false,
    badgeText: String = "",
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, iconColor.copy(alpha = 0.6f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("mode_card_${title.take(7).lowercase()}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = iconColor
                )
                if (isBadge) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF10B981), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badgeText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = tagline,
                fontSize = 12.sp,
                color = Color(0xFF94A3B8)
            )
        }
    }
}

@Composable
fun SettingsDialog(
    onDismiss: () -> Unit,
    sfxEnabled: Boolean,
    musicEnabled: Boolean,
    sfxVolume: Float,
    musicVolume: Float,
    hapticsEnabled: Boolean,
    reducedMotion: Boolean,
    onToggleSfx: (Boolean) -> Unit,
    onToggleMusic: (Boolean) -> Unit,
    onChangeSfxVol: (Float) -> Unit,
    onChangeMusicVol: (Float) -> Unit,
    onToggleHaptics: (Boolean) -> Unit,
    onToggleReducedMotion: (Boolean) -> Unit,
    onResetProgress: () -> Unit
) {
    var showConfirmReset by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF3B82F6)),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(12.dp)
                .testTag("settings_dialog")
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SETTINGS",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sound Effects
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Sound Effects", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    Switch(
                        checked = sfxEnabled,
                        onCheckedChange = onToggleSfx,
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF38BDF8))
                    )
                }
                Slider(
                    value = sfxVolume,
                    onValueChange = onChangeSfxVol,
                    enabled = sfxEnabled,
                    colors = SliderDefaults.colors(thumbColor = Color(0xFF38BDF8), activeTrackColor = Color(0xFF38BDF8))
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Background Music
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Background Music", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    Switch(
                        checked = musicEnabled,
                        onCheckedChange = onToggleMusic,
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF38BDF8))
                    )
                }
                Slider(
                    value = musicVolume,
                    onValueChange = onChangeMusicVol,
                    enabled = musicEnabled,
                    colors = SliderDefaults.colors(thumbColor = Color(0xFF38BDF8), activeTrackColor = Color(0xFF38BDF8))
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Haptics
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Haptic Feedback", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    Switch(
                        checked = hapticsEnabled,
                        onCheckedChange = onToggleHaptics,
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF38BDF8))
                    )
                }

                // Reduced Motion
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Reduced Motion", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    Switch(
                        checked = reducedMotion,
                        onCheckedChange = onToggleReducedMotion,
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF38BDF8))
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Reset progress button
                OutlinedButton(
                    onClick = { showConfirmReset = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("settings_btn_reset"),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFEF4444)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEF4444))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reset Local Progress", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showConfirmReset) {
        AlertDialog(
            onDismissRequest = { showConfirmReset = false },
            title = { Text("Reset All Progress?", fontWeight = FontWeight.Bold) },
            text = { Text("This will clear all your high scores, coins, unlocked themes, and achievements. This cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        onResetProgress()
                        showConfirmReset = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Reset Everything")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showConfirmReset = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ThemesDialog(
    coins: Int,
    currentThemeId: String,
    unlockedThemeIds: Set<String>,
    onSelectTheme: (String) -> Unit,
    onUnlockTheme: (BoardTheme) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF8B5CF6)),
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .padding(8.dp)
                .testTag("themes_dialog")
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "THEMES & STYLES",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color(0xFF1E293B), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("🪙 $coins Coins", fontWeight = FontWeight.Bold, color = Color(0xFFFACC15), fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(modifier = Modifier.height(340.dp)) {
                    items(ThemePresets.ALL) { theme ->
                        val isUnlocked = unlockedThemeIds.contains(theme.id)
                        val isEquipped = currentThemeId == theme.id

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (isEquipped) 2.5.dp else 1.dp,
                                color = if (isEquipped) Color(0xFF34D399) else Color(0xFF334155)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = theme.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row {
                                        theme.previewColors.forEach { c ->
                                            Box(
                                                modifier = Modifier
                                                    .size(16.dp)
                                                    .padding(end = 4.dp)
                                                    .background(c, RoundedCornerShape(4.dp))
                                            )
                                        }
                                    }
                                }

                                if (isEquipped) {
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFF065F46), RoundedCornerShape(8.dp))
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text("EQUIPPED", color = Color(0xFF34D399), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                } else if (isUnlocked) {
                                    Button(
                                        onClick = { onSelectTheme(theme.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("EQUIP", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                } else {
                                    Button(
                                        onClick = { onUnlockTheme(theme) },
                                        enabled = coins >= theme.cost,
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("${theme.cost} 🪙", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AchievementsDialog(
    unlockedIds: Set<String>,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFF59E0B)),
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .padding(8.dp)
                .testTag("achievements_dialog")
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ACHIEVEMENTS",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Text(
                    text = "${unlockedIds.size} of ${AchievementList.ALL.size} Unlocked",
                    fontSize = 13.sp,
                    color = Color(0xFF94A3B8)
                )

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(modifier = Modifier.height(360.dp)) {
                    items(AchievementList.ALL) { ach ->
                        val unlocked = unlockedIds.contains(ach.id)

                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (unlocked) Color(0xFF1E293B) else Color(0xFF131A29)
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                width = 1.dp,
                                color = if (unlocked) Color(0xFFF59E0B) else Color(0xFF334155)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .background(
                                            if (unlocked) Color(0xFF78350F) else Color(0xFF1E293B),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (unlocked) Icons.Default.Check else Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = if (unlocked) Color(0xFFFACC15) else Color(0xFF64748B),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = ach.title,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (unlocked) Color.White else Color(0xFF94A3B8)
                                    )
                                    Text(
                                        text = ach.description,
                                        fontSize = 12.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }

                                Text(
                                    text = "+${ach.coinReward} 🪙",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFBBF24)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HowToPlayDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF38BDF8)),
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .padding(12.dp)
                .testTag("how_to_play_dialog")
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "HOW TO PLAY",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                RuleItem(number = "1", title = "Drag & Place Pieces", desc = "Drag shapes from the bottom tray onto the 8x8 grid. You can also tap a piece and tap a cell to drop it.")
                RuleItem(number = "2", title = "Clear Lines", desc = "Completely fill horizontal rows or vertical columns to clear them and score big points.")
                RuleItem(number = "3", title = "Smash Multiple Lines", desc = "Clearing 2 or more rows/columns at the same time awards huge combo multipliers!")
                RuleItem(number = "4", title = "Trigger Fury Mode", desc = "Chain 3 consecutive line-clearing moves to activate FURY MODE with blazing arcade power!")
                RuleItem(number = "5", title = "Don't Run Out of Room", desc = "Game ends only when none of your 3 available pieces can fit on the board.")

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("GOT IT!", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }
        }
    }
}

@Composable
fun RuleItem(number: String, title: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .background(Color(0xFF0284C7), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = number, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(text = title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(text = desc, color = Color(0xFF94A3B8), fontSize = 12.sp)
        }
    }
}
