package com.intheme.dev.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

inline fun LifecycleOwner.launchWhenStarted(crossinline block: suspend () -> Unit) {
    lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
            block()
        }
    }
}

inline fun LifecycleOwner.lifeCycleLaunch(crossinline block: suspend () -> Unit) {
    lifecycleScope.launch {
        block()
    }
}

inline fun LifecycleOwner.launchWhenCreated(crossinline block: suspend () -> Unit) {
    lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
            block()
        }
    }
}

inline fun LifecycleOwner.launchWhenResumed(crossinline block: suspend () -> Unit) {
    lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            block()
        }
    }
}

fun coroutineAsync(runner: suspend CoroutineScope.() -> Unit, uiRunner: CoroutineScope.() -> Unit) =
    CoroutineScope(Dispatchers.IO).launch {
        runner.invoke((this))
        withContext(Dispatchers.Main){
            uiRunner.invoke(this)
        }
    }

fun coroutineUI(runner: suspend CoroutineScope.() -> Unit) =
    CoroutineScope(Dispatchers.Main).launch { runner.invoke((this)) }


fun coroutineAsync(runner: suspend CoroutineScope.() -> Unit) =
    CoroutineScope(Dispatchers.IO).launch {
        runner.invoke((this))

    }