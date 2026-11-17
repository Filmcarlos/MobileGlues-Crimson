# MobileGlues Crimson (`com.crimsondev.glplugin`)

A production-grade Android Renderer Plugin foundation for **Minecraft Java Launchers** (**Fold Craft Launcher (FCL)** & **Zalith Launcher**), built with **Kotlin**, **Jetpack Compose**, **Material 3**, and a custom **Crimson Obsidian Design System**.

---

## Key Highlights

- **Strict Manifest Contract**: Pre-configured `<meta-data>` (`fclPlugin`, `des`, `renderer`, `boatEnv`, `pojavEnv`, `minMCVer`, `maxMCVer`) driven entirely from `manifestPlaceholders` in `app/build.gradle.kts`.
- **100% Offline & Privacy-First**: Zero `INTERNET` permissions (`tools:node="remove"`), zero analytics, zero telemetry.
- **Real Hardware & Package Inspection**:
  - Inspects `applicationInfo.nativeLibraryDir` and APK `lib/<abi>/libmobileglues.so` entries in real time.
  - Queries installed Minecraft Java launchers (`com.tungsten.fcl`, `com.movtery.zalithlauncher.v2`) via Android `PackageManager`.
- **Graceful Degradation**: Works cleanly even before `libmobileglues.so` is added to `jniLibs/`, surfacing clear diagnostics in the UI.
- **Built-in Diagnostics**: Local ring-buffer/file logger with an interactive in-app Log Viewer + copy-to-clipboard, plus a local `UncaughtExceptionHandler` crash report recorder.
- **Bilingual Support**: Full Thai (`values-th/strings.xml`) and English (`values/strings.xml`) localization with instant in-app switching.

---

## 1. How to Add `libmobileglues.so`

Place your compiled `libmobileglues.so` binaries into the corresponding ABI folders under `app/src/main/jniLibs/`:

```text
app/src/main/jniLibs/
├── arm64-v8a/
│   └── libmobileglues.so      <-- Primary 64-bit ARM devices (Required for modern phones)
├── armeabi-v7a/
│   └── libmobileglues.so      <-- Optional: 32-bit ARM devices
└── x86_64/
    └── libmobileglues.so      <-- Optional: Chromebooks / Emulators
```

> **Important**: Keep the filename strictly as `libmobileglues.so` so it matches the `renderer`, `boatEnv`, and `pojavEnv` manifest contract.
> `packaging.jniLibs.useLegacyPackaging = true` and `android:extractNativeLibs="true"` are already enabled so launchers can load the `.so` directly from the installed native library directory.

---

## 2. How to Build the Project

### Debug Build
```bash
gradle :app:assembleDebug
```
Output APK: `app/build/outputs/apk/debug/app-debug.apk`

### Run Unit & Robolectric Tests
```bash
gradle :app:testDebugUnitTest
```

### Run Android Lint
```bash
gradle :app:lintDebug
```

---

## 3. How to Sign a Release APK with Your Own Keystore

1. **Generate a Keystore** (if you don't already have one):
   ```bash
   keytool -genkeypair -v \
     -keystore my-upload-key.jks \
     -alias upload \
     -keyalg RSA \
     -keysize 2048 \
     -validity 10000
   ```

2. **Build Signed Release APK** by passing environment variables expected by `signingConfigs.release` in `app/build.gradle.kts`:
   ```bash
   export KEYSTORE_PATH="/absolute/path/to/my-upload-key.jks"
   export STORE_PASSWORD="your_keystore_password"
   export KEY_PASSWORD="your_key_password"
   gradle :app:assembleRelease
   ```
   Output APK: `app/build/outputs/apk/release/app-release.apk`

---

## License

- Wrapper Application & UI Architecture: **MIT / Apache-2.0 compatible**
- `MobileGlues` Native Renderer Library: **GNU Lesser General Public License v2.1 (LGPL-2.1)** — see [LICENSE](LICENSE) for full details.
