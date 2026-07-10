import re

file_path = "app/src/main/java/com/example/ui/screens/planner/PlannerScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

alarm_code = """
                        viewModel.addSchedule(title, startTime, endTime, selectedDateStr)
                        showAddDialog = false
                        
                        // Schedule alarm
                        try {
                            val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
                            val timeObj = timeFormat.parse(startTime)
                            val cal = Calendar.getInstance()
                            cal.time = selectedDate
                            if (timeObj != null) {
                                val timeCal = Calendar.getInstance()
                                timeCal.time = timeObj
                                cal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY))
                                cal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE))
                                cal.set(Calendar.SECOND, 0)
                                
                                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                                val intent = Intent(context, com.example.AlarmReceiver::class.java).apply {
                                    putExtra("title", "Scheduled Focus: $title")
                                    putExtra("message", "It's time for your $startTime - $endTime focus block!")
                                }
                                val pendingIntent = PendingIntent.getBroadcast(
                                    context, 
                                    title.hashCode(), 
                                    intent, 
                                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                                )
                                alarmManager.setExactAndAllowWhileIdle(
                                    AlarmManager.RTC_WAKEUP,
                                    cal.timeInMillis,
                                    pendingIntent
                                )
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
"""

content = content.replace(
    'viewModel.addSchedule(title, startTime, endTime, selectedDateStr)\n                        showAddDialog = false\n                        // We would schedule an alarm here',
    alarm_code
)

with open(file_path, "w") as f:
    f.write(content)
