package com.example.ui.screens.planner

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.BottomNavigationBar
import com.example.viewmodels.AppViewModelProvider
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun PlannerScreen(
    onNavigateToFocus: () -> Unit,
    onNavigateToBlocks: () -> Unit,
    onNavigateToAddSchedule: (String) -> Unit,
    onNavigateToEditSchedule: (String, Int) -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToAIStats: () -> Unit,
    viewModel: PlannerViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val schedules by viewModel.schedules.collectAsState()
    
    
    // Generate next 7 days
    val dates = remember {
        val calendar = Calendar.getInstance()
        val list = mutableListOf<Date>()
        for (i in 0 until 7) {
            list.add(calendar.time)
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        list
    }
    
    var selectedDate by remember { mutableStateOf(dates.first()) }
    
    val dateFormatter = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }
    val dbDateFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val dayFormatter = remember { SimpleDateFormat("EEE", Locale.getDefault()) }
    val numFormatter = remember { SimpleDateFormat("d", Locale.getDefault()) }
    
    val selectedDateStr = dateFormatter.format(selectedDate)
    val dbDateStr = dbDateFormatter.format(selectedDate)
    val filteredSchedules = schedules.filter { it.date == dbDateStr }
    
    val context = LocalContext.current
    var hasNotificationPermission by remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mutableStateOf(
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            )
        } else {
            mutableStateOf(true)
        }
    }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasNotificationPermission = isGranted
        }
    )
    
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Scaffold(
        containerColor = Color(0xFF0F0F0F),
        bottomBar = {
            BottomNavigationBar(
                currentRoute = "planner",
                onNavigateToFocus = onNavigateToFocus,
                onNavigateToPlanner = { },
                onNavigateToBlocks = onNavigateToBlocks,
                onNavigateToAnalytics = onNavigateToAnalytics,
                onNavigateToAIStats = onNavigateToAIStats
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onNavigateToAddSchedule(dbDateStr) },
                containerColor = Color(0xFF10B981),
                contentColor = Color.White,
                shape = RoundedCornerShape(100.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Schedule")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Schedule", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Planner",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(dates) { date ->
                    val isSelected = date == selectedDate
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) Color(0xFF163321) else Color(0xFF1C1C1E))
                            .clickable { selectedDate = date }
                            .padding(horizontal = 16.dp, vertical = 16.dp)
                    ) {
                        Text(
                            text = dayFormatter.format(date), 
                            color = if (isSelected) Color(0xFF10B981) else Color.Gray, 
                            fontSize = 14.sp, 
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = numFormatter.format(date), 
                            color = if (isSelected) Color(0xFF10B981) else Color.White, 
                            fontSize = 20.sp, 
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Schedules for $selectedDateStr",
                color = Color.LightGray,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (filteredSchedules.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No schedules for this date.\nTap + to add one.",
                        color = Color.Gray,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 24.sp
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredSchedules) { schedule ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFF1A1A1A))
                                .height(IntrinsicSize.Min)
                        ) {
                            Box(modifier = Modifier.width(4.dp).fillMaxHeight().background(Color(android.graphics.Color.parseColor(schedule.colorHex))))
                            Row(modifier = Modifier.fillMaxWidth().padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(text = schedule.icon, fontSize = 32.sp)
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = schedule.title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "${schedule.startTime} - ${schedule.endTime}", color = Color.Gray, fontSize = 14.sp)
                                }
                                IconButton(onClick = { onNavigateToEditSchedule(schedule.date, schedule.id) }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.LightGray)
                                }
                                IconButton(onClick = { viewModel.deleteSchedule(schedule) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.7f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    }
