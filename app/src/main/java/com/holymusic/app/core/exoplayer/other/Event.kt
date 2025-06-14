package com.holymusic.app.core.exoplayer.other

open class Event<out T>(private val data: T) {

    var hasBeenHandled = false
        private set

    fun getContentIfNotHandled(): T? {
        return if(hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            data
        }
    }

    fun peekContent() = data
}