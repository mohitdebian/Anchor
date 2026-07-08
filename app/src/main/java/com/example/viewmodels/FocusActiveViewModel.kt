package com.example.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.models.FocusSession
import com.example.data.repository.FocusRepository
import kotlinx.coroutines.launch

class FocusActiveViewModel(private val focusRepository: FocusRepository) : ViewModel() {
    fun saveSession(durationMinutes: Int) {
        viewModelScope.launch {
            focusRepository.insertSession(FocusSession(durationMinutes = durationMinutes))
        }
    }
}
