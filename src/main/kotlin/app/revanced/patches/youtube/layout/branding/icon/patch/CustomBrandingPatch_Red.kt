package app.revanced.patches.youtube.layout.branding.icon.patch

import app.revanced.extensions.doRecursively
import app.revanced.extensions.startsWithAny
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.*
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patches.youtube.layout.branding.icon.annotations.CustomBrandingCompatibility
import app.revanced.patches.youtube.misc.manifest.patch.FixLocaleConfigErrorPatch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import org.w3c.dom.Element

@Patch
@Name("custom-branding-red")
@Description("Changes the YouTube launcher icon and name to your choice (defaults to ReVanced Red).")
@CustomBrandingCompatibility
@Version("0.0.1")
class CustomBrandingPatch_Red : ResourcePatch {
    override fun execute(context: ResourceContext): PatchResult {
        val classLoader = this.javaClass.classLoader
        val resDirectory = context["res"]
        if (!resDirectory.isDirectory) return PatchResultError("The res folder can not be found.")

        // Icon branding
        val AppiconNames = arrayOf(
            "adaptiveproduct_youtube_background_color_108",
            "adaptiveproduct_youtube_foreground_color_108",
            "ic_launcher",
            "ic_launcher_round"
        )

        /*
        val SplashiconNames = arrayOf(
            "product_logo_youtube_color_24",
            "product_logo_youtube_color_36",
            "product_logo_youtube_color_144",
            "product_logo_youtube_color_192"
        )
        */

        mapOf(
            "xxxhdpi" to 192,
            "xxhdpi" to 144,
            "xhdpi" to 96,
            "hdpi" to 72,
            "mdpi" to 48
        ).forEach { (iconDirectory, size) ->
            AppiconNames.forEach iconLoop@{ iconName ->
                Files.copy(
                    classLoader.getResourceAsStream("branding/red/launchericon/$size/$iconName.png")!!,
                    resDirectory.resolve("mipmap-$iconDirectory").resolve("$iconName.png").toPath(),
                    StandardCopyOption.REPLACE_EXISTING
                )
            }
            /*
            SplashiconNames.forEach iconLoop@{ iconName ->
                Files.copy(
                    classLoader.getResourceAsStream("branding/red/splashicon/$size/$iconName.png")!!,
                    resDirectory.resolve("drawable-$iconDirectory").resolve("$iconName.png").toPath(),
                    StandardCopyOption.REPLACE_EXISTING
                )
            }
            */
        }

        val drawables = "drawable" to arrayOf(
            "adaptive_monochrome_ic_youtube_launcher"
        )

        val xmlResources = arrayOf(drawables)

        xmlResources.forEach { (path, resourceNames) ->
            resourceNames.forEach { name ->
                val relativePath = "$path/$name.xml"

                Files.copy(
                    classLoader.getResourceAsStream("branding/monochrome/$relativePath")!!,
                    context["res"].resolve(relativePath).toPath(),
                    StandardCopyOption.REPLACE_EXISTING
                )
            }
        }

        // Name branding
        val resourceFileNames = arrayOf(
            "strings.xml"
        )

        context.forEach {
            if (!it.name.startsWithAny(*resourceFileNames)) return@forEach

            // for each file in the "layouts" directory replace all necessary attributes content
            context.xmlEditor[it.absolutePath].use { editor ->
            val resourcesNode = editor.file.getElementsByTagName("resources").item(0) as Element

                for (i in 0 until resourcesNode.childNodes.length) {
                    val node = resourcesNode.childNodes.item(i)
                    if (node !is Element) continue

                    val element = resourcesNode.childNodes.item(i) as Element
                    element.textContent = when (element.getAttribute("name")) {
                        "application_name" -> "YouTube ReVanced"
                        else -> continue
                    }
                }
            }
        }

        /*
        val disableSplashAnimation1 = context["res/values-v31/styles.xml"]
        if (!disableSplashAnimation1.isFile) return PatchResultError("Failed to disable Splash Animation.")

        disableSplashAnimation1.writeText(
                disableSplashAnimation1.readText()
                        .replace(
                                "<item name=\"android:windowSplashScreenAnimatedIcon\">@drawable/avd_anim</item>",
                                ""
                        )
        )

        val disableSplashAnimation2 = context["res/values-night-v31/styles.xml"]
        if (!disableSplashAnimation2.isFile) return PatchResultError("Failed to disable Splash Animation.")

        disableSplashAnimation2.writeText(
                disableSplashAnimation2.readText()
                        .replace(
                                "<item name=\"android:windowSplashScreenAnimatedIcon\">@drawable/avd_anim</item>",
                                ""
                        )
        )
        */

        return PatchResultSuccess()
    }

}