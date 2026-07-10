import re

file_path = "app/src/main/java/com/example/viewmodels/AppViewModelProvider.kt"
with open(file_path, "r") as f:
    content = f.read()

import_statement = "import com.example.ui.screens.block.BlockedOverlayViewModel\n"
if "BlockedOverlayViewModel" not in content:
    content = content.replace(
        "import com.example.ui.screens.planner.PlannerViewModel",
        "import com.example.ui.screens.planner.PlannerViewModel\n" + import_statement
    )

factory_addition = """
        initializer {
            BlockedOverlayViewModel(
                anchorApplication(),
                anchorApplication().container.nvidiaNimApi
            )
        }
"""
if "BlockedOverlayViewModel(" not in content:
    content = content.replace(
        "        initializer {",
        factory_addition.strip() + "\n        initializer {"
    )

with open(file_path, "w") as f:
    f.write(content)
