package app.revanced.patches.music.misc.microg.annotations

import app.revanced.patcher.annotation.Compatibility
import app.revanced.patcher.annotation.Package

@Compatibility(
    [Package(
        "com.google.android.apps.youtube.music", arrayOf("5.23.50", "5.25.51", "5.26.53", "5.27.50")
    )]
)
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
internal annotation class MusicMicroGPatchCompatibility
