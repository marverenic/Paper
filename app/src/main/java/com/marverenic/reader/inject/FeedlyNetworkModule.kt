package com.marverenic.reader.inject

import com.marverenic.reader.data.AuthenticationManager
import com.marverenic.reader.data.DevAuthenticationManager
import com.marverenic.reader.data.FeedlyRssStore
import com.marverenic.reader.data.RssStore
import com.marverenic.reader.data.database.RssDatabase
import com.marverenic.reader.data.service.FeedlyService
import com.marverenic.reader.data.service.createFeedlyApi
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class FeedlyNetworkModule {

    @Provides
    @Singleton
    fun provideFeedlyApi(): FeedlyService = createFeedlyApi()

    @Provides
    @Singleton
    fun provideAuthManager(): AuthenticationManager = DevAuthenticationManager()

    @Provides
    @Singleton
    fun provideRssStore(authManager: AuthenticationManager,
                        service: FeedlyService,
                        rssDatabase: RssDatabase): RssStore
            = FeedlyRssStore(authManager, service, rssDatabase)

}
