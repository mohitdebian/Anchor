import re

file_path = "app/src/main/java/com/example/viewmodels/InsightsViewModel.kt"
with open(file_path, "r") as f:
    content = f.read()

content = content.replace("import com.example.data.api.NvidiaNimApi", "import com.example.data.api.GeminiApiService")
content = content.replace("private val geminiApiService: NvidiaNimApi", "private val api: GeminiApiService")
content = content.replace("class InsightsViewModel(\n    private val nvidiaNimApi: NvidiaNimApi", "class InsightsViewModel(\n    private val api: GeminiApiService")

# if the first one failed:
content = content.replace("class InsightsViewModel(", "class InsightsViewModel(\n")
content = re.sub(r'class InsightsViewModel\([\s\S]*?private val api: GeminiApiService', 'class InsightsViewModel(\n    private val api: GeminiApiService', content)
content = re.sub(r'class InsightsViewModel\([\s\S]*?private val nvidiaNimApi: [a-zA-Z]+', 'class InsightsViewModel(\n    private val api: GeminiApiService', content)

# just specifically:
content = re.sub(r'private val nvidiaNimApi:\s*NvidiaNimApi', 'private val api: GeminiApiService', content)

with open(file_path, "w") as f:
    f.write(content)
