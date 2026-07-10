import re

file_path = "app/src/main/java/com/example/ui/screens/analytics/AnalyticsScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

# Add imports
imports_to_add = """
import androidx.compose.foundation.clickable
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.ui.platform.LocalContext
"""

if "import androidx.compose.foundation.clickable" not in content:
    content = content.replace("import androidx.compose.foundation.layout.*", "import androidx.compose.foundation.layout.*\n" + imports_to_add)

# Move LocalContext.current outside of clickable
# It is currently inside clickable
content = content.replace('val displayTime = if (!uiState.hasUsagePermission) "Needs Permission" else screenTimeStr',
                          'val displayTime = if (!uiState.hasUsagePermission) "Needs Permission" else screenTimeStr\n            val context = LocalContext.current')
content = content.replace('androidx.compose.ui.platform.LocalContext.current.startActivity(intent)', 'context.startActivity(intent)')

with open(file_path, "w") as f:
    f.write(content)
