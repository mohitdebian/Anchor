import re

file_path = "app/src/main/java/com/example/ui/screens/block/BlockedOverlayViewModel.kt"
with open(file_path, "r") as f:
    content = f.read()

content = content.replace("import com.example.data.api.NimChatRequest", "import com.example.data.api.GeminiRequest")
content = content.replace("import com.example.data.api.NimMessage", "import com.example.data.api.GeminiContent\nimport com.example.data.api.GeminiPart\nimport com.example.data.api.GeminiApiService")
content = content.replace("import com.example.data.api.NvidiaNimApi", "")
content = content.replace("private val nvidiaNimApi: NvidiaNimApi", "private val geminiApiService: GeminiApiService")
content = content.replace("BuildConfig.NVIDIA_NIM_API_KEY", "BuildConfig.GEMINI_API_KEY")

replacement = """
                    val response = geminiApiService.generateContent(
                        apiKey = apiKey,
                        request = GeminiRequest(
                            contents = listOf(
                                GeminiContent(
                                    parts = listOf(GeminiPart(text = prompt))
                                )
                            )
                        )
                    )
                    var messageContent = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: fallbackRoast(appName)
"""

content = re.sub(
    r'val response = nvidiaNimApi.getInsights\([\s\S]*?var messageContent = response.choices.firstOrNull\(\)\?.message\?.content \?: fallbackRoast\(appName\)',
    replacement.strip(),
    content
)

with open(file_path, "w") as f:
    f.write(content)
