# BLOCK FURY: CUBE MASTER

**TAGLINE:** *PLACE. SMASH. DOMINATE.*  
**PACKAGE:** `com.blockfury.cubemaster`  
**ORIENTATION:** Portrait (Smartphones & Tablets)  
**PLATFORM:** Android (API 26+)  

---

## 🎮 Overview

**BLOCK FURY: CUBE MASTER** is a casual arcade block puzzle game built with Kotlin and Jetpack Compose. It delivers a fast, colorful, satisfying block-placement puzzle experience with 3D-styled glossy cubes, real offline audio effects, responsive drag-and-drop and tap-to-place touch controls, combo multipliers, Fury Mode, and complete local progression.

---

## 🌟 Key Features

1. **Core Gameplay & Mechanics:**
   - 8x8 Board with recessed dark-blue grid cells.
   - 3D glossy beveled cubes across 8 distinct colors (Crimson Ruby, Solar Blaze, Electric Gold, Neon Emerald, Cyber Cyan, Hyper Blue, Cosmic Violet, Laser Pink).
   - Dynamic 3-piece tray with balanced variety (dots, 2-lines, 3-lines, 4-lines, 5-lines, 2x2 squares, 3x3 big blocks, corners, T-shapes, L-shapes, Z-shapes, and crosses).
   - Drag-and-drop piece placement with touch offset so thumbs never obscure the blocks, plus tap-to-select for one-finger play.
   - Real-time ghost preview with animated pulsing highlight and collision detection.
   - Horizontal and vertical line clearing, with simultaneous multi-line clears.
   - Clear animations with particle bursts and screen shake.
   - Game over detection tests all remaining pieces against all legal board positions.

2. **Scoring & Combo System:**
   - Block placement points (10 pts per cell).
   - Single line clear: 100 points.
   - Double line clear: 300 points (2x bonus).
   - Multi-line clear: 600 - 1400+ points.
   - Consecutive line clears build a combo meter (x1, x2, x3...).
   - **FURY MODE:** Triggered on 3+ consecutive clears with arcade music and visual energy!

3. **Game Modes:**
   - **Classic Mode:** Endless puzzle experience with saved local high score.
   - **Time Attack:** 120-second countdown rush to maximize score and combos.
   - **Daily Challenge:** Deterministic puzzle challenge seeded by date to clear 15 lines and earn 50 gold coins.

4. **100% Offline Audio Architecture:**
   - Bundled PCM WAV audio assets in `app/src/main/res/raw/`.
   - Android `SoundPool` for low-latency sound effects (startup, taps, piece select, placements, line clears, combos, coins, high score, game over, challenge victory).
   - Android `MediaPlayer` for background music tracks (menu chill synth, gameplay groove, and intense Fury Mode beat).
   - Independent sliders for SFX and Music volume with persistent mute toggles.
   - Audio focus and Android lifecycle management (pauses and resumes cleanly).

5. **Progression & Offline Storage:**
   - Gold coin economy earned through line clears, combos, and daily challenges.
   - Unlockable Board Themes (Neon Arcade, Cosmic Deep, Sunset Blaze, Emerald Mine).
   - Achievements system with trophy rewards.
   - High scores saved per mode in local SharedPreferences.
   - Settings: Sound volume, Music volume, Haptics toggle, Reduced motion toggle, and Reset progress.

6. **Premium Arcade Fusion Visuals & SmartLink Integration:**
   - Deep midnight-blue/indigo atmospheric background with floating particles and ambient radial aura.
   - Balanced vertical layout eliminating dead space between the HUD and board.
   - Glassmorphic top HUD with glowing scores and gold coin counters.
   - 3D tactile glossy cubes with upper bevel highlights, bottom drop shadows, and specular shines.
   - Translucent glass pedestals for piece trays with animated gold/cyan selection pulse.
   - Score count-up animations on Game Over with celebration badges.
   - **SmartLink Integration:** Clearly labeled, optional `🎁 SPECIAL OFFERS` button on the Home Screen (and secondary entry in Pause dialog) safely opening `https://www.profitableratecpmnetwork.com/cbni9m1hk2?key=1691fbbe4463e0e3e65f7e27606f80e7` via standard external Android Intent without interrupting gameplay or lifecycle.

---

## 📁 Project Structure

```
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml
│   │   │   ├── java/com/example/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── audio/
│   │   │   │   │   └── GameAudioManager.kt
│   │   │   │   ├── data/
│   │   │   │   │   ├── GameDataModels.kt
│   │   │   │   │   └── GamePreferences.kt
│   │   │   │   ├── model/
│   │   │   │   │   ├── BlockPiece.kt
│   │   │   │   │   ├── Board.kt
│   │   │   │   │   └── GameModels.kt
│   │   │   │   ├── ui/
│   │   │   │   │   ├── BlockFuryApp.kt
│   │   │   │   │   ├── BoardCanvas.kt
│   │   │   │   │   ├── Dialogs.kt
│   │   │   │   │   ├── GamePlayScreen.kt
│   │   │   │   │   ├── MainMenuScreen.kt
│   │   │   │   │   ├── PieceTray.kt
│   │   │   │   │   ├── SplashScreen.kt
│   │   │   │   │   └── theme/
│   │   │   │   └── viewmodel/
│   │   │   │       └── GameViewModel.kt
│   │   │   └── res/
│   │   │       ├── drawable/
│   │   │       ├── mipmap-*/ (Adaptive & raster icons across mdpi to xxxhdpi)
│   │   │       ├── raw/ (19 offline bundled WAV audio assets)
│   │   │       └── values/
│   │   └── test/java/com/example/
│   │       ├── ExampleRobolectricTest.kt
│   │       ├── ExampleUnitTest.kt
│   │       └── GameEngineTest.kt
│   └── build.gradle.kts
├── settings.gradle.kts
└── metadata.json
```

---

## 🛠️ Building & Installing the APK

### 1. Build with Gradle CLI
```bash
# Build Debug APK
gradle assembleDebug

# Output APK path:
# app/build/outputs/apk/debug/app-debug.apk
```

### 2. Run Unit Tests
```bash
gradle :app:testDebugUnitTest
```

### 3. Install on an Android Phone via ADB
```bash
# Connect phone via USB with USB Debugging enabled
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### 4. Direct Device Installation
Copy `app-debug.apk` to your phone's storage via USB, Google Drive, or local file transfer, tap the file in your device's Files app, and select **Install**.

---

## 🚀 Release Configuration & App Bundle (AAB)
To generate a Google Play release App Bundle:
```bash
gradle bundleRelease
```
Sign using your production keystore configured via standard Gradle signing configs or Android Studio Build > Generate Signed Bundle / APK.
