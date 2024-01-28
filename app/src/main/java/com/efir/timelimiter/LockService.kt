package com.efir.timelimiter

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log

class LockService : Service() {
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var checkLockStateRunnable: Runnable

    override fun onBind(intent: Intent?): IBinder? {
        // Этот сервис не предоставляет связывание, поэтому возвращаем null.
        return null
    }

    override fun onCreate() {
        super.onCreate()

        checkLockStateRunnable = object : Runnable {
            override fun run() {
                // Проверяем состояние блокировки и запускаем нужную Activity если требуется.
                checkAndLaunch()

                // Запланировать следующий вызов через 1 секунду (1000 миллисекунд).
                handler.postDelayed(this, 1000)
            }
        }

        handler.post(checkLockStateRunnable)
    }

    private fun checkAndLaunch() {
        val prefs =
            getSharedPreferences(LockScreenActivity.PREFS_NAME, MODE_PRIVATE)
        val lockTime = prefs.getLong(LockScreenActivity.LOCK_TIME_KEY, 0)

        Log.d(
            "LockService",
            "checkAndLaunch: locked=" + (System.currentTimeMillis() > lockTime)
                    + " LockScreenState.isLockScreenActive()=" + LockScreenState.isLockScreenActive()
        )

        if ((lockTime == 0L || System.currentTimeMillis() > lockTime) && !LockScreenState.isLockScreenActive()) {
            val lockIntent =
                Intent(this, LockScreenActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }

            Log.d("LockService", "startActivity(lockIntent)")
            startActivity(lockIntent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(checkLockStateRunnable) // Удаляем callbacks при уничтожении сервиса.
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        return START_STICKY // Сервис будет пересоздан после его уничтожения системой если памяти недостаточно.
    }

}
