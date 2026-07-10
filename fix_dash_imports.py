file_path = "app/src/main/java/com/example/ui/screens/dashboard/DashboardScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

imports_to_add = """
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
"""

if "import androidx.compose.foundation.lazy.LazyColumn" not in content:
    content = content.replace("import androidx.compose.material3.*", "import androidx.compose.material3.*\n" + imports_to_add)

with open(file_path, "w") as f:
    f.write(content)
