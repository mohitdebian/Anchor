import re

file_path = "app/src/main/java/com/example/viewmodels/InsightsViewModel.kt"
with open(file_path, "r") as f:
    content = f.read()

# Replace NIM imports
content = content.replace("import com.example.data.api.NimApiClient", "import com.example.data.api.GeminiRetrofitClient")
content = content.replace("import com.example.data.api.NimChatRequest", "import com.example.data.api.GeminiRequest")
content = content.replace("import com.example.data.api.NimMessage", "import com.example.data.api.GeminiContent\nimport com.example.data.api.GeminiPart\nimport com.example.data.api.GeminiConfig")
content = content.replace("private val api = NimApiClient.api", "private val api = GeminiRetrofitClient.api")
content = content.replace("BuildConfig.NVIDIA_NIM_API_KEY", "BuildConfig.GEMINI_API_KEY")

# Replace API call
replacement = """
                val response = api.generateContent(
                    apiKey = apiKey,
                    request = GeminiRequest(
                        contents = listOf(
                            GeminiContent(
                                parts = listOf(GeminiPart(text = prompt))
                            )
                        ),
                        generationConfig = GeminiConfig(responseMimeType = "application/json")
                    )
                )
                
                var messageContent = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "[]"
"""

content = re.sub(
    r'val response = api.getInsights\([\s\S]*?var messageContent = response.choices.firstOrNull\(\)\?.message\?.content \?: "\[\]"',
    replacement.strip(),
    content
)

with open(file_path, "w") as f:
    f.write(content)
