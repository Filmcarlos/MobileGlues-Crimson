package com.example

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Fast, deterministic JVM verification of the FCL / Zalith Launcher Manifest contract
 * and BuildConfig constants injected by `app/build.gradle.kts`.
 */
class ExampleRobolectricTest {

    @Test
    fun `verify BuildConfig and AndroidManifest FCL meta-data contract`() {
        assertEquals("com.crimsondev.glplugin", BuildConfig.APPLICATION_ID)
        assertEquals("1.0.0-crimson", BuildConfig.VERSION_NAME)
        assertEquals("MobileGlues Crimson", BuildConfig.RENDERER_DISPLAY_NAME)
        assertEquals("libmobileglues.so", BuildConfig.NATIVE_LIB_FILENAME)
        assertEquals(
            "MobileGlues Crimson:libmobileglues.so:libmobileglues.so",
            BuildConfig.RENDERER_META_SPEC
        )
        assertEquals("LIBGL_ES=3", BuildConfig.BOAT_ENV_SPEC)
        assertEquals(
            "LIBGL_ES=3:POJAV_RENDERER=opengles3:POJAVEXEC_EGL=libmobileglues.so:LIBGL_EGL=libmobileglues.so",
            BuildConfig.POJAV_ENV_SPEC
        )
        assertEquals("1.17", BuildConfig.MIN_MC_VER)
        assertTrue(BuildConfig.MAX_MC_VER.isEmpty())
    }
}
