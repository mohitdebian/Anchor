import os

with open('app/src/main/java/com/example/data/database/AppDatabase.kt', 'r') as f:
    content = f.read()

content = content.replace('version = 3', 'version = 4')
content = content.replace('fallbackToDestructiveMigration(false)', 'fallbackToDestructiveMigration()')

with open('app/src/main/java/com/example/data/database/AppDatabase.kt', 'w') as f:
    f.write(content)

with open('app/src/main/java/com/example/data/models/Schedule.kt', 'r') as f:
    s_content = f.read()

s_content = s_content.replace('val title: String,', 'val title: String,\n    val date: String = "",\n    val timestamp: Long = 0L,')

with open('app/src/main/java/com/example/data/models/Schedule.kt', 'w') as f:
    f.write(s_content)
