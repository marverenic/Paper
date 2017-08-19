package com.marverenic.reader.inject

import dagger.Component

@Component(modules = arrayOf(ContextModule::class))
interface PaperComponent : PaperGraph