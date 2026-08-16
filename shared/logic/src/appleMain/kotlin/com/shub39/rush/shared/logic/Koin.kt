/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.shub39.rush.shared.logic

import com.shub39.rush.shared.core.interfaces.SongRepository
import com.shub39.rush.shared.logic.di.DataModule
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.plugin.module.dsl.startKoin

fun initKoin() {
    startKoin<IosModule>()
}

class KoinDependencies : KoinComponent {
    val songRepository: SongRepository by inject()
}

@Module(includes = [DataModule::class]) @Configuration class IosModule
