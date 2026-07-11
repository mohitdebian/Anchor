with open("app/src/main/java/com/example/viewmodels/DashboardViewModel.kt", "r") as f:
    content = f.read()

content = content.replace("fun stopTimer() {", "fun stopTimer(soundManager: com.example.services.AmbientSoundManager) {\n        soundManager.stop()\n        _uiState.value = _uiState.value.copy(isAmbientPlaying = false)\n")

with open("app/src/main/java/com/example/viewmodels/DashboardViewModel.kt", "w") as f:
    f.write(content)
