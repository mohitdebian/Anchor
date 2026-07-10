import re

file_path = "app/src/main/java/com/example/viewmodels/AppViewModelProvider.kt"
content = """package com.example.viewmodels

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.AnchorApplication
import com.example.ui.screens.block.BlockedOverlayViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            DashboardViewModel(
                anchorApplication().container.focusRepository,
                anchorApplication().container.usageStatsRepository,
                anchorApplication().container.userRepository,
                anchorApplication().container.scheduleRepository
            )
        }
        initializer {
            AnalyticsViewModel(
                anchorApplication().container.focusRepository,
                anchorApplication().container.usageStatsRepository
            )
        }
        initializer {
            InsightsViewModel(
                anchorApplication().container.nvidiaNimApi,
                anchorApplication().container.focusRepository,
                anchorApplication().container.usageStatsRepository
            )
        }
        initializer {
            FocusActiveViewModel(
                anchorApplication().container.focusRepository
            )
        }
        initializer {
            com.example.ui.screens.planner.PlannerViewModel(
                anchorApplication(),
                anchorApplication().container.scheduleRepository
            )
        }
        initializer {
            BlockedOverlayViewModel(
                anchorApplication(),
                anchorApplication().container.nvidiaNimApi,
                anchorApplication().container.usageStatsRepository
            )
        }
    }
}

fun CreationExtras.anchorApplication(): AnchorApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AnchorApplication)
"""

with open(file_path, "w") as f:
    f.write(content)
