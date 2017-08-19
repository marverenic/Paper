package com.marverenic.reader

import android.app.Application
import android.content.Context
import android.support.v4.app.Fragment
import com.marverenic.reader.inject.ContextModule
import com.marverenic.reader.inject.DaggerPaperComponent
import com.marverenic.reader.inject.PaperGraph

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
        component = DaggerPaperComponent.builder()
                .contextModule(ContextModule(this))
                .build()
    }

}