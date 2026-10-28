package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundFeedbackManager
import com.example.data.local.AppDatabase
import com.example.data.model.ActiveWorkoutExercise
import com.example.data.model.BodyweightEntry
import com.example.data.model.Exercise
import com.example.data.model.ExercisePrSummary
import com.example.data.model.MilestoneHistoryItem
import com.example.data.model.PersonalRecord
import com.example.data.model.Routine
import com.example.data.model.VolumeBarStat
import com.example.data.model.WorkoutSession
import com.example.data.model.WorkoutSet
import com.example.data.repository.WorkoutRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MainTab {
    HOME,
    ROUTINES,
    PROGRESSION,
    TROPHIES,
    SETTINGS
}

class WorkoutViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    val repository = WorkoutRepository(database.workoutDao())
    val soundManager = SoundFeedbackManager(application)

    // Navigation and screen state
    private val _selectedTab = MutableStateFlow(MainTab.HOME)
    val selectedTab: StateFlow<MainTab> = _selectedTab.asStateFlow()

    private val _isWorkoutScreenOpen = MutableStateFlow(false)
    val isWorkoutScreenOpen: StateFlow<Boolean> = _isWorkoutScreenOpen.asStateFlow()

    // Weight Unit: false = kg, true = lbs
    private val _isLbsUnit = MutableStateFlow(false)
    val isLbsUnit: StateFlow<Boolean> = _isLbsUnit.asStateFlow()

    // Rest Stopwatch
    private val _restSecondsRemaining = MutableStateFlow(0)
    val restSecondsRemaining: StateFlow<Int> = _restSecondsRemaining.asStateFlow()

    private val _restTotalSeconds = MutableStateFlow(90)
    val restTotalSeconds: StateFlow<Int> = _restTotalSeconds.asStateFlow()

    private val _isRestTimerRunning = MutableStateFlow(false)
    val isRestTimerRunning: StateFlow<Boolean> = _isRestTimerRunning.asStateFlow()

    private var restTimerJob: Job? = null

    // PR Celebration event (shows confetti / trophy badge popup)
    private val _celebratingPr = MutableStateFlow<PersonalRecord?>(null)
    val celebratingPr: StateFlow<PersonalRecord?> = _celebratingPr.asStateFlow()

    // Active session duration counter
    private val _workoutDurationSeconds = MutableStateFlow(0)
    val workoutDurationSeconds: StateFlow<Int> = _workoutDurationSeconds.asStateFlow()
    private var workoutDurationJob: Job? = null

    // Rest Timer Auto-Start on set completion
    val autoStartRestTimer = MutableStateFlow(true)

    // Data streams
    val allExercises: StateFlow<List<Exercise>> = repository.allExercises
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRoutines: StateFlow<List<Routine>> = repository.allRoutines
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val completedWorkouts: StateFlow<List<WorkoutSession>> = repository.completedWorkouts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeSession: StateFlow<WorkoutSession?> = repository.activeWorkoutSession
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val activeWorkoutExercises: StateFlow<List<ActiveWorkoutExercise>> = activeSession
        .distinctUntilChanged { old, new -> old?.id == new?.id }
        .flatMapLatest { session ->
            if (session != null) repository.observeActiveWorkoutExercises(session.id)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val milestoneHistory: StateFlow<List<MilestoneHistoryItem>> = repository.observeMilestoneHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val prSummaries: StateFlow<List<ExercisePrSummary>> = repository.observeExercisePrSummaries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bodyweightEntries: StateFlow<List<BodyweightEntry>> = repository.allBodyweightEntries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val volumeProgression: StateFlow<List<VolumeBarStat>> = repository.observeVolumeProgression()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Observe active session to manage workout elapsed timer
        viewModelScope.launch {
            activeSession.collect { session ->
                if (session != null) {
                    startWorkoutDurationCounter(session.startTime)
                } else {
                    stopWorkoutDurationCounter()
                }
            }
        }
    }

    fun selectTab(tab: MainTab) {
        _selectedTab.value = tab
    }

    fun openWorkoutScreen() {
        _isWorkoutScreenOpen.value = true
    }

    fun closeWorkoutScreen() {
        _isWorkoutScreenOpen.value = false
    }

    fun toggleWeightUnit() {
        _isLbsUnit.value = !_isLbsUnit.value
    }

    fun dismissPrCelebration() {
        _celebratingPr.value = null
    }

    // Weight display helper (converts kg to displayed unit)
    fun displayWeight(weightKg: Double): String {
        return if (_isLbsUnit.value) {
            val lbs = weightKg * 2.20462
            String.format(java.util.Locale.US, "%.1f", lbs)
        } else {
            if (weightKg % 1.0 == 0.0) String.format(java.util.Locale.US, "%.0f", weightKg)
            else String.format(java.util.Locale.US, "%.1f", weightKg)
        }
    }

    fun weightUnitLabel(): String = if (_isLbsUnit.value) "lbs" else "kg"

    fun inputWeightToKg(inputWeight: Double): Double {
        return if (_isLbsUnit.value) inputWeight / 2.20462 else inputWeight
    }

    // --- In-Workout Actions ---
    fun startRoutine(routineId: Long) {
        viewModelScope.launch {
            repository.startRoutineWorkout(routineId)
            _isWorkoutScreenOpen.value = true
        }
    }

    fun startQuickWorkout(title: String = "Quick Workout") {
        viewModelScope.launch {
            repository.startQuickWorkout(title)
            _isWorkoutScreenOpen.value = true
        }
    }

    fun addExerciseToWorkout(exercise: Exercise) {
        val session = activeSession.value ?: return
        viewModelScope.launch {
            repository.addExerciseToActiveWorkout(session.id, exercise)
        }
    }

    fun addSet(exerciseId: Long) {
        val session = activeSession.value ?: return
        viewModelScope.launch {
            repository.addSetToExercise(session.id, exerciseId)
        }
    }

    fun removeSet(set: WorkoutSet) {
        viewModelScope.launch {
            repository.removeSet(set)
        }
    }

    fun incrementWeight(set: WorkoutSet, deltaKg: Double) {
        val convertedDelta = if (_isLbsUnit.value) deltaKg / 2.20462 else deltaKg
        val newWeight = (set.weightKg + convertedDelta).coerceAtLeast(0.0)
        val rounded = Math.round(newWeight * 10.0) / 10.0
        viewModelScope.launch {
            repository.updateSet(set.copy(weightKg = rounded))
        }
    }

    fun updateSetWeightDirect(set: WorkoutSet, weightInCurrentUnit: Double) {
        val weightKg = inputWeightToKg(weightInCurrentUnit).coerceAtLeast(0.0)
        val rounded = Math.round(weightKg * 10.0) / 10.0
        viewModelScope.launch {
            repository.updateSet(set.copy(weightKg = rounded))
        }
    }

    fun incrementReps(set: WorkoutSet, deltaReps: Int) {
        val newReps = (set.reps + deltaReps).coerceAtLeast(1)
        viewModelScope.launch {
            repository.updateSet(set.copy(reps = newReps))
        }
    }

    fun updateSetType(set: WorkoutSet, newType: String) {
        viewModelScope.launch {
            repository.updateSet(set.copy(setType = newType))
        }
    }

    fun toggleSetCompleted(set: WorkoutSet, defaultRestSec: Int = 90) {
        viewModelScope.launch {
            val awardedPr = repository.toggleSetCompleted(set)
            if (!set.isCompleted) {
                // If transitioning to completed:
                soundManager.playSetCompleteTap()
                if (awardedPr != null) {
                    soundManager.playPrCelebration()
                    _celebratingPr.value = awardedPr
                }
                if (autoStartRestTimer.value) {
                    startRestTimer(defaultRestSec)
                }
            }
        }
    }

    fun finishActiveWorkout(notes: String = "") {
        val session = activeSession.value ?: return
        viewModelScope.launch {
            repository.finishWorkout(session, notes)
            stopRestTimer()
            _isWorkoutScreenOpen.value = false
            soundManager.playRestCompleteChime()
        }
    }

    fun discardActiveWorkout() {
        val session = activeSession.value ?: return
        viewModelScope.launch {
            repository.discardWorkout(session)
            stopRestTimer()
            _isWorkoutScreenOpen.value = false
        }
    }

    // --- Rest Stopwatch Controller ---
    fun startRestTimer(seconds: Int) {
        restTimerJob?.cancel()
        _restTotalSeconds.value = seconds
        _restSecondsRemaining.value = seconds
        _isRestTimerRunning.value = true

        restTimerJob = viewModelScope.launch {
            while (_restSecondsRemaining.value > 0 && _isRestTimerRunning.value) {
                delay(1000L)
                val remaining = _restSecondsRemaining.value - 1
                _restSecondsRemaining.value = remaining

                if (remaining in 1..3) {
                    soundManager.playCountdownTick()
                } else if (remaining == 0) {
                    soundManager.playRestCompleteChime()
                    _isRestTimerRunning.value = false
                }
            }
        }
    }

    fun adjustRestSeconds(delta: Int) {
        val current = _restSecondsRemaining.value
        val newTime = (current + delta).coerceAtLeast(0)
        _restSecondsRemaining.value = newTime
        if (newTime > _restTotalSeconds.value) {
            _restTotalSeconds.value = newTime
        }
        if (newTime > 0 && !_isRestTimerRunning.value) {
            resumeRestTimer()
        }
    }

    fun pauseRestTimer() {
        _isRestTimerRunning.value = false
    }

    fun resumeRestTimer() {
        if (_restSecondsRemaining.value <= 0) return
        _isRestTimerRunning.value = true
        restTimerJob?.cancel()
        restTimerJob = viewModelScope.launch {
            while (_restSecondsRemaining.value > 0 && _isRestTimerRunning.value) {
                delay(1000L)
                val remaining = _restSecondsRemaining.value - 1
                _restSecondsRemaining.value = remaining
                if (remaining in 1..3) {
                    soundManager.playCountdownTick()
                } else if (remaining == 0) {
                    soundManager.playRestCompleteChime()
                    _isRestTimerRunning.value = false
                }
            }
        }
    }

    fun stopRestTimer() {
        restTimerJob?.cancel()
        _isRestTimerRunning.value = false
        _restSecondsRemaining.value = 0
    }

    // --- Session duration counter ---
    private fun startWorkoutDurationCounter(startTime: Long) {
        workoutDurationJob?.cancel()
        workoutDurationJob = viewModelScope.launch {
            while (true) {
                val elapsed = ((System.currentTimeMillis() - startTime) / 1000).toInt()
                _workoutDurationSeconds.value = elapsed.coerceAtLeast(0)
                delay(1000L)
            }
        }
    }

    private fun stopWorkoutDurationCounter() {
        workoutDurationJob?.cancel()
        _workoutDurationSeconds.value = 0
    }

    // --- Bodyweight Logging ---
    fun logBodyweight(weightInCurrentUnit: Double, note: String = "") {
        val kg = inputWeightToKg(weightInCurrentUnit)
        viewModelScope.launch {
            repository.logBodyweight(kg, note)
        }
    }

    fun deleteBodyweight(entry: BodyweightEntry) {
        viewModelScope.launch {
            repository.deleteBodyweight(entry)
        }
    }

    // --- Routine Creation ---
    fun createRoutine(
        name: String,
        category: String,
        description: String,
        exercises: List<Triple<Exercise, Int, String>>
    ) {
        viewModelScope.launch {
            repository.createRoutine(name, category, description, exercises)
        }
    }

    fun deleteRoutine(routine: Routine) {
        viewModelScope.launch {
            repository.deleteRoutine(routine)
        }
    }

    fun addCustomExercise(name: String, category: String, primaryMuscle: String, restSeconds: Int) {
        viewModelScope.launch {
            repository.addCustomExercise(name, category, primaryMuscle, restSeconds)
        }
    }

    fun restoreTemplates() {
        viewModelScope.launch {
            repository.restoreDefaultTemplateData()
        }
    }

    override fun onCleared() {
        super.onCleared()
        soundManager.release()
    }
}
