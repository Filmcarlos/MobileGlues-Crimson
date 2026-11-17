# ProGuard / R8 configuration for MobileGlues Crimson Renderer Plugin

# Preserve file names and line numbers for readable local crash stack traces
-keepattributes SourceFile,LineNumberTable

# Keep JNI native methods and classes interacting with libmobileglues.so
-keepclasseswithmembernames,includedescriptorclasses class * {
    native <methods>;
}

# Keep any explicit JNI bridge or callback classes in core/data/domain
-keep class com.example.data.gateway.** { *; }
-keep class com.crimsondev.glplugin.** { *; }

# Keep Kotlinx Serialization generated serializers for type-safe Navigation Compose
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.example.**$$serializer { *; }
-keepclassmembers class com.example.** {
    *** Companion;
}
-keepclasseswithmembers class com.example.** {
    kotlinx.serialization.KSerializer serializer(...);
}
