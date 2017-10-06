package com.marverenic.reader

import android.app.Application
import android.content.Context
import android.support.v4.app.Fragment
import com.jakewharton.picasso.OkHttp3Downloader
import com.marverenic.reader.inject.ContextModule
import com.marverenic.reader.inject.DaggerPaperComponent
import com.marverenic.reader.inject.PaperGraph
import com.squareup.picasso.Picasso
import net.danlew.android.joda.JodaTimeAndroid

class ReaderApplication : Application() {

    private lateinit var component: PaperGraph

    companion object {

        fun component(context: Context) = (context.applicationContext as? ReaderApplication).let {
            it?.component ?: throw RuntimeException("Cannot access component from $it")
        }

        fun component(fragment: Fragment) = component(fragment.context)

    }

    override fun onCreate() {
        super.onCreate()
        JodaTimeAndroid.init(this)

        Picasso.setSingletonInstance(
                Picasso.Builder(this)
                        .downloader(OkHttp3Downloader(this))
                        .build()
        )

        component = DaggerPaperComponent.builder()
                .contextModule(ContextModule(this))
                .build()
    }

}