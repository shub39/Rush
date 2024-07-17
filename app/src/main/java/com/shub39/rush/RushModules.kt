package com.shub39.rush

import com.shub39.rush.component.provideImageLoader
import com.shub39.rush.viewmodel.RushViewModel
import org.koin.dsl.module

val rushModules = module {
    factory { RushViewModel(get()) }
    single { provideImageLoader(get()) }
}