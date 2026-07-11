package com.example.ui.screens.planner

import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.viewmodels.AppViewModelProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class SelectableApp(val packageName: String, val name: String, var isSelected: Boolean = false)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScheduleScreen(
    dateStr: String = "",
    scheduleId: Int = -1,
    onNavigateBack: () -> Unit,
    viewModel: PlannerViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val context = LocalContext.current
    
    val existingSchedule = remember(scheduleId) {
        if (scheduleId != -1) viewModel.schedules.value.find { it.id == scheduleId } else null
    }

    var title by remember { mutableStateOf(existingSchedule?.title ?: "") }
    var startTimeStr by remember { mutableStateOf(existingSchedule?.startTime ?: "09:00 AM") }
    var endTimeStr by remember { mutableStateOf(existingSchedule?.endTime ?: "10:00 AM") }
    
    val initialDate = if (dateStr.isNotEmpty()) {

        try {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateStr) ?: Calendar.getInstance().time
        } catch (e: Exception) {
            Calendar.getInstance().time
        }
    } else {
        Calendar.getInstance().time
    }
    
    val targetDateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(initialDate)
    val displayDateStr = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(initialDate)
    
    var apps by remember { mutableStateOf(emptyList<SelectableApp>()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val pm = context.packageManager
            val intent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val resolveInfoList = pm.queryIntentActivities(intent, PackageManager.GET_META_DATA)
            val appList = mutableListOf<SelectableApp>()
            for (resolveInfo in resolveInfoList) {
                val packageName = resolveInfo.activityInfo.packageName
                if (packageName != context.packageName) {
                    val label = resolveInfo.loadLabel(pm).toString()
                    appList.add(SelectableApp(packageName, label))
                }
            }
            val uniqueApps = appList.distinctBy { it.packageName }.sortedBy { it.name }
            
            if (existingSchedule != null) {
                val blockedList = existingSchedule.blockedPackages.split(",")
                uniqueApps.forEach { app ->
                    if (blockedList.contains(app.packageName)) {
                        app.isSelected = true
                    }
                }
            }
            
            apps = uniqueApps
            isLoading = false
        }
    }
    
    fun showTimePicker(isStart: Boolean) {
        val cal = Calendar.getInstance()
        TimePickerDialog(
            context,
            { _, hour, minute ->
                val chosen = Calendar.getInstance()
                chosen.set(Calendar.HOUR_OF_DAY, hour)
                chosen.set(Calendar.MINUTE, minute)
                val str = SimpleDateFormat("h:mm a", Locale.getDefault()).format(chosen.time)
                if (isStart) startTimeStr = str else endTimeStr = str
            },
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            false
        ).show()
    }

    Scaffold(
        containerColor = Color(0xFF0F0F0F),
        topBar = {
            TopAppBar(
                title = { Text("New Session", color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = androidx.compose.material3.MaterialTheme.colorScheme.onBackground)
                    }
                },
                actions = {
                    TextButton(onClick = {
                        if (title.isNotBlank()) {
                            val selectedPackages = apps.filter { it.isSelected }.joinToString(",") { it.packageName }
                            viewModel.addSchedule(title, startTimeStr, endTimeStr, targetDateStr, selectedPackages, scheduleId)
                            onNavigateBack()
                        }
                    }) {
                        Text("Save", color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F0F0F))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    Text("Session Name", color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        placeholder = { Text("e.g. Deep Work, Reading", color = Color.DarkGray) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
                            focusedBorderColor = Color(0xFF10B981),
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                }
                
                item {
                    Text("Date", color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(androidx.compose.material3.MaterialTheme.colorScheme.surface)
                            .padding(16.dp)
                    ) {
                        Text(displayDateStr, color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground, fontSize = 16.sp)
                    }
                }
                
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Start Time", color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(androidx.compose.material3.MaterialTheme.colorScheme.surface)
                                    .clickable { showTimePicker(true) }
                                    .padding(16.dp)
                            ) {
                                Text(startTimeStr, color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground, fontSize = 16.sp)
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("End Time", color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(androidx.compose.material3.MaterialTheme.colorScheme.surface)
                                    .clickable { showTimePicker(false) }
                                    .padding(16.dp)
                            ) {
                                Text(endTimeStr, color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground, fontSize = 16.sp)
                            }
                        }
                    }
                }
                
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Select Apps to Block", color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("These apps will be blocked during this session.", color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (isLoading) {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color(0xFF10B981))
                        }
                    }
                }
                
                if (!isLoading) {
                    items(apps, key = { it.packageName }) { app ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (app.isSelected) Color(0xFF10B981).copy(alpha = 0.15f) else androidx.compose.material3.MaterialTheme.colorScheme.surface)
                                .border(1.dp, if (app.isSelected) Color(0xFF10B981).copy(alpha = 0.5f) else Color.Transparent, RoundedCornerShape(16.dp))
                                .clickable {
                                    apps = apps.map { if (it.packageName == app.packageName) it.copy(isSelected = !it.isSelected) else it }
                                }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AndroidView(
                                factory = { ctx ->
                                    ImageView(ctx).apply {
                                        scaleType = ImageView.ScaleType.FIT_CENTER
                                        try {
                                            setImageDrawable(ctx.packageManager.getApplicationIcon(app.packageName))
                                        } catch (e: Exception) {}
                                    }
                                },
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = app.name,
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                                fontSize = 16.sp,
                                modifier = Modifier.weight(1f)
                            )
                            if (app.isSelected) {
                                Icon(Icons.Default.Check, contentDescription = "Selected", tint = Color(0xFF10B981))
                            } else {
                                Box(modifier = Modifier.size(24.dp).border(2.dp, Color.Gray, CircleShape))
                            }
                        }
                    }
                }
                
                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}
