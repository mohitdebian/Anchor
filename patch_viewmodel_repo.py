import re

file_path = "app/src/main/java/com/example/ui/screens/block/BlockedOverlayViewModel.kt"
with open(file_path, "r") as f:
    content = f.read()

content = content.replace(
    'private val nvidiaNimApi: NvidiaNimApi\n) : AndroidViewModel(application) {',
    'private val nvidiaNimApi: NvidiaNimApi,\n    private val usageStatsRepository: com.example.data.repository.UsageStatsRepository\n) : AndroidViewModel(application) {'
)

content = content.replace(
    'val prompt = "The user is trying to open the app \'$appName\' which is currently blocked because they are supposed to be focusing. Give them a 1 sentence harsh, regretful, and sarcastic roast to make them reconsider their life choices and go back to work."',
    'val timeSpentMins = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {\n                        usageStatsRepository.getTopDistractingApps(50).find { it.appName == appName }?.timeInForegroundMinutes ?: 0\n                    }\n                    val prompt = "The user is trying to open the app \'$appName\' which is currently blocked. They have already wasted $timeSpentMins minutes on it today. Give them a 1 sentence harsh, regretful, and sarcastic roast using this time spent to make them reconsider their life choices and go back to work."'
)

with open(file_path, "w") as f:
    f.write(content)
