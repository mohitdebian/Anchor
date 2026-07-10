import re

file_path = "app/src/main/java/com/example/ui/screens/dashboard/DashboardScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

# I will find 'LaunchedEffect(showTimerDialog) {' and replace until 'val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)'

old_part = content[content.find("LaunchedEffect(showTimerDialog) {"):content.find("val sheetState = rememberModalBottomSheetState")]

new_part = """LaunchedEffect(showTimerDialog) {
        if (showTimerDialog) {
            isLoadingApps = true
            withContext(Dispatchers.IO) {
                val pm = context.packageManager
                val intent = Intent(Intent.ACTION_MAIN, null).apply { addCategory(Intent.CATEGORY_LAUNCHER) }
                val resolveInfoList = pm.queryIntentActivities(intent, PackageManager.GET_META_DATA)
                val appList = mutableListOf<AppItem>()
                for (resolveInfo in resolveInfoList) {
                    val packageName = resolveInfo.activityInfo.packageName
                    if (packageName != context.packageName) {
                        val label = resolveInfo.loadLabel(pm).toString()
                        val isBlocked = sharedPreferences.getBoolean(packageName, false)
                        appList.add(AppItem(packageName, label, isBlocked))
                    }
                }
                manualApps = appList.distinctBy { it.packageName }.sortedBy { it.name }
                isLoadingApps = false
            }
        }
    }
    
    var showGoalDialog by remember { mutableStateOf(false) }
    var selectedGoalMinutes by remember(uiState.dailyGoalMinutes) { mutableStateOf(uiState.dailyGoalMinutes) }
    
    var isScreenVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isScreenVisible = true
    }
    
    val infiniteTransition = rememberInfiniteTransition(label = "pulseTransition")
    
    val streakPulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "streakPulseScale"
    )
    
    val buttonPulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "buttonPulseScale"
    )
    
    val card1Alpha by animateFloatAsState(
        targetValue = if (isScreenVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 600, delayMillis = 0, easing = FastOutSlowInEasing),
        label = "card1Alpha"
    )
    val card1OffsetY by animateDpAsState(
        targetValue = if (isScreenVisible) 0.dp else 24.dp,
        animationSpec = tween(durationMillis = 600, delayMillis = 0, easing = FastOutSlowInEasing),
        label = "card1OffsetY"
    )
    val card2Alpha by animateFloatAsState(
        targetValue = if (isScreenVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 600, delayMillis = 100, easing = FastOutSlowInEasing),
        label = "card2Alpha"
    )
    val card2OffsetY by animateDpAsState(
        targetValue = if (isScreenVisible) 0.dp else 24.dp,
        animationSpec = tween(durationMillis = 600, delayMillis = 100, easing = FastOutSlowInEasing),
        label = "card2OffsetY"
    )
    val card3Alpha by animateFloatAsState(
        targetValue = if (isScreenVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 600, delayMillis = 200, easing = FastOutSlowInEasing),
        label = "card3Alpha"
    )
    val card3OffsetY by animateDpAsState(
        targetValue = if (isScreenVisible) 0.dp else 24.dp,
        animationSpec = tween(durationMillis = 600, delayMillis = 200, easing = FastOutSlowInEasing),
        label = "card3OffsetY"
    )
    val card4Alpha by animateFloatAsState(
        targetValue = if (isScreenVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 600, delayMillis = 300, easing = FastOutSlowInEasing),
        label = "card4Alpha"
    )
    val card4OffsetY by animateDpAsState(
        targetValue = if (isScreenVisible) 0.dp else 24.dp,
        animationSpec = tween(durationMillis = 600, delayMillis = 300, easing = FastOutSlowInEasing),
        label = "card4OffsetY"
    )
    val card5Alpha by animateFloatAsState(
        targetValue = if (isScreenVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 600, delayMillis = 400, easing = FastOutSlowInEasing),
        label = "card5Alpha"
    )
    val card5OffsetY by animateDpAsState(
        targetValue = if (isScreenVisible) 0.dp else 24.dp,
        animationSpec = tween(durationMillis = 600, delayMillis = 400, easing = FastOutSlowInEasing),
        label = "card5OffsetY"
    )
    
    if (showGoalDialog) {
        AlertDialog(
            onDismissRequest = { showGoalDialog = false },
            title = { Text("Set Daily Goal") },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val h = selectedGoalMinutes / 60
                    val m = selectedGoalMinutes % 60
                    val goalText = if (h > 0) "${h}h ${m}m" else "${m}m"
                    Text(goalText, style = MaterialTheme.typography.headlineMedium)
                    Slider(
                        value = selectedGoalMinutes.toFloat(),
                        onValueChange = { selectedGoalMinutes = it.toInt() },
                        valueRange = 30f..480f,
                        steps = 14
                    )
                }
            },
            confirmButton = {
                GlowingButton(
                    onClick = {
                        showGoalDialog = false
                        viewModel.updateDailyGoal(selectedGoalMinutes)
                    },
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = Color(0xFF10B981),
                    contentColor = Color.White
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showGoalDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    """

content = content.replace(old_part, new_part)

with open(file_path, "w") as f:
    f.write(content)
