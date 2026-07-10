import re

file_path = "app/src/main/java/com/example/AlarmScheduler.kt"
with open(file_path, "r") as f:
    content = f.read()

replacement = """
    fun cancelAlarm(context: Context, requestCode: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
"""

content = re.sub(r'\}\n\}$', replacement.strip() + "\n}", content)

with open(file_path, "w") as f:
    f.write(content)
