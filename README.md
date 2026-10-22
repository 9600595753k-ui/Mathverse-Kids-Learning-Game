# 🧮 MathWhiz Kids — Gamified Elementary Math Learning App

A gamified, interactive, and offline-accessible Android application built with **Kotlin** and **Jetpack Compose (Material Design 3)** for elementary school students (Kindergarten through Grade 5).

---

## 📱 Download & Install the APK File

### Method 1: Direct APK in Repository
You can download the pre-built APK file directly from this repository:
👉 **[`apk/MathWhiz-Kids.apk`](apk/MathWhiz-Kids.apk)**

**To install on your Android device:**
1. Download `MathWhiz-Kids.apk` directly to your Android phone or tablet.
2. Tap the downloaded file in your browser or File Manager.
3. If prompted by Android security, toggle **"Allow from this source"**.
4. Tap **Install** and enjoy!

### Method 2: GitHub Actions Automated Build
Every push automatically builds the newest APK using GitHub Actions:
1. Click the **Actions** tab at the top of this GitHub repository.
2. Click on the latest workflow run: **"Build MathWhiz Kids APK"**.
3. Scroll down to the **Artifacts** section at the bottom.
4. Download **`MathWhiz-Kids-APK.zip`**, unzip it, and install `app-debug.apk` directly!

---

## 🎮 Key Features

- **6 Interactive Math Arcade Games:**
  - 🎈 **Balloon Pop Mania:** Tap floating balloons to pop the exact answer with combo streaks.
  - 🍕 **Pizza Chef Fraction Bakery:** Slice and serve pizzas to hungry customers to master 1/2, 3/4, and 5/8 fractions.
  - 🃏 **Memory Math Flip & Match:** High-speed card matching pairing math equations with their solutions.
  - ⚖️ **Monster Balance Seesaw:** Physics-based weight balancing seesaw.
  - ⚡ **60-Second Lightning Sprint:** Rapid-fire arithmetic clock challenge with combo multipliers.
  - ✖️ **Times Table Matrix & Explorer:** Tables 1 through 12 with visual dot array matrices and flashcards.
- **✏️ In-Quiz Scratchpad & Rough Sheet:**
  - Finger-drawn doodle canvas with math grid paper, color palette, eraser, and undo for rough calculations and carrying numbers.
- **🔊 Text-to-Speech (TTS) Voice Read-Aloud:**
  - Young students or language learners can tap the speaker button to hear math problems read aloud.
- **🔒 Parental Controls & Analytics:**
  - PIN gate (`1234`), student profile switcher, and daily screen time limit settings.
  - One-tap native PDF progress report generation, system printing, and WhatsApp sharing.
- **👑 VIP Subscription via Google Pay (GPay):**
  - 1-Year VIP for ₹10 INR via Google Pay / UPI.
- **🌐 Offline-First & Multi-Language:**
  - Full Room Database persistence.
  - Multi-language support (English, Spanish, French, German, Hindi, Tamil).
  - Dynamic Light & Dark Mode support.

---

## 🛠️ Building From Source

```bash
# Clone the repository
git clone https://github.com/<your-username>/<your-repo-name>.git

# Navigate into directory
cd mathwhiz-kids

# Build debug APK
gradle assembleDebug

# Output APK location
# app/build/outputs/apk/debug/app-debug.apk
```
