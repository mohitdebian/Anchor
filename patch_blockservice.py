import re

file_path = "app/src/main/java/com/example/services/BlockService.kt"
with open(file_path, "r") as f:
    content = f.read()

replacement = """
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
"""

content = re.sub(
    r'if \(currentApp != null && currentApp != packageName && !isHomeApp\(currentApp\)\) \{[\s\S]*?if \(isBlocked\) \{',
    replacement.strip() + " {",
    content
)

with open(file_path, "w") as f:
    f.write(content)
