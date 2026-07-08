package com.example.ui.screens.planner

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.BottomNavigationBar
import com.example.viewmodels.AppViewModelProvider
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PlannerScreen(
    onNavigateToFocus: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToAIStats: () -> Unit,
    viewModel: PlannerViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val schedules by viewModel.schedules.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    val currentDate = remember { SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date()) }

    Scaffold(
        containerColor = Color(0xFF0F0F0F),
        bottomBar = {
            BottomNavigationBar(
                currentRoute = "planner",
                onNavigateToFocus = onNavigateToFocus,
                onNavigateToPlanner = { },
                onNavigateToAnalytics = onNavigateToAnalytics,
                onNavigateToAIStats = onNavigateToAIStats
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
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
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val parts = currentDate.split(" ")
                val month = parts.getOrNull(0) ?: "Month"
                val year = parts.getOrNull(1) ?: "Year"

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(text = month, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = year, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                }
                
                Surface(color = Color(0xFF222222), shape = RoundedCornerShape(12.dp)) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = "Help", tint = Color.LightGray, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Help", color = Color.LightGray, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Calendars... (skipped for brevity, but I'll add a simplified version)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                val days = listOf("Mon" to "6", "Tue" to "7", "Wed" to "8", "Thu" to "9", "Fri" to "10", "Sat" to "11", "Sun" to "12")
                days.forEach { (day, date) ->
                    val isSelected = date == "8"
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) Color(0xFF163321) else Color.Transparent)
                            .padding(horizontal = 10.dp, vertical = 12.dp)
                    ) {
                        Text(text = day, color = if (isSelected) Color(0xFF10B981) else Color.Gray, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = date, color = if (isSelected) Color(0xFF10B981) else Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // List Schedules
            if (schedules.isEmpty()) {
                Text(
                    text = "No schedules yet. Add one to get started!",
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 48.dp)
                )
            } else {
                schedules.forEach { schedule ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF1A1A1A))
                            .height(IntrinsicSize.Min)
                    ) {
                        Box(modifier = Modifier.width(4.dp).fillMaxHeight().background(Color(android.graphics.Color.parseColor(schedule.colorHex))))
                        Row(modifier = Modifier.fillMaxWidth().padding(20.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = schedule.icon, fontSize = 32.sp)
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(text = schedule.title, color = Color.Gray, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "${schedule.startTime} - ${schedule.endTime}", color = Color.DarkGray, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var title by remember { mutableStateOf("") }
        var startTime by remember { mutableStateOf("") }
        var endTime by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Schedule") },
            text = {
                Column {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = startTime, onValueChange = { startTime = it }, label = { Text("Start Time (e.g. 8:00 PM)") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = endTime, onValueChange = { endTime = it }, label = { Text("End Time (e.g. 10:00 PM)") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (title.isNotBlank()) {
                        viewModel.addSchedule(title, startTime, endTime)
                        showAddDialog = false
                    }
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
