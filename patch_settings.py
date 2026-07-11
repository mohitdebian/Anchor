with open("app/src/main/java/com/example/ui/screens/settings/SettingsScreen.kt", "r") as f:
    content = f.read()

settings_button = """@Composable
fun SettingsButton(title: String, subtitle: String, onClick: () -> Unit) {
    GlassCard(modifier = Modifier.fillMaxWidth().androidx.compose.foundation.clickable(onClick = onClick)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}"""

if "SettingsButton" not in content:
    content = content + "\n\n" + settings_button

random_notification_call = """
            val context = androidx.compose.ui.platform.LocalContext.current
            SettingsButton(
                title = "Send Random Notification",
                subtitle = "Test the notification anytime",
                onClick = {
                    val messages = listOf(
                        "Stay focused! You can do this.",
                        "Take a deep breath. Keep up the good work.",
                        "Every minute of focus counts towards your goals.",
                        "You are doing great. Stay on track!",
                        "Anchor your focus. Don't let distractions win."
                    )
                    val msg = messages.random()
                    val notification = androidx.core.app.NotificationCompat.Builder(context, "block_service")
                        .setContentTitle("Anchor")
                        .setContentText(msg)
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setPriority(androidx.core.app.NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true)
                        .build()
                    val manager = context.getSystemService(android.content.Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
                    manager.notify((System.currentTimeMillis() % 10000).toInt(), notification)
                }
            )
"""

if "Send Random Notification" not in content:
    content = content.replace("SettingsItem(title = \"About\", subtitle = \"Version 1.0.0\")", "SettingsItem(title = \"About\", subtitle = \"Version 1.0.0\")" + random_notification_call)

with open("app/src/main/java/com/example/ui/screens/settings/SettingsScreen.kt", "w") as f:
    f.write(content)
