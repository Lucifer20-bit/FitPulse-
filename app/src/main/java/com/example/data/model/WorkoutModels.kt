package com.example.data.model

data class RoutineWithDetails(
    val routine: Routine,
    val exercises: List<RoutineExerciseDetail>
)

data class RoutineExerciseDetail(
    val routineExercise: RoutineExercise,
    val exercise: Exercise
)

data class ActiveWorkoutExercise(
    val exercise: Exercise,
    val sets: List<WorkoutSet>,
    val targetRestSeconds: Int = 90
)

data class ExercisePrSummary(
    val exercise: Exercise,
    val best1RM: Double,
    val bestWeight: Double,
    val maxReps: Int,
    val bestWeightReps: Int,
    val latestDate: Long,
    val badgeTier: String
)

data class MilestoneHistoryItem(
    val pr: PersonalRecord,
    val exerciseName: String,
    val category: String
)

data class VolumeBarStat(
    val dayOrWeekLabel: String,
    val timestamp: Long,
    val volumeKg: Double,
    val setsCount: Int
)

data class ExerciseStrengthPoint(
    val timestamp: Long,
    val weightKg: Double,
    val reps: Int,
    val estimated1RM: Double
)
