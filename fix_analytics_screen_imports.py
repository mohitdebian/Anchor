file_path = "app/src/main/java/com/example/ui/screens/analytics/AnalyticsScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

imports_to_add = """
import androidx.compose.foundation.clickable
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.ui.platform.LocalContext
"""

if "import androidx.compose.foundation.clickable" not in content:
    content = content.replace("package com.example.ui.screens.analytics", "package com.example.ui.screens.analytics\n" + imports_to_add)

with open(file_path, "w") as f:
    f.write(content)
