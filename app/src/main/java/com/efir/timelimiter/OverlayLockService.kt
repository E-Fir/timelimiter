import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import com.efir.timelimiter.R

class OverlayLockService : Service() {
    private var overlayView: View? = null

    override fun onBind(intent: Intent): IBinder? {
        return null // Нет привязки к данной службе.
    }

    override fun onCreate() {
        super.onCreate()

        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        // Используйте LayoutInflater для получения вашего layout.
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        overlayView = inflater.inflate(R.layout.overlay_lock_screen, null)

        // Параметры для 'плавающего' окна.
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT).apply {
            gravity = Gravity.TOP or Gravity.START  // Расположение оверлея.
            width = WindowManager.LayoutParams.WRAP_CONTENT   // Ширина оверлея.
            height = WindowManager.LayoutParams.WRAP_CONTENT  // Высота оверлея.
        }

        windowManager.addView(overlayView, params)

        setupOverlayUI(overlayView!!)
    }

    private fun setupOverlayUI(view: View) {
        view.findViewById<View>(R.id.close_button).setOnClickListener { stopSelf() }
        // Установка действий UI элементов...
    }

    override fun onDestroy() {
        super.onDestroy()
        if (overlayView != null) {
            val windowManager =
                getSystemService(Context.WINDOW_SERVICE) as WindowManager?
            windowManager?.removeView(overlayView)
            overlayView = null;
        }
    }
}
