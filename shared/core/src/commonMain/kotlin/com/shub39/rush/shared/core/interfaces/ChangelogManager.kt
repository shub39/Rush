package com.shub39.rush.shared.core.interfaces

import com.shub39.rush.shared.core.dataclasses.Changelog
import kotlinx.coroutines.flow.Flow

interface ChangelogManager {
    val changelogs: Flow<Changelog>
}