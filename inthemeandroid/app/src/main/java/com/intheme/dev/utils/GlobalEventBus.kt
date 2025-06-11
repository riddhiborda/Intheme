package com.intheme.dev.utils

import com.intheme.dev.data.model.EventModel

object GlobalEventBus {
    val event = SingleEventFlow<EventModel>()
}