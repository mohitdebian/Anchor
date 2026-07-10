import re

file_path = "app/src/main/java/com/example/ui/screens/onboarding/OnboardingScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

# I will find the ValuePropStep and its closing braces.
start_str = "fun ValuePropStep(onNext: () -> Unit) {"
start_idx = content.find(start_str)

if start_idx != -1:
    # Find the end of this function
    brace_count = 0
    in_function = False
    end_idx = -1
    
    for i in range(start_idx, len(content)):
        if content[i] == '{':
            brace_count += 1
            in_function = True
        elif content[i] == '}':
            brace_count -= 1
        
        if in_function and brace_count == 0:
            end_idx = i
            break

    if end_idx != -1:
        # Check if we need any imports
        imports = """
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
"""
        
        new_func = """
@Composable
fun ValuePropStep(onNext: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "robot_transition")
    
    val floatAnim by infiniteTransition.animateFloat(
        initialValue = -15f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "robot_float"
    )
    
    val pulseAnim by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura_pulse"
    )
    
    val visorScanAnim by infiniteTransition.animateFloat(
        initialValue = -15f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "visor_scan"
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Schedule focus sessions to beat procrastination and stay consistent",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily.SansSerif,
            lineHeight = 32.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
        )
        
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            // Aura
            Box(
                modifier = Modifier
                    .size(250.dp)
                    .scale(pulseAnim)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0xFF10B981).copy(alpha = 0.3f), Color.Transparent)
                        ),
                        shape = CircleShape
                    )
            )
            
            // Robot Character Container
            Box(
                modifier = Modifier
                    .offset(y = floatAnim.dp)
                    .size(150.dp),
                contentAlignment = Alignment.Center
            ) {
                // Head
                Box(
                    modifier = Modifier
                        .offset(y = (-35).dp)
                        .size(80.dp, 70.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF222831))
                        .border(2.dp, Color(0xFF393E46), RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // Visor Track
                    Box(
                        modifier = Modifier
                            .size(50.dp, 16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF121212)),
                        contentAlignment = Alignment.Center
                    ) {
                        // Scanning Eye
                        Box(
                            modifier = Modifier
                                .offset(x = visorScanAnim.dp)
                                .size(16.dp, 8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981))
                        )
                    }
                    
                    // Antenna
                    Box(
                        modifier = Modifier
                            .offset(y = (-40).dp)
                            .size(6.dp, 15.dp)
                            .background(Color(0xFF393E46), RoundedCornerShape(3.dp))
                    )
                    Box(
                        modifier = Modifier
                            .offset(y = (-48).dp)
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(if (pulseAnim > 1.0f) Color(0xFF10B981) else Color(0xFFE1306C))
                    )
                }
                
                // Body
                Box(
                    modifier = Modifier
                        .offset(y = 30.dp)
                        .size(60.dp, 50.dp)
                        .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp, topStart = 12.dp, topEnd = 12.dp))
                        .background(Color(0xFF222831))
                        .border(2.dp, Color(0xFF393E46), RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp, topStart = 12.dp, topEnd = 12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // Chest core
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .scale(pulseAnim)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981).copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981))
                        )
                    }
                }
                
                // Left Arm
                Box(
                    modifier = Modifier
                        .offset(x = (-45).dp, y = 25.dp + (floatAnim * 0.5f).dp)
                        .size(16.dp, 45.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF222831))
                        .border(2.dp, Color(0xFF393E46), RoundedCornerShape(8.dp))
                )
                
                // Right Arm
                Box(
                    modifier = Modifier
                        .offset(x = 45.dp, y = 25.dp + (floatAnim * 0.5f).dp)
                        .size(16.dp, 45.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF222831))
                        .border(2.dp, Color(0xFF393E46), RoundedCornerShape(8.dp))
                )
            }
            
            // Meditating rings
            Canvas(modifier = Modifier.size(200.dp)) {
                drawCircle(
                    color = Color(0xFF10B981),
                    radius = size.width / 2 * pulseAnim,
                    style = Stroke(width = 2.dp.toPx(), pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(20f, 20f)))
                )
                drawCircle(
                    color = Color(0xFF10B981).copy(alpha = 0.5f),
                    radius = (size.width / 2 - 20.dp.toPx()) * (2f - pulseAnim),
                    style = Stroke(width = 1.dp.toPx())
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(100.dp)
        ) {
            Text("Schedule now!", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}
"""
        content = content[:start_idx-12] + new_func + content[end_idx+1:]
        # Add imports
        if "import androidx.compose.animation.core.*" not in content:
            content = content.replace("import androidx.compose.ui.unit.sp", "import androidx.compose.ui.unit.sp\n" + imports)

with open(file_path, "w") as f:
    f.write(content)
