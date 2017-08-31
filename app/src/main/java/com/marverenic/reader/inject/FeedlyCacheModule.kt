package com.marverenic.reader.inject

import android.content.Context
import com.marverenic.reader.data.database.RssDatabase
import com.marverenic.reader.data.database.SqliteRssDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class FeedlyCacheModule {

    @Provides
    @Singleton
    fun provideCacheDatabase(context: Context): RssDatabase = SqliteRssDatabase(context)

}