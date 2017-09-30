package com.marverenic.reader.inject

import com.marverenic.reader.data.*
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
    fun provideAuthManager(service: FeedlyService, prefs: PreferenceStore): AuthenticationManager
            = FeedlyAuthenticationManager(service, prefs)

    @Provides
    @Singleton
    fun provideRssStore(authManager: AuthenticationManager, service: FeedlyService): RssStore
            = FeedlyRssStore(authManager, service)

}
