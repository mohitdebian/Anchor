import re

file_path = "app/src/main/java/com/example/ui/screens/planner/PlannerViewModel.kt"
with open(file_path, "r") as f:
    content = f.read()

content = content.replace("""        } catch (e: Exception) {
            e.printStackTrace()
            return 0L
        }
    fun deleteSchedule(schedule: Schedule) {
        viewModelScope.launch {
            scheduleRepository.deleteSchedule(schedule)
            AlarmScheduler.cancelAlarm(getApplication(), schedule.timestamp.hashCode())
        }
    }
}""", """        } catch (e: Exception) {
            e.printStackTrace()
            return 0L
        }
    }

    fun deleteSchedule(schedule: Schedule) {
        viewModelScope.launch {
            scheduleRepository.deleteSchedule(schedule)
            com.example.AlarmScheduler.cancelAlarm(getApplication(), schedule.timestamp.hashCode())
        }
    }
}""")

with open(file_path, "w") as f:
    f.write(content)
