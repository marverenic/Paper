package com.marverenic.reader.inject

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(
        ContextModule::class,
        FeedlyNetworkModule::class,
        FeedlyCacheModule::class
))
interface PaperComponent : PaperGraph