package app.violytra.manager.installer.step.installing

import android.content.Context
import app.violytra.manager.R
import app.violytra.manager.domain.manager.InstallMethod
import app.violytra.manager.domain.manager.PreferenceManager
import app.violytra.manager.installer.Installer
import app.violytra.manager.installer.session.SessionInstaller
import app.violytra.manager.installer.shizuku.ShizukuInstaller
import app.violytra.manager.installer.shizuku.ShizukuPermissions
import app.violytra.manager.installer.step.Step
import app.violytra.manager.installer.step.StepGroup
import app.violytra.manager.installer.step.StepRunner
import app.violytra.manager.utils.isMiui
import app.violytra.manager.utils.showToast
import org.koin.core.component.inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Installs all the modified splits with the users desired [Installer]
 *
 * @see SessionInstaller
 * @see ShizukuInstaller
 *
 * @param apksDir Where all the patched APKs are
 */
class InstallStep(
    private val apksDir: File
): Step() {

    private val preferences: PreferenceManager by inject()
    private val context: Context by inject()

    override val group = StepGroup.INSTALLING
    override val nameRes = R.string.step_installing

    override suspend fun run(runner: StepRunner) {
        runner.logger.i("Installing apks")
        val files = apksDir.listFiles()
            ?.takeIf { it.isNotEmpty() }
            ?: throw Error("Missing APKs from patching step; failure likely")

        val installMethod = if (preferences.installMethod == InstallMethod.SHIZUKU && !ShizukuPermissions.waitShizukuPermissions()) {
            // Temporarily use DEFAULT if SHIZUKU permissions are not granted
            withContext(Dispatchers.Main) {
                context.applicationContext.showToast(R.string.msg_shizuku_denied)
            }
            InstallMethod.DEFAULT
        } else {
            preferences.installMethod
        }

        val installer: Installer = when (installMethod) {
            InstallMethod.DEFAULT -> SessionInstaller(context, preferences)
            InstallMethod.SHIZUKU -> ShizukuInstaller(context)
        }

        installer.installApks(silent = !isMiui, *files)
    }

}