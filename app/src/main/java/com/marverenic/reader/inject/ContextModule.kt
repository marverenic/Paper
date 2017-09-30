package com.marverenic.reader.inject

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import dagger.Module
import dagger.Provides

@Module
class ContextModule(private val context: Context) {

    @Provides
    fun provideContext() = context

    @Provides
    fun provideSharedPrefs() = PreferenceManager.getDefaultSharedPreferences(context)

    @Provides
    fun providePrefManager(prefs: SharedPreferences) = com.marverenic.reader.data.PreferenceStore(prefs)

}