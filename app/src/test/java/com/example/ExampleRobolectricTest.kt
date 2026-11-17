package com.example

import android.content.Context
import android.content.pm.PackageManager
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `verify app_name and AndroidManifest FCL meta-data contract`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        assertEquals("MobileGlues Crimson", context.getString(R.string.app_name))

        val appInfo = context.packageManager.getApplicationInfo(
            context.packageName,
            PackageManager.GET_META_DATA
        )
        val meta = appInfo.metaData
        assertNotNull("Application meta-data must not be null", meta)
        assertEquals(true, meta.getBoolean("fclPlugin"))
        assertEquals(
            "MobileGlues Crimson:libmobileglues.so:libmobileglues.so",
            meta.getString("renderer")
        )
        assertEquals("LIBGL_ES=3", meta.getString("boatEnv"))
        assertEquals(
            "LIBGL_ES=3:POJAV_RENDERER=opengles3:POJAVEXEC_EGL=libmobileglues.so:LIBGL_EGL=libmobileglues.so",
            meta.getString("pojavEnv")
        )
        val minMcVer = meta.getString("minMCVer") ?: meta.getFloat("minMCVer").toString()
        assertEquals("1.17", minMcVer)
    }
}
