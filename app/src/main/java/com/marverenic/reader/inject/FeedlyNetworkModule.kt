package com.marverenic.reader.inject

import com.marverenic.reader.data.FeedlyRssStore
import com.marverenic.reader.data.RssStore
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
    fun provideRssStore(service: FeedlyService): RssStore = FeedlyRssStore(service)

}
