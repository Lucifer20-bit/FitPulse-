package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String, // Chest, Back, Legs, Shoulders, Arms, Core, Cardio
    val primaryMuscle: String,
    val defaultRestSeconds: Int = 90,
    val isCustom: Boolean = false
)

@Entity(tableName = "routines")
data class Routine(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String, // Push/Pull/Legs, Upper/Lower, Full Body, Cardio, Custom
    val description: String = "",
    val estimatedDurationMinutes: Int = 45,
    val isTemplate: Boolean = false
)

@Entity(
    tableName = "routine_exercises",
    indices = [
        androidx.room.Index("routineId"),
        androidx.room.Index("exerciseId")
    ]
)
data class RoutineExercise(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val routineId: Long,
    val exerciseId: Long,
    val orderIndex: Int,
    val targetSets: Int = 3,
    val targetReps: String = "8-12",
    val targetRestSeconds: Int = 90
)

@Entity(tableName = "workout_sessions")
data class WorkoutSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val routineId: Long? = null,
    val title: String,
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long? = null,
    val isCompleted: Boolean = false,
    val totalVolumeKg: Double = 0.0,
    val totalSets: Int = 0,
    val notes: String = ""
)

@Entity(
    tableName = "workout_sets",
    indices = [
        androidx.room.Index("workoutSessionId"),
        androidx.room.Index("exerciseId")
    ]
)
data class WorkoutSet(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val workoutSessionId: Long,
    val exerciseId: Long,
    val setNumber: Int,
    val weightKg: Double,
    val reps: Int,
    val isCompleted: Boolean = false,
    val isPr: Boolean = false,
    val setType: String = "Normal", // "Normal", "Warmup", "Drop", "Failure"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "personal_records",
    indices = [androidx.room.Index("exerciseId")]
)
data class PersonalRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val exerciseId: Long,
    val recordType: String, // "1RM", "MaxWeight", "MaxReps"
    val value: Double,
    val reps: Int,
    val date: Long = System.currentTimeMillis(),
    val workoutSessionId: Long = 0,
    val badgeTier: String = "Gold", // "Bronze", "Silver", "Gold", "Diamond"
    val previousValue: Double = 0.0
)

@Entity(tableName = "bodyweight_entries")
data class BodyweightEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Long = System.currentTimeMillis(),
    val weightKg: Double,
    val note: String = ""
)
