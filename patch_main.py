import re

file_path = "app/src/main/java/com/example/MainActivity.kt"
with open(file_path, "r") as f:
    content = f.read()

content = content.replace(
    'startService(android.content.Intent(this, com.example.services.BlockService::class.java))',
    'try { startService(android.content.Intent(this, com.example.services.BlockService::class.java)) } catch (e: Exception) { e.printStackTrace() }'
)

with open(file_path, "w") as f:
    f.write(content)
