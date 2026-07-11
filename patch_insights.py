import re

with open("app/src/main/java/com/example/ui/screens/insights/InsightsScreen.kt", "r") as f:
    content = f.read()

imports = """
import coil.compose.AsyncImage
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import android.os.Build
import androidx.compose.ui.draw.clip
"""
if "import coil.compose.AsyncImage" not in content:
    content = content.replace("import androidx.compose.runtime.remember", "import androidx.compose.runtime.remember\n" + imports)

target = """                Text(
                    text = "Powered by NVIDIA NIM ✨",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )"""

replacement = """                Text(
                    text = "Powered by NVIDIA NIM ✨",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                val aiGifs = listOf(
                    "https://media.giphy.com/media/26xBEamXwaMSUbV72/giphy.gif",
                    "https://media.giphy.com/media/3o7btPCcdNniyf0ArS/giphy.gif",
                    "https://media.giphy.com/media/l41lFw057lAJQMwg0/giphy.gif"
                )
                val randomAiGif = remember { aiGifs.random() }
                
                AsyncImage(
                    model = randomAiGif,
                    imageLoader = ImageLoader.Builder(LocalContext.current).components {
                        if (Build.VERSION.SDK_INT >= 28) {
                            add(ImageDecoderDecoder.Factory())
                        } else {
                            add(GifDecoder.Factory())
                        }
                    }.build(),
                    contentDescription = "AI GIF",
                    modifier = Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(16.dp))
                )"""

content = content.replace(target, replacement)

with open("app/src/main/java/com/example/ui/screens/insights/InsightsScreen.kt", "w") as f:
    f.write(content)
