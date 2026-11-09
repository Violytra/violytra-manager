package app.violytra.manager.installer.step.download

import androidx.compose.runtime.Stable
import app.violytra.manager.R
import app.violytra.manager.domain.manager.BundleManager
import app.violytra.manager.installer.step.download.base.DownloadStep
import org.koin.core.component.inject
import java.io.File

/**
 * Downloads the Violytra Xposed module from the release the patch bundle is from
 *
 * https://github.com/Violytra/violytra
 */
@Stable
class DownloadModStep(
    workingDir: File
): DownloadStep() {

    private val bundleManager: BundleManager by inject()

    override val nameRes = R.string.step_dl_mod

    override val downloadFullUrl: String
        get() = bundleManager.bundle?.moduleUrl ?: error("The patch bundle isn't loaded")
    override val destination = preferenceManager.moduleLocation
    override val workingCopy = workingDir.resolve("xposed.apk")

}
