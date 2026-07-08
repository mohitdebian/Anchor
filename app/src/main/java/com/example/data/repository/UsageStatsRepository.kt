package com.example.data.repository

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Process
import android.provider.Settings
import java.util.Calendar

class UsageStatsRepository(private val context: Context) {

    fun hasUsageStatsPermission(): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.unsafeCheckOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    fun requestUsageStatsPermission(context: Context) {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    fun getTodayScreenTimeMinutes(): Int {
        if (!hasUsageStatsPermission()) return 0
        
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfDay = calendar.timeInMillis
        val now = System.currentTimeMillis()

        val usageStats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startOfDay,
            now
        )

        var totalForegroundTime = 0L
        for (stat in usageStats) {
            totalForegroundTime += stat.totalTimeInForeground
        }

        return (totalForegroundTime / 1000 / 60).toInt()
    }

    fun getTopDistractingApps(limit: Int = 3): List<AppUsageInfo> {
        if (!hasUsageStatsPermission()) return emptyList()
        
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val pm = context.packageManager
        
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfDay = calendar.timeInMillis
        val now = System.currentTimeMillis()
        
        val usageStats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startOfDay,
            now
        )
        
        val apps = mutableListOf<AppUsageInfo>()
        for (stat in usageStats) {
            if (stat.totalTimeInForeground > 0 && stat.packageName != context.packageName) {
                try {
                    val appInfo = pm.getApplicationInfo(stat.packageName, 0)
                    val isSystemApp = (appInfo.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0
                    if (!isSystemApp) {
                        val appName = pm.getApplicationLabel(appInfo).toString()
                        val minutes = (stat.totalTimeInForeground / 1000 / 60).toInt()
                        if (minutes > 0) {
                            apps.add(AppUsageInfo(stat.packageName, appName, minutes))
                        }
                    }
                } catch (e: Exception) {
                    // Ignore
                }
            }
        }
        
        return apps.sortedByDescending { it.timeInForegroundMinutes }.take(limit)
    }
}

data class AppUsageInfo(val packageName: String, val appName: String, val timeInForegroundMinutes: Int)
