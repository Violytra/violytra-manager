package app.violytra.manager

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.system.Os
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import app.reseam.sdk.ReseamAndroidHost
import app.violytra.manager.di.httpModule
import app.violytra.manager.di.managerModule
import app.violytra.manager.di.repositoryModule
import app.violytra.manager.di.viewModelModule
import app.violytra.manager.domain.manager.PreferenceManager
import app.violytra.manager.domain.manager.UpdateCheckerDuration
import app.violytra.manager.updatechecker.worker.UpdateWorker
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ManagerApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Android sets TMPDIR for apps only from 13 on. Without it the Reseam SDK's temp files fall back to /data/local/tmp, which apps can't write to
        Os.setenv("TMPDIR", cacheDir.absolutePath, false)
        ReseamAndroidHost.setClassLoader(classLoader)
        initNotificationChannels()

        startKoin {
            androidContext(this@ManagerApplication)
            modules(
                httpModule,
                managerModule,
                viewModelModule,
                repositoryModule
            )
        }

        val prefs: PreferenceManager = get()

        if (prefs.updateDuration != UpdateCheckerDuration.DISABLED) {
            val duration = prefs.updateDuration
            WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
                "app.violytra.manager.UPDATE_CHECK",
                ExistingPeriodicWorkPolicy.KEEP,
                PeriodicWorkRequestBuilder<UpdateWorker>(duration.time, duration.unit).build()
            )
        }
    }

    private fun initNotificationChannels() {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val updates = NotificationChannel(
            "${BuildConfig.APPLICATION_ID}.notifications.UPDATE",
            "Discord updates",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        nm.createNotificationChannel(updates)
    }

}