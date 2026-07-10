import re

file_path = "app/src/main/java/com/example/viewmodels/DashboardViewModel.kt"
with open(file_path, "r") as f:
    content = f.read()

content = content.replace(
    'val todayStr = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Calendar.getInstance().time)',
    'val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)'
)

with open(file_path, "w") as f:
    f.write(content)
