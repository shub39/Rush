package com.shub39.rush.core.domain.backup

sealed class RestoreResult {
    data object Success : RestoreResult()
    data class Failure(val exceptionType: RestoreFailedException) : RestoreResult()
}

sealed interface RestoreState {
    data object Idle : RestoreState
    data object Restoring : RestoreState
    data object Restored : RestoreState
    data class Failure(val exception: RestoreFailedException) : RestoreState
}

sealed interface RestoreFailedException {
    data object InvalidFile : RestoreFailedException
    data object OldSchema : RestoreFailedException
}