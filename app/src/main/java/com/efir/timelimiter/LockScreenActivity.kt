package com.efir.timelimiter

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LockScreenActivity : AppCompatActivity() {
    companion object {
        const val PREFS_NAME = "TimeLimiterPrefs"
        const val LOCK_TIME_KEY = "UnlockTimeKey"
        private const val REQUEST_CODE_ENABLE_ADMIN = 1

        const val HARD_CODED_PASSWORD = ""
    }

    private lateinit var passwordInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN or
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )

        setContentView(R.layout.activity_lock_screen)

        passwordInput = findViewById(R.id.passwordInput)

        val unlockButton = findViewById<Button>(R.id.unlockButton)
        unlockButton.setOnClickListener {
            onUnlockClicked()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        LockScreenState.setLockScreenActive(false)
    }

    private fun onUnlockClicked() {
        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
            putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, ComponentName(this@LockScreenActivity,
                LockDeviceAdminReceiver::class.java)
            )
            putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                getString(R.string.add_device_admin_explanation))
        }

        startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN)

        val password = passwordInput.text.toString()
        passwordInput.text.clear()

        if (password == HARD_CODED_PASSWORD) {
            val unlockAfterSeconds = 60 * 60
            val lockTime = System.currentTimeMillis() + unlockAfterSeconds * 1000
            saveLockTime(lockTime)

            finish()
        } else {
            Toast.makeText(this, "Неправильный пароль", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        return
    }

    private fun saveLockTime(lockTime: Long) {
        val sharedPref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        with(sharedPref.edit()) {
            putLong(LOCK_TIME_KEY, lockTime)
            apply()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode,resultCode,data)
        if (requestCode == REQUEST_CODE_ENABLE_ADMIN) {
            // Изучаем результат и принимаем соответствующее действие.
        }
    }

    override fun onResume() {
        super.onResume()

        LockScreenState.setLockScreenActive(true)
    }
}
