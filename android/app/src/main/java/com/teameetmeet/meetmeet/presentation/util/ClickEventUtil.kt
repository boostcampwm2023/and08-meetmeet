package com.teameetmeet.meetmeet.presentation.util

import android.view.View
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

const val THROTTLE_DURATION = 1000L

fun View.clicks(): Flow<Unit> = callbackFlow {
    setOnClickListener {
        this.trySend(Unit)
    }
    awaitClose { setOnClickListener(null) }
}

fun MaterialToolbar.menuClicks(): Flow<Int> = callbackFlow {
    setOnMenuItemClickListener { menuItem ->
        this.trySend(menuItem.itemId)
        true
    }
    awaitClose { setOnMenuItemClickListener(null) }
}

fun <T> Flow<T>.throttleFirst(windowDuration: Long): Flow<T> = flow {
    var lastEmissionTime = 0L
    collect { upstream ->
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastEmissionTime > windowDuration) {
            lastEmissionTime = currentTime
            emit(upstream)
        }
    }
}

fun View.setClickEvent(
    uiScope: CoroutineScope,
    windowDuration: Long = THROTTLE_DURATION,
    onClick: () -> Unit,
) {
    clicks()
        .throttleFirst(windowDuration)
        .onEach { onClick.invoke() }
        .launchIn(uiScope)
}

fun MaterialToolbar.setMenuClickEvent(
    uiScope: CoroutineScope,
    windowDuration: Long = THROTTLE_DURATION,
    onMenuClick: (menuId: Int) -> Unit,
) {
    menuClicks()
        .throttleFirst(windowDuration)
        .onEach { onMenuClick.invoke(it) }
        .launchIn(uiScope)
}