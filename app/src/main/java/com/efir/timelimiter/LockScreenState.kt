package com.efir.timelimiter

object LockScreenState {
    @Volatile private var isLockScreenActive: Boolean = false

    fun setLockScreenActive(active: Boolean) {
        isLockScreenActive = active
    }

    fun isLockScreenActive(): Boolean {
        return isLockScreenActive
    }
}
