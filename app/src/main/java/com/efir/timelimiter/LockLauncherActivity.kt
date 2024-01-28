package com.efir.timelimiter

import OverlayLockService
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity

class LockLauncherActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_CODE = 1
    }

    private val handler = Handler(Looper.getMainLooper())

    private val checkLockStateRunnable = object : Runnable {
        override fun run() {
            // Проверяем состояние блокировки и запускаем нужную Activity
            checkAndLaunchAlt()

            // Запланировать следующий вызов через 1 секунду (1000 миллисекунд)
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock_launcher)

        checkAndLaunch()

        val lockIntent = Intent(this, LockService::class.java)
        startService(lockIntent)

        if (!Settings.canDrawOverlays(this)) {
            val intentOverlayPermission =
                Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName"))
            startActivityForResult(intentOverlayPermission, REQUEST_CODE)
        } else {
            startService(Intent(this@LockLauncherActivity, OverlayLockService::class.java))
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        handler.removeCallbacks(checkLockStateRunnable)
    }

    override fun onResume() {
        super.onResume()

        checkAndLaunch()
    }

    override fun onBackPressed() {
        // Перехватываем нажатие кнопки "Назад", чтобы оставаться на текущем экране.
    }

    private fun checkAndLaunch() {
        val prefs = getSharedPreferences(LockScreenActivity.PREFS_NAME, MODE_PRIVATE)
        val lockTime = prefs.getLong(LockScreenActivity.LOCK_TIME_KEY, 0)

        if (System.currentTimeMillis() < lockTime) {
            launchDefaultHome()
        } else if (!isCurrentActivity(LockScreenActivity::class.java)) {
            startActivity(Intent(this, LockScreenActivity::class.java))
            finish()
        }
    }

    private fun checkAndLaunchAlt() {
        val prefs = getSharedPreferences(LockScreenActivity.PREFS_NAME, MODE_PRIVATE)
        val lockTime = prefs.getLong(LockScreenActivity.LOCK_TIME_KEY, 0)

        if (System.currentTimeMillis() > lockTime && !isCurrentActivity(LockScreenActivity::class.java)) {
            startActivity(Intent(this, LockScreenActivity::class.java))
            finish()
        }
    }

    // Функция для определения является ли текущая активность экраном блокировки или нет.
    private fun isCurrentActivity(activityClass: Class<*>): Boolean {
        return activityClass.isInstance(this)
    }

    // Функция для определения является ли наше приложение домашним по умолчанию.
    private fun isDefaultHome(): Boolean {
        val intent = Intent(Intent.ACTION_MAIN).apply{
            addCategory(Intent.CATEGORY_HOME);
            setPackage(packageName);
        }

        val resolveInfoList = packageManager.queryIntentActivities(intent,
            PackageManager.MATCH_DEFAULT_ONLY);

        for (resolveInfo in resolveInfoList) {
            if(resolveInfo.activityInfo.packageName == packageName){
                return true;
            }
        }

        return false;
    }

    private fun launchDefaultHome() {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        // Получаем список всех доступных активностей с категорией HOME и выбираем стандартный Launcher,
        // который не является нашим LockLauncherActivity.

        val resolveInfoList = packageManager.queryIntentActivities(intent, 0)

        for (resolveInfo in resolveInfoList) {
            if (resolveInfo.activityInfo.packageName != packageName) {
                intent.component = ComponentName(
                    resolveInfo.activityInfo.packageName,
                    resolveInfo.activityInfo.name
                )

                break;
            }
        }

        startActivity(intent)

    }
}
