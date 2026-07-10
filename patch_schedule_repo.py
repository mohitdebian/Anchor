import re

file_path = "app/src/main/java/com/example/data/repository/ScheduleRepository.kt"
with open(file_path, "r") as f:
    content = f.read()

content = content.replace('suspend fun insertSchedule(schedule: Schedule) {\n        scheduleDao.insertSchedule(schedule)\n    }\n}', 'suspend fun insertSchedule(schedule: Schedule) {\n        scheduleDao.insertSchedule(schedule)\n    }\n\n    suspend fun deleteSchedule(schedule: Schedule) {\n        scheduleDao.deleteSchedule(schedule)\n    }\n}')

with open(file_path, "w") as f:
    f.write(content)
