with open("app/src/main/java/com/example/services/BlockService.kt", "r") as f:
    content = f.read()

send_random_func = """    private fun sendRandomMotivation() {
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
"""

if "private fun sendRandomMotivation()" not in content:
    content = content.replace("private fun isHomeApp", send_random_func + "\n    private fun isHomeApp")

# Insert the random trigger inside the while loop
# We'll put it right after delay(1000)
if "sendRandomMotivation()" not in content:
    content = content.replace("delay(1000) // check every second\n", "delay(1000) // check every second\n                if (Math.random() < 0.005) { sendRandomMotivation() }\n")

with open("app/src/main/java/com/example/services/BlockService.kt", "w") as f:
    f.write(content)
