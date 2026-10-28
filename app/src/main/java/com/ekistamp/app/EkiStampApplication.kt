package com.ekistamp.app

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
import coil3.request.crossfade
import com.ekistamp.app.data.local.EkiStampDatabase
import com.ekistamp.app.data.prefs.ThemePreferences
import com.ekistamp.app.data.remote.EkiStampRemoteDataSource
import com.ekistamp.app.data.repository.EkiStampRepository
import com.ekistamp.app.data.repository.EkiStampRepositoryImpl
import com.ekistamp.app.data.translation.TranslationManager
import com.ekistamp.app.data.update.ApkInstaller
import com.ekistamp.app.data.update.UpdateChecker
import okhttp3.OkHttpClient

class EkiStampApplication : Application(), SingletonImageLoader.Factory {

    /** Manual dependency container; swap for Hilt if the graph grows. */
    val repository: EkiStampRepository by lazy {
        EkiStampRepositoryImpl(
            dao = EkiStampDatabase.create(this).ekiStampDao(),
            remote = EkiStampRemoteDataSource(),
            translator = TranslationManager()
        )
    }

    val themePreferences: ThemePreferences by lazy { ThemePreferences(this) }

    private val httpClient by lazy { OkHttpClient() }

    val updateChecker: UpdateChecker by lazy {
        UpdateChecker(BuildConfig.GITHUB_REPO, BuildConfig.VERSION_NAME, httpClient)
    }

    val apkInstaller: ApkInstaller by lazy { ApkInstaller(this, httpClient) }

    override fun newImageLoader(context: PlatformContext): ImageLoader =
        ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder().maxSizePercent(context, 0.25).build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("stamp_images"))
                    .maxSizeBytes(256L * 1024 * 1024)
                    .build()
            }
            .crossfade(true)
            .build()
}
