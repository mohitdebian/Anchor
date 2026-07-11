package com.example.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.data.database.AppDatabase
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BlockService : Service() {
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    private var isRunning = false

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val notification = NotificationCompat.Builder(this, "block_service")
            .setContentTitle("Anchor Focus Active")
            .setContentText("Monitoring for distracting apps...")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()
        try { startForeground(1, notification) } catch (e: Exception) { e.printStackTrace() }
        
        isRunning = true
        startMonitoring()
    }

    private fun startMonitoring() {
        val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val sharedPreferences = getSharedPreferences("blocked_apps", Context.MODE_PRIVATE)
        val db = AppDatabase.getDatabase(this)
        
        scope.launch {
            while (isRunning) {
                delay(1000) // check every second
                val time = System.currentTimeMillis()
                val events = usageStatsManager.queryEvents(time - 2000, time)
                var currentApp: String? = null
                val event = android.app.usage.UsageEvents.Event()
                while (events.hasNextEvent()) {
                    events.getNextEvent(event)
                    if (event.eventType == android.app.usage.UsageEvents.Event.MOVE_TO_FOREGROUND) {
                        currentApp = event.packageName
                    }
                }
                
if (currentApp != null && currentApp != packageName && !isHomeApp(currentApp)) {
                    val isTimerActive = sharedPreferences.getBoolean("is_timer_active", false)
                    var isBlocked = false
                    
                    val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)
                    val currentTime = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Calendar.getInstance().time)
                    
                    val schedules = db.scheduleDao().getAllSchedulesSync() 
                    val activeSchedule = schedules.find { 
                        it.date == todayStr && isTimeBetween(currentTime, it.startTime, it.endTime)
                    }
                    
                    if (activeSchedule != null) {
                        if (activeSchedule.blockedPackages.split(",").contains(currentApp)) {
                            isBlocked = true
                        }
                    } else if (isTimerActive) {
                        isBlocked = sharedPreferences.getBoolean(currentApp, false)
                    }
                    
                    if (isBlocked) {
                        if (android.provider.Settings.canDrawOverlays(this@BlockService)) {
                            val intent = Intent(this@BlockService, MainActivity::class.java).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                putExtra("blocked_app_package", currentApp)
                            }
                            startActivity(intent)
                        } else {
                            // Fallback if overlay permission is missing
                            val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                                addCategory(Intent.CATEGORY_HOME)
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            startActivity(homeIntent)
                        }
                        // Sleep a bit to prevent multiple intents
                        delay(2000)
                    }
                }
            }
        }
    }
    

        private fun sendRandomMotivation() {
        val messages = listOf(
            "Stay focused! You can do this.",
            "Take a deep breath. Keep up the good work.",
            "Every minute of focus counts towards your goals.",
            "You are doing great. Stay on track!",
            "Anchor your focus. Don't let distractions win."
        )
        val msg = messages.random()
        val notification = NotificationCompat.Builder(this, "block_service")
            .setContentTitle("Anchor")
            .setContentText(msg)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify((System.currentTimeMillis() % 10000).toInt(), notification)
    }

    private fun isHomeApp(packageName: String): Boolean {
        val intent = Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_HOME) }
        val resolveInfo = packageManager.resolveActivity(intent, android.content.pm.PackageManager.MATCH_DEFAULT_ONLY)
        return resolveInfo?.activityInfo?.packageName == packageName
    }

    private fun isTimeBetween(current: String, start: String, end: String): Boolean {
        try {
            val format = SimpleDateFormat("h:mm a", Locale.getDefault())
            val c = format.parse(current) ?: return false
            val s = format.parse(start) ?: return false
            val e = format.parse(end) ?: return false
            
            val endAdjusted = if (e.before(s)) Calendar.getInstance().apply { time = e; add(Calendar.DATE, 1) }.time else e
            val currAdjusted = if (c.before(s) && e.before(s)) Calendar.getInstance().apply { time = c; add(Calendar.DATE, 1) }.time else c
            
            return !currAdjusted.before(s) && !currAdjusted.after(endAdjusted)
        } catch (ex: Exception) {
            return false
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "block_service",
                "Focus Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            if (manager != null) {
                manager.createNotificationChannel(channel)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        job.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
