# Native Binary Directory (arm64-v8a)

Place your compiled 64-bit ARM native renderer binary in this folder before building the APK:

- Required filename: `libmobileglues.so`
- Full path: `app/src/main/jniLibs/arm64-v8a/libmobileglues.so`
- ABI: `arm64-v8a` (AArch64 - primary target for modern Android devices and FCL / Zalith Launcher)

## Verification
1. Ensure `packaging.jniLibs.useLegacyPackaging = true` in `app/build.gradle.kts` (already configured).
2. Ensure `android:extractNativeLibs="true"` in `AndroidManifest.xml` (already configured).
3. Launch the app and check the **Home** screen status card — it will verify extraction in `applicationInfo.nativeLibraryDir` and test `dlopen` / `System.loadLibrary("mobileglues")` readiness.
