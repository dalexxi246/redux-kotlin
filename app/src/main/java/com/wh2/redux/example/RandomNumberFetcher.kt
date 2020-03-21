package com.wh2.redux.example

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random

object RandomNumberFetcher {

    suspend fun fetchRandomAsync(): Deferred<Int> {
        return withContext(Dispatchers.Default) {
            delay(1000)
            CompletableDeferred(Random.nextInt(99999))
        }
    }

    suspend fun fetchTenThousandsAsync(): Deferred<Int> {
        return withContext(Dispatchers.Default) {
            delay(1000)
            CompletableDeferred(10000)
        }
    }

}