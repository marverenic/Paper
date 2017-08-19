package com.marverenic.reader.inject

import android.content.Context
import dagger.Module

@Module
class ContextModule(private val context: Context) {

    fun provideContext() = context

}