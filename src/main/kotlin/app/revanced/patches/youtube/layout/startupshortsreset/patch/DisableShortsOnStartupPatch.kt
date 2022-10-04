package app.revanced.patches.youtube.layout.startupshortsreset.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.impl.BytecodeData
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.impl.BytecodePatch
import app.revanced.patches.youtube.layout.startupshortsreset.annotations.StartupShortsResetCompatibility
import app.revanced.patches.youtube.layout.startupshortsreset.fingerprints.UserWasInShortsFingerprint
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction

@Patch
@DependsOn([IntegrationsPatch::class])
@Name("disable-startup-shorts-player")
@Description("Disables playing YouTube Shorts when launching YouTube.")
@StartupShortsResetCompatibility
@Version("0.0.1")
class DisableShortsOnStartupPatch : BytecodePatch(
    listOf(
        UserWasInShortsFingerprint
    )
) {
    override fun execute(data: BytecodeData): PatchResult {
        val userWasInShortsResult = UserWasInShortsFingerprint.result!!
        val userWasInShortsMethod = userWasInShortsResult.mutableMethod
        val moveResultIndex = userWasInShortsResult.scanResult.patternScanResult!!.endIndex

        userWasInShortsMethod.addInstructions(
            moveResultIndex + 1, """
            invoke-static { }, Lapp/revanced/integrations/patches/DisableStartupShortsPlayerPatch;->disableStartupShortsPlayer()Z
            move-result v5
            if-eqz v5, :disable_shorts_player
            return-void
            :disable_shorts_player
            nop
        """
        )

        return PatchResultSuccess()
    }
}
