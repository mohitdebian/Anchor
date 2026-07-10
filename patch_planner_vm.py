import re

file_path = "app/src/main/java/com/example/ui/screens/planner/PlannerViewModel.kt"
with open(file_path, "r") as f:
    content = f.read()

replacement = """
    fun deleteSchedule(schedule: Schedule) {
        viewModelScope.launch {
            scheduleRepository.deleteSchedule(schedule)
            AlarmScheduler.cancelAlarm(getApplication(), schedule.timestamp.hashCode())
        }
    }
}
"""

content = re.sub(r'\}\n\}$', replacement, content)

with open(file_path, "w") as f:
    f.write(content)
