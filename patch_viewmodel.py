with open("app/src/main/java/com/example/viewmodels/DashboardViewModel.kt", "r") as f:
    content = f.read()

if "isAmbientPlaying: Boolean = false" not in content:
    content = content.replace("val activeDurationMinutes: Int = 0", "val activeDurationMinutes: Int = 0,\n    val isAmbientPlaying: Boolean = false")

if "fun toggleAmbientSound" not in content:
    content = content.replace("fun stopTimer() {", """fun toggleAmbientSound(soundManager: com.example.services.AmbientSoundManager) {
        if (soundManager.isPlaying()) {
            soundManager.stop()
            _uiState.value = _uiState.value.copy(isAmbientPlaying = false)
        } else {
            soundManager.play()
            _uiState.value = _uiState.value.copy(isAmbientPlaying = true)
        }
    }

    fun stopTimer() {""")
    content = content.replace("fun stopTimer(soundManager: com.example.services.AmbientSoundManager) {", """fun stopTimer(soundManager: com.example.services.AmbientSoundManager) {
        soundManager.stop()
        _uiState.value = _uiState.value.copy(isAmbientPlaying = false)""")

with open("app/src/main/java/com/example/viewmodels/DashboardViewModel.kt", "w") as f:
    f.write(content)
