import re

file_path = "app/src/main/java/com/example/navigation/AnchorNavigation.kt"
with open(file_path, "r") as f:
    content = f.read()

imports = """
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
"""

if "import androidx.compose.runtime.remember" not in content:
    content = content.replace(
        "import androidx.compose.runtime.Composable",
        "import androidx.compose.runtime.Composable" + imports
    )

content = content.replace(
    'packageName = currentBlockedApp!!,',
    'packageName = currentBlockedApp ?: "",'
)

# Fix the brace syntax error at the end
# The file currently ends with:
#         }
#     }
#     }
# }
# Let's clean up the end of the file by stripping the trailing braces and adding exactly what we need
content = re.sub(r'\}\s*\}\s*\}\s*\}\s*$', '        }\n    }\n}', content)

with open(file_path, "w") as f:
    f.write(content)
