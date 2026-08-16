# Tic Tac Toe Premium — Android (Kotlin + Jetpack Compose)

این پروژه در محیط چت (بدون اینترنت و بدون Android SDK) قابل build نیست؛
باید روی سیستم خودت (با اینترنت + JDK 17 + Android SDK) build شود.

## ساخت APK — دو روش

### روش ۱ (ساده‌ترین): Android Studio
1. پروژه را در Android Studio باز کن (Open → پوشه‌ی این پروژه).
2. صبر کن Gradle Sync تمام شود (اینترنت لازم دارد تا اولین بار AGP/Kotlin/Gradle را دانلود کند).
3. از منو: Build → Build Bundle(s)/APK(s) → Build APK(s).
4. مسیر خروجی: `app/build/outputs/apk/debug/app-debug.apk`

### روش ۲: خط فرمان
```
gradle wrapper --gradle-version 8.7   # فقط بار اول، برای ساخت gradlew
./gradlew assembleDebug
```
APK در مسیر زیر ساخته می‌شود:
```
app/build/outputs/apk/debug/app-debug.apk
```

برای نسخه‌ی Release (بدون امضا):
```
./gradlew assembleRelease
```
خروجی: `app/build/outputs/apk/release/app-release-unsigned.apk`
(برای نصب روی گوشی باید امضا/zipalign شود یا از Android Studio → Generate Signed Bundle/APK استفاده کنی.)

## نسخه‌های استفاده‌شده (پایدار و در دسترس)
- Android Gradle Plugin: 8.5.2
- Gradle: 8.7
- Kotlin: 1.9.24
- Compose Compiler: 1.5.14
- compileSdk / targetSdk: 34
- minSdk: 26

## ساختار
- `app/src/main/java/com/namvar/tictactoe/game/GameEngine.kt` — منطق بازی + AI (Minimax در Expert)
- `app/src/main/java/com/namvar/tictactoe/data/GameStore.kt` — ذخیره‌سازی آمار و تنظیمات (SharedPreferences)
- `app/src/main/java/com/namvar/tictactoe/ui/App.kt` — تمام صفحات (منو، انتخاب سختی، بازی، تنظیمات، آمار، درباره)
- `app/src/main/java/com/namvar/tictactoe/MainActivity.kt` — نقطه‌ی ورود

Application ID: `com.namvar.tictactoe` | نسخه: `1.0.0`
