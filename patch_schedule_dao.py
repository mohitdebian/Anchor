import re

file_path = "app/src/main/java/com/example/data/database/ScheduleDao.kt"
with open(file_path, "r") as f:
    content = f.read()

content = content.replace('import androidx.room.Insert', 'import androidx.room.Insert\nimport androidx.room.Delete')
content = content.replace('suspend fun insertSchedule(schedule: Schedule)\n}', 'suspend fun insertSchedule(schedule: Schedule)\n\n    @Delete\n    suspend fun deleteSchedule(schedule: Schedule)\n}')

with open(file_path, "w") as f:
    f.write(content)
