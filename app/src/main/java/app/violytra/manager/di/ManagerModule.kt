package app.violytra.manager.di

import app.violytra.manager.domain.manager.BundleManager
import app.violytra.manager.domain.manager.DownloadManager
import app.violytra.manager.domain.manager.InstallManager
import app.violytra.manager.domain.manager.PathManager
import app.violytra.manager.domain.manager.PreferenceManager
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val managerModule = module {
    singleOf(::PathManager)
    singleOf(::DownloadManager)
    singleOf(::PreferenceManager)
    singleOf(::InstallManager)
    singleOf(::BundleManager)
}