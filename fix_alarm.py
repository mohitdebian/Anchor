import re

file_path = "app/src/main/java/com/example/AlarmScheduler.kt"
with open(file_path, "r") as f:
    content = f.read()

content = content.replace("""        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timestamp, pendingIntent)
        }
    fun cancelAlarm(context: Context, requestCode: Int) {""", """        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timestamp, pendingIntent)
        }
    }

    fun cancelAlarm(context: Context, requestCode: Int) {""")

with open(file_path, "w") as f:
    f.write(content)
