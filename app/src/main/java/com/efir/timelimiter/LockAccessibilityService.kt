package com.efir.timelimiter

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent

class LockAccessibilityService : AccessibilityService() {
    override fun onInterrupt() {
        // Обработка прерывания службы.
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val className = event.className.toString()
            LockScreenState.setLockScreenActive(className == LockScreenActivity::class.java.name)
        }
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        // Блокировка всех аппаратных кнопок кроме громкости
        return when (event.keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP, KeyEvent.KEYCODE_VOLUME_DOWN -> false  // Разрешаем регулировку громкости
            else -> true  // Все остальные кнопки блокируем
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()

        val info = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPES_ALL_MASK
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC

            flags =
                flags or AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS  // Добавляем этот флаг

            notificationTimeout = 100L
        }

        this.serviceInfo = info
    }
}
