package com.devbin.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class BaseUseCase<in ParamsType, out ReturnType> {

    private var job: Job? = null

    open val dispatcher: CoroutineDispatcher = Dispatchers.IO
    open val mainDispatcher: CoroutineDispatcher = Dispatchers.Main

    // This fun will be used to provide the actual implementation
    // in the child class
    abstract suspend fun execute(params: ParamsType): ReturnType

    // This will enable you to call the useCase as function
    // example sendPayment(params)
    // it will run the buildUseCase function using the provided dispatcher
    operator fun invoke(params: ParamsType, onError: (Throwable) -> Unit, onSuccess: (ReturnType) -> Unit) {
        job = CoroutineScope(dispatcher).launch {
            try {
                val response = execute(params)
                withContext(mainDispatcher) { onSuccess(response) }
            } catch (e: Exception) {
                withContext(mainDispatcher) { onError(e) }
            }
        }
    }

    fun cancel() {
        job?.cancel()
    }
}