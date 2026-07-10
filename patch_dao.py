import re

file_path = "app/src/main/java/com/example/data/database/ScheduleDao.kt"
with open(file_path, "r") as f:
    content = f.read()

content = content.replace(
    'fun getAllSchedules(): Flow<List<Schedule>>',
    'fun getAllSchedules(): Flow<List<Schedule>>\n\n    @Query("SELECT * FROM schedules ORDER BY timestamp ASC")\n    fun getAllSchedulesSync(): List<Schedule>'
)

with open(file_path, "w") as f:
    f.write(content)
