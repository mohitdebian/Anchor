with open("app/src/main/java/com/example/MainActivity.kt", "r") as f:
    content = f.read()

if "import androidx.compose.runtime.getValue" not in content:
    content = content.replace("import androidx.compose.runtime.Composable", "import androidx.compose.runtime.Composable\nimport androidx.compose.runtime.getValue")

with open("app/src/main/java/com/example/MainActivity.kt", "w") as f:
    f.write(content)
