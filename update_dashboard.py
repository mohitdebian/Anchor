import os

provider_file = "app/src/main/java/com/example/viewmodels/AppViewModelProvider.kt"
with open(provider_file, "r") as f:
    content = f.read()

content = content.replace(
    'anchorApplication().container.userRepository\n            )',
    'anchorApplication().container.userRepository,\n                anchorApplication().container.scheduleRepository\n            )'
)

with open(provider_file, "w") as f:
    f.write(content)


vm_file = "app/src/main/java/com/example/viewmodels/DashboardViewModel.kt"
with open(vm_file, "r") as f:
    content = f.read()

content = content.replace(
    'import com.example.data.models.FocusSession',
    'import com.example.data.models.FocusSession\nimport com.example.data.models.Schedule\nimport com.example.data.repository.ScheduleRepository\nimport java.text.SimpleDateFormat\nimport java.util.Locale'
)

content = content.replace(
    'val recentSessions: List<FocusSession> = emptyList(),',
    'val recentSessions: List<FocusSession> = emptyList(),\n    val todaySchedules: List<Schedule> = emptyList(),'
)

content = content.replace(
    'private val userRepository: UserRepository',
    'private val userRepository: UserRepository,\n    private val scheduleRepository: ScheduleRepository'
)

content = content.replace(
    'isLoading = false\n                )',
    'isLoading = false\n                )\n            }\n        }\n\n        viewModelScope.launch {\n            scheduleRepository.getAllSchedules().collectLatest { schedules ->\n                val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)\n                _uiState.value = _uiState.value.copy(\n                    todaySchedules = schedules.filter { it.date == todayStr }\n                )'
)

with open(vm_file, "w") as f:
    f.write(content)
