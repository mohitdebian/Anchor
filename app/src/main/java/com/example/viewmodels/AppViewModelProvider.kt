package com.example.viewmodels

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.AnchorApplication

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            DashboardViewModel(
                anchorApplication().container.focusRepository,
                anchorApplication().container.usageStatsRepository,
                anchorApplication().container.userRepository
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
                anchorApplication().container.scheduleRepository
            )
        }
    }
}

fun CreationExtras.anchorApplication(): AnchorApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AnchorApplication)
