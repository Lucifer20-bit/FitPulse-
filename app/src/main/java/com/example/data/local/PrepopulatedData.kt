package com.example.data.local

import com.example.data.model.BodyweightEntry
import com.example.data.model.Exercise
import com.example.data.model.PersonalRecord
import com.example.data.model.Routine
import com.example.data.model.RoutineExercise
import com.example.data.model.WorkoutSession
import com.example.data.model.WorkoutSet

object PrepopulatedData {
    val exercises = listOf(
        // Chest
        Exercise(id = 1, name = "Barbell Bench Press", category = "Chest", primaryMuscle = "Pectorals", defaultRestSeconds = 90),
        Exercise(id = 2, name = "Incline Dumbbell Press", category = "Chest", primaryMuscle = "Upper Chest", defaultRestSeconds = 90),
        Exercise(id = 3, name = "Chest Fly (Cable/DB)", category = "Chest", primaryMuscle = "Pectorals", defaultRestSeconds = 60),
        Exercise(id = 4, name = "Push-ups", category = "Chest", primaryMuscle = "Pectorals & Core", defaultRestSeconds = 60),

        // Back
        Exercise(id = 5, name = "Deadlift (Conventional)", category = "Back", primaryMuscle = "Full Posterior Chain", defaultRestSeconds = 120),
        Exercise(id = 6, name = "Barbell Bent-Over Row", category = "Back", primaryMuscle = "Lats & Rhomboids", defaultRestSeconds = 90),
        Exercise(id = 7, name = "Pull-ups / Chin-ups", category = "Back", primaryMuscle = "Lats & Biceps", defaultRestSeconds = 90),
        Exercise(id = 8, name = "Lat Pulldown", category = "Back", primaryMuscle = "Latissimus Dorsi", defaultRestSeconds = 60),
        Exercise(id = 9, name = "Seated Cable Row", category = "Back", primaryMuscle = "Mid Back", defaultRestSeconds = 60),

        // Legs
        Exercise(id = 10, name = "Barbell Back Squat", category = "Legs", primaryMuscle = "Quadriceps & Glutes", defaultRestSeconds = 120),
        Exercise(id = 11, name = "Romanian Deadlift (RDL)", category = "Legs", primaryMuscle = "Hamstrings & Glutes", defaultRestSeconds = 90),
        Exercise(id = 12, name = "Leg Press", category = "Legs", primaryMuscle = "Quadriceps", defaultRestSeconds = 90),
        Exercise(id = 13, name = "Bulgarian Split Squat", category = "Legs", primaryMuscle = "Quadriceps & Glutes", defaultRestSeconds = 75),
        Exercise(id = 14, name = "Standing Calf Raise", category = "Legs", primaryMuscle = "Calves", defaultRestSeconds = 45),

        // Shoulders
        Exercise(id = 15, name = "Overhead Barbell Press", category = "Shoulders", primaryMuscle = "Anterior Deltoids", defaultRestSeconds = 90),
        Exercise(id = 16, name = "Dumbbell Lateral Raise", category = "Shoulders", primaryMuscle = "Lateral Deltoids", defaultRestSeconds = 60),
        Exercise(id = 17, name = "Face Pulls", category = "Shoulders", primaryMuscle = "Rear Deltoids", defaultRestSeconds = 60),

        // Arms
        Exercise(id = 18, name = "Barbell Bicep Curl", category = "Arms", primaryMuscle = "Biceps Brachii", defaultRestSeconds = 60),
        Exercise(id = 19, name = "Cable Tricep Pushdown", category = "Arms", primaryMuscle = "Triceps Brachii", defaultRestSeconds = 60),
        Exercise(id = 20, name = "Hammer Curl", category = "Arms", primaryMuscle = "Brachialis & Forearms", defaultRestSeconds = 60),
        Exercise(id = 21, name = "Dips (Chest/Tricep)", category = "Arms", primaryMuscle = "Triceps & Lower Chest", defaultRestSeconds = 75),

        // Core
        Exercise(id = 22, name = "Hanging Leg Raise", category = "Core", primaryMuscle = "Abdominals", defaultRestSeconds = 60),
        Exercise(id = 23, name = "Plank Hold", category = "Core", primaryMuscle = "Core Stability", defaultRestSeconds = 60),
        Exercise(id = 24, name = "Cable Woodchoppers", category = "Core", primaryMuscle = "Obliques", defaultRestSeconds = 45),

        // Cardio
        Exercise(id = 25, name = "Treadmill Incline Run", category = "Cardio", primaryMuscle = "Cardiovascular", defaultRestSeconds = 45),
        Exercise(id = 26, name = "Rowing Machine Interval", category = "Cardio", primaryMuscle = "Full Body Cardio", defaultRestSeconds = 60),
        Exercise(id = 27, name = "Jump Rope Sprint", category = "Cardio", primaryMuscle = "Cardiovascular", defaultRestSeconds = 30)
    )

    val routines = listOf(
        Routine(
            id = 1,
            name = "Push Day",
            category = "Push/Pull/Legs",
            description = "Heavy chest, anterior delts, and triceps focus with compound presses",
            estimatedDurationMinutes = 50,
            isTemplate = true
        ),
        Routine(
            id = 2,
            name = "Pull Day",
            category = "Push/Pull/Legs",
            description = "Explosive deadlifts, vertical pulls, heavy rows, and arms",
            estimatedDurationMinutes = 55,
            isTemplate = true
        ),
        Routine(
            id = 3,
            name = "Leg Day",
            category = "Push/Pull/Legs",
            description = "Squat strength, hamstring RDLs, quad hypertrophy, and calves",
            estimatedDurationMinutes = 60,
            isTemplate = true
        ),
        Routine(
            id = 4,
            name = "Upper Body Power",
            category = "Upper/Lower",
            description = "Balanced horizontal and vertical push-pull strength routine",
            estimatedDurationMinutes = 50,
            isTemplate = true
        ),
        Routine(
            id = 5,
            name = "Lower Body Hypertrophy",
            category = "Upper/Lower",
            description = "High intensity leg volume builder with squats and leg presses",
            estimatedDurationMinutes = 55,
            isTemplate = true
        ),
        Routine(
            id = 6,
            name = "Full Body Blast",
            category = "Full Body",
            description = "Time-efficient workout hitting the three big compound lifts",
            estimatedDurationMinutes = 45,
            isTemplate = true
        ),
        Routine(
            id = 7,
            name = "Cardio & Core Ignition",
            category = "Cardio",
            description = "High-energy endurance conditioning paired with core stability",
            estimatedDurationMinutes = 35,
            isTemplate = true
        )
    )

    val routineExercises = listOf(
        // Push Day (id 1)
        RoutineExercise(id = 1, routineId = 1, exerciseId = 1, orderIndex = 0, targetSets = 4, targetReps = "6-8", targetRestSeconds = 90),
        RoutineExercise(id = 2, routineId = 1, exerciseId = 2, orderIndex = 1, targetSets = 3, targetReps = "8-10", targetRestSeconds = 90),
        RoutineExercise(id = 3, routineId = 1, exerciseId = 15, orderIndex = 2, targetSets = 3, targetReps = "8-10", targetRestSeconds = 90),
        RoutineExercise(id = 4, routineId = 1, exerciseId = 16, orderIndex = 3, targetSets = 4, targetReps = "12-15", targetRestSeconds = 60),
        RoutineExercise(id = 5, routineId = 1, exerciseId = 19, orderIndex = 4, targetSets = 3, targetReps = "10-12", targetRestSeconds = 60),

        // Pull Day (id 2)
        RoutineExercise(id = 6, routineId = 2, exerciseId = 5, orderIndex = 0, targetSets = 3, targetReps = "5", targetRestSeconds = 120),
        RoutineExercise(id = 7, routineId = 2, exerciseId = 7, orderIndex = 1, targetSets = 4, targetReps = "8-10", targetRestSeconds = 90),
        RoutineExercise(id = 8, routineId = 2, exerciseId = 6, orderIndex = 2, targetSets = 3, targetReps = "8-10", targetRestSeconds = 90),
        RoutineExercise(id = 9, routineId = 2, exerciseId = 18, orderIndex = 3, targetSets = 3, targetReps = "10-12", targetRestSeconds = 60),
        RoutineExercise(id = 10, routineId = 2, exerciseId = 17, orderIndex = 4, targetSets = 3, targetReps = "15", targetRestSeconds = 60),

        // Leg Day (id 3)
        RoutineExercise(id = 11, routineId = 3, exerciseId = 10, orderIndex = 0, targetSets = 4, targetReps = "6-8", targetRestSeconds = 120),
        RoutineExercise(id = 12, routineId = 3, exerciseId = 11, orderIndex = 1, targetSets = 3, targetReps = "8-10", targetRestSeconds = 90),
        RoutineExercise(id = 13, routineId = 3, exerciseId = 12, orderIndex = 2, targetSets = 3, targetReps = "10-12", targetRestSeconds = 90),
        RoutineExercise(id = 14, routineId = 3, exerciseId = 14, orderIndex = 3, targetSets = 4, targetReps = "15-20", targetRestSeconds = 45),

        // Upper Body Power (id 4)
        RoutineExercise(id = 15, routineId = 4, exerciseId = 1, orderIndex = 0, targetSets = 4, targetReps = "5", targetRestSeconds = 120),
        RoutineExercise(id = 16, routineId = 4, exerciseId = 6, orderIndex = 1, targetSets = 4, targetReps = "6-8", targetRestSeconds = 90),
        RoutineExercise(id = 17, routineId = 4, exerciseId = 15, orderIndex = 2, targetSets = 3, targetReps = "6-8", targetRestSeconds = 90),
        RoutineExercise(id = 18, routineId = 4, exerciseId = 7, orderIndex = 3, targetSets = 3, targetReps = "8", targetRestSeconds = 90),

        // Lower Body Hypertrophy (id 5)
        RoutineExercise(id = 19, routineId = 5, exerciseId = 10, orderIndex = 0, targetSets = 3, targetReps = "8-10", targetRestSeconds = 90),
        RoutineExercise(id = 20, routineId = 5, exerciseId = 13, orderIndex = 1, targetSets = 3, targetReps = "10-12", targetRestSeconds = 75),
        RoutineExercise(id = 21, routineId = 5, exerciseId = 11, orderIndex = 2, targetSets = 3, targetReps = "10", targetRestSeconds = 90),

        // Full Body Blast (id 6)
        RoutineExercise(id = 22, routineId = 6, exerciseId = 10, orderIndex = 0, targetSets = 3, targetReps = "6", targetRestSeconds = 90),
        RoutineExercise(id = 23, routineId = 6, exerciseId = 1, orderIndex = 1, targetSets = 3, targetReps = "6", targetRestSeconds = 90),
        RoutineExercise(id = 24, routineId = 6, exerciseId = 6, orderIndex = 2, targetSets = 3, targetReps = "8", targetRestSeconds = 90),

        // Cardio & Core (id 7)
        RoutineExercise(id = 25, routineId = 7, exerciseId = 25, orderIndex = 0, targetSets = 3, targetReps = "10 min", targetRestSeconds = 60),
        RoutineExercise(id = 26, routineId = 7, exerciseId = 22, orderIndex = 1, targetSets = 3, targetReps = "15", targetRestSeconds = 45),
        RoutineExercise(id = 27, routineId = 7, exerciseId = 23, orderIndex = 2, targetSets = 3, targetReps = "60s", targetRestSeconds = 45)
    )

    fun createInitialWorkouts(now: Long = System.currentTimeMillis()): Pair<List<WorkoutSession>, List<WorkoutSet>> {
        val oneDay = 86400000L
        val sessions = listOf(
            WorkoutSession(
                id = 101,
                routineId = 1,
                title = "Push Day",
                startTime = now - (6 * oneDay),
                endTime = now - (6 * oneDay) + (48 * 60 * 1000),
                isCompleted = true,
                totalVolumeKg = 4850.0,
                totalSets = 14,
                notes = "Solid session, felt strong on bench press"
            ),
            WorkoutSession(
                id = 102,
                routineId = 2,
                title = "Pull Day",
                startTime = now - (4 * oneDay),
                endTime = now - (4 * oneDay) + (52 * 60 * 1000),
                isCompleted = true,
                totalVolumeKg = 5620.0,
                totalSets = 13,
                notes = "Deadlift feeling crisp, new 1RM!"
            ),
            WorkoutSession(
                id = 103,
                routineId = 3,
                title = "Leg Day",
                startTime = now - (2 * oneDay),
                endTime = now - (2 * oneDay) + (56 * 60 * 1000),
                isCompleted = true,
                totalVolumeKg = 6900.0,
                totalSets = 14,
                notes = "Deep squats, good quad pump"
            )
        )

        val sets = listOf(
            // Push Day sets
            WorkoutSet(id = 1, workoutSessionId = 101, exerciseId = 1, setNumber = 1, weightKg = 80.0, reps = 8, isCompleted = true, isPr = false, timestamp = now - (6 * oneDay)),
            WorkoutSet(id = 2, workoutSessionId = 101, exerciseId = 1, setNumber = 2, weightKg = 90.0, reps = 6, isCompleted = true, isPr = false, timestamp = now - (6 * oneDay)),
            WorkoutSet(id = 3, workoutSessionId = 101, exerciseId = 1, setNumber = 3, weightKg = 100.0, reps = 5, isCompleted = true, isPr = true, timestamp = now - (6 * oneDay)),
            WorkoutSet(id = 4, workoutSessionId = 101, exerciseId = 2, setNumber = 1, weightKg = 28.0, reps = 10, isCompleted = true, isPr = false, timestamp = now - (6 * oneDay)),
            WorkoutSet(id = 5, workoutSessionId = 101, exerciseId = 2, setNumber = 2, weightKg = 30.0, reps = 8, isCompleted = true, isPr = true, timestamp = now - (6 * oneDay)),

            // Pull Day sets
            WorkoutSet(id = 6, workoutSessionId = 102, exerciseId = 5, setNumber = 1, weightKg = 120.0, reps = 5, isCompleted = true, isPr = false, timestamp = now - (4 * oneDay)),
            WorkoutSet(id = 7, workoutSessionId = 102, exerciseId = 5, setNumber = 2, weightKg = 140.0, reps = 5, isCompleted = true, isPr = false, timestamp = now - (4 * oneDay)),
            WorkoutSet(id = 8, workoutSessionId = 102, exerciseId = 5, setNumber = 3, weightKg = 160.0, reps = 3, isCompleted = true, isPr = true, timestamp = now - (4 * oneDay)),
            WorkoutSet(id = 9, workoutSessionId = 102, exerciseId = 6, setNumber = 1, weightKg = 70.0, reps = 8, isCompleted = true, isPr = false, timestamp = now - (4 * oneDay)),
            WorkoutSet(id = 10, workoutSessionId = 102, exerciseId = 6, setNumber = 2, weightKg = 80.0, reps = 6, isCompleted = true, isPr = true, timestamp = now - (4 * oneDay)),

            // Leg Day sets
            WorkoutSet(id = 11, workoutSessionId = 103, exerciseId = 10, setNumber = 1, weightKg = 100.0, reps = 8, isCompleted = true, isPr = false, timestamp = now - (2 * oneDay)),
            WorkoutSet(id = 12, workoutSessionId = 103, exerciseId = 10, setNumber = 2, weightKg = 120.0, reps = 6, isCompleted = true, isPr = false, timestamp = now - (2 * oneDay)),
            WorkoutSet(id = 13, workoutSessionId = 103, exerciseId = 10, setNumber = 3, weightKg = 130.0, reps = 5, isCompleted = true, isPr = true, timestamp = now - (2 * oneDay)),
            WorkoutSet(id = 14, workoutSessionId = 103, exerciseId = 11, setNumber = 1, weightKg = 90.0, reps = 10, isCompleted = true, isPr = true, timestamp = now - (2 * oneDay))
        )
        return Pair(sessions, sets)
    }

    fun createInitialPersonalRecords(now: Long = System.currentTimeMillis()): List<PersonalRecord> {
        val oneDay = 86400000L
        return listOf(
            PersonalRecord(id = 1, exerciseId = 5, recordType = "MaxWeight", value = 160.0, reps = 3, date = now - (4 * oneDay), workoutSessionId = 102, badgeTier = "Diamond", previousValue = 150.0),
            PersonalRecord(id = 2, exerciseId = 5, recordType = "1RM", value = 176.0, reps = 1, date = now - (4 * oneDay), workoutSessionId = 102, badgeTier = "Diamond", previousValue = 165.0),
            PersonalRecord(id = 3, exerciseId = 10, recordType = "MaxWeight", value = 130.0, reps = 5, date = now - (2 * oneDay), workoutSessionId = 103, badgeTier = "Gold", previousValue = 125.0),
            PersonalRecord(id = 4, exerciseId = 1, recordType = "MaxWeight", value = 100.0, reps = 5, date = now - (6 * oneDay), workoutSessionId = 101, badgeTier = "Gold", previousValue = 95.0),
            PersonalRecord(id = 5, exerciseId = 2, recordType = "MaxWeight", value = 30.0, reps = 8, date = now - (6 * oneDay), workoutSessionId = 101, badgeTier = "Silver", previousValue = 28.0),
            PersonalRecord(id = 6, exerciseId = 6, recordType = "MaxWeight", value = 80.0, reps = 6, date = now - (4 * oneDay), workoutSessionId = 102, badgeTier = "Silver", previousValue = 75.0),
            PersonalRecord(id = 7, exerciseId = 11, recordType = "MaxWeight", value = 90.0, reps = 10, date = now - (2 * oneDay), workoutSessionId = 103, badgeTier = "Bronze", previousValue = 85.0)
        )
    }

    fun createInitialBodyweight(now: Long = System.currentTimeMillis()): List<BodyweightEntry> {
        val oneDay = 86400000L
        return listOf(
            BodyweightEntry(id = 1, date = now - (28 * oneDay), weightKg = 79.8, note = "Morning fast"),
            BodyweightEntry(id = 2, date = now - (21 * oneDay), weightKg = 79.4, note = "Post workout"),
            BodyweightEntry(id = 3, date = now - (14 * oneDay), weightKg = 79.0, note = "Morning weigh-in"),
            BodyweightEntry(id = 4, date = now - (7 * oneDay), weightKg = 78.6, note = "Feeling leaner"),
            BodyweightEntry(id = 5, date = now - (3 * oneDay), weightKg = 78.2, note = "Morning check"),
            BodyweightEntry(id = 6, date = now, weightKg = 78.0, note = "Goal weight reached!")
        )
    }
}
