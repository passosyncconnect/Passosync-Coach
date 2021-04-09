package com.passosync.socialnetwork.repositories

import com.pasosync.pasosynccoach.other.Resource


inline fun <T> safeCall(action: () -> Resource<T>): Resource<T> {
    return try {
        action()
    } catch(e: Exception) {
        Resource.Error(e.message ?: "An unknown error occured")
    }
}