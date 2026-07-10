import re

file_path = "app/src/main/java/com/example/navigation/AnchorNavigation.kt"
with open(file_path, "r") as f:
    content = f.read()

content = content.replace(
    'startDestination = if (initialBlockedApp != null) "blocked_overlay?pkg=$initialBlockedApp" else "onboarding"',
    'startDestination = "onboarding"'
)

nav_host_replacement = """
    var currentBlockedApp by remember { mutableStateOf(initialBlockedApp) }
    
    if (currentBlockedApp != null) {
        com.example.ui.screens.block.BlockedOverlayScreen(
            packageName = currentBlockedApp!!,
            onGoHome = {
                currentBlockedApp = null
            }
        )
    } else {
        SharedTransitionLayout {
"""

content = content.replace(
    '    SharedTransitionLayout {',
    nav_host_replacement.strip() + '\n        SharedTransitionLayout {'
)

# Also we need to close the else block at the very end of the function
# The function ends with:
#             }
#         }
#     }
# }

content = content.replace(
    '        }\n    }\n}',
    '        }\n    }\n    }\n}'
)

with open(file_path, "w") as f:
    f.write(content)
