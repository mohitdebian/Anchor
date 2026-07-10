import re

file_path = "app/src/main/java/com/example/AppContainer.kt"
with open(file_path, "r") as f:
    content = f.read()

content = content.replace("import com.example.data.api.NvidiaNimApi", "import com.example.data.api.GeminiApiService\nimport com.example.data.api.GeminiRetrofitClient")
content = content.replace("val nvidiaNimApi: NvidiaNimApi", "val geminiApiService: GeminiApiService")
content = content.replace("override val nvidiaNimApi: NvidiaNimApi", "override val geminiApiService: GeminiApiService")

replacement = """
    override val geminiApiService: GeminiApiService by lazy {
        GeminiRetrofitClient.api
    }
"""

content = re.sub(r'override val geminiApiService: GeminiApiService by lazy \{[\s\S]*?\}', replacement.strip(), content)

with open(file_path, "w") as f:
    f.write(content)
