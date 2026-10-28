package com.example.data.repository

import com.example.data.local.PrepopulatedData
import com.example.data.local.WorkoutDao
import com.example.data.model.ActiveWorkoutExercise
import com.example.data.model.BodyweightEntry
import com.example.data.model.Exercise
import com.example.data.model.ExercisePrSummary
import com.example.data.model.MilestoneHistoryItem
import com.example.data.model.PersonalRecord
import com.example.data.model.Routine
import com.example.data.model.RoutineExercise
import com.example.data.model.RoutineExerciseDetail
import com.example.data.model.RoutineWithDetails
import com.example.data.model.VolumeBarStat
import com.example.data.model.WorkoutSession
import com.example.data.model.WorkoutSet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WorkoutRepository(private val workoutDao: WorkoutDao) {

    // --- Exercises ---
    val allExercises: Flow<List<Exercise>> = workoutDao.getAllExercises()

    suspend fun getExerciseById(id: Long): Exercise? = workoutDao.getExerciseById(id)

    suspend fun addCustomExercise(name: String, category: String, primaryMuscle: String, restSeconds: Int): Long {
        val exercise = Exercise(
            name = name.trim(),
            category = category,
            primaryMuscle = primaryMuscle.trim().ifEmpty { category },
            defaultRestSeconds = restSeconds,
            isCustom = true
        )
        return workoutDao.insertExercise(exercise)
    }

    // --- Routines ---
    val allRoutines: Flow<List<Routine>> = workoutDao.getAllRoutines()

    fun observeRoutineDetails(routineId: Long): Flow<RoutineWithDetails?> {
        return combine(
            allExercises,
            workoutDao.getRoutineExercises(routineId)
        ) { exercises, routineExercises ->
            val routine = workoutDao.getRoutineById(routineId) ?: return@combine null
            val exerciseMap = exercises.associateBy { it.id }
            val details = routineExercises.mapNotNull { re ->
                exerciseMap[re.exerciseId]?.let { ex ->
                    RoutineExerciseDetail(re, ex)
                }
            }
            RoutineWithDetails(routine, details)
        }
    }

    suspend fun createRoutine(
        name: String,
        category: String,
        description: String,
        exercisesWithSets: List<Triple<Exercise, Int, String>> // Exercise, sets, targetReps
    ): Long {
        val routineId = workoutDao.insertRoutine(
            Routine(
                name = name.trim(),
                category = category,
                description = description.trim(),
                estimatedDurationMinutes = (exercisesWithSets.size * 10).coerceAtLeast(30),
                isTemplate = false
            )
        )
        val routineExercises = exercisesWithSets.mapIndexed { index, triple ->
            RoutineExercise(
                routineId = routineId,
                exerciseId = triple.first.id,
                orderIndex = index,
                targetSets = triple.second,
                targetReps = triple.third,
                targetRestSeconds = triple.first.defaultRestSeconds
            )
        }
        workoutDao.insertRoutineExercises(routineExercises)
        return routineId
    }

    suspend fun deleteRoutine(routine: Routine) {
        workoutDao.deleteRoutineExercises(routine.id)
        workoutDao.deleteRoutine(routine)
    }

    // --- Workout Sessions & Sets ---
    val completedWorkouts: Flow<List<WorkoutSession>> = workoutDao.getCompletedWorkoutSessions()
    val activeWorkoutSession: Flow<WorkoutSession?> = workoutDao.getActiveWorkoutSession()

    fun observeActiveWorkoutExercises(sessionId: Long): Flow<List<ActiveWorkoutExercise>> {
        return combine(
            allExercises,
            workoutDao.getSetsForSession(sessionId)
        ) { exercises, sets ->
            val exerciseMap = exercises.associateBy { it.id }
            val setsByExercise = sets.groupBy { it.exerciseId }
            setsByExercise.mapNotNull { (exerciseId, exerciseSets) ->
                exerciseMap[exerciseId]?.let { ex ->
                    ActiveWorkoutExercise(
                        exercise = ex,
                        sets = exerciseSets.sortedBy { it.setNumber },
                        targetRestSeconds = ex.defaultRestSeconds
                    )
                }
            }
        }
    }

    suspend fun startRoutineWorkout(routineId: Long): Long {
        // Discard any previous uncompleted session
        workoutDao.getActiveWorkoutSessionSync()?.let { previous ->
            workoutDao.deleteSetsForSession(previous.id)
            workoutDao.deleteWorkoutSession(previous)
        }

        val routine = workoutDao.getRoutineById(routineId)
        val title = routine?.name ?: "Workout"
        val sessionId = workoutDao.insertWorkoutSession(
            WorkoutSession(
                routineId = routineId,
                title = title,
                startTime = System.currentTimeMillis(),
                isCompleted = false
            )
        )

        val routineExercises = workoutDao.getRoutineExercisesSync(routineId)
        val setsToInsert = mutableListOf<WorkoutSet>()
        for (re in routineExercises) {
            val lastSet = workoutDao.getAllSetsForExercise(re.exerciseId).first().firstOrNull()
            val defaultWeight = lastSet?.weightKg ?: 20.0
            val defaultReps = lastSet?.reps ?: 10
            for (setIndex in 1..re.targetSets) {
                setsToInsert.add(
                    WorkoutSet(
                        workoutSessionId = sessionId,
                        exerciseId = re.exerciseId,
                        setNumber = setIndex,
                        weightKg = defaultWeight,
                        reps = defaultReps,
                        isCompleted = false,
                        setType = "Normal"
                    )
                )
            }
        }
        if (setsToInsert.isNotEmpty()) {
            workoutDao.insertWorkoutSets(setsToInsert)
        }
        return sessionId
    }

    suspend fun startQuickWorkout(title: String = "Quick Workout"): Long {
        workoutDao.getActiveWorkoutSessionSync()?.let { previous ->
            workoutDao.deleteSetsForSession(previous.id)
            workoutDao.deleteWorkoutSession(previous)
        }
        return workoutDao.insertWorkoutSession(
            WorkoutSession(
                routineId = null,
                title = title,
                startTime = System.currentTimeMillis(),
                isCompleted = false
            )
        )
    }

    suspend fun addExerciseToActiveWorkout(sessionId: Long, exercise: Exercise) {
        val lastSets = workoutDao.getAllSetsForExercise(exercise.id).first()
        val defaultWeight = lastSets.firstOrNull()?.weightKg ?: 20.0
        val defaultReps = lastSets.firstOrNull()?.reps ?: 10
        val newSet = WorkoutSet(
            workoutSessionId = sessionId,
            exerciseId = exercise.id,
            setNumber = 1,
            weightKg = defaultWeight,
            reps = defaultReps,
            isCompleted = false,
            setType = "Normal"
        )
        workoutDao.insertWorkoutSet(newSet)
    }

    suspend fun addSetToExercise(sessionId: Long, exerciseId: Long) {
        val currentSets = workoutDao.getSetsForSessionSync(sessionId).filter { it.exerciseId == exerciseId }
        val nextNumber = currentSets.size + 1
        val lastSet = currentSets.lastOrNull()
        val weight = lastSet?.weightKg ?: 20.0
        val reps = lastSet?.reps ?: 10
        workoutDao.insertWorkoutSet(
            WorkoutSet(
                workoutSessionId = sessionId,
                exerciseId = exerciseId,
                setNumber = nextNumber,
                weightKg = weight,
                reps = reps,
                isCompleted = false,
                setType = "Normal"
            )
        )
    }

    suspend fun removeSet(set: WorkoutSet) {
        workoutDao.deleteWorkoutSet(set)
    }

    suspend fun updateSet(set: WorkoutSet) {
        workoutDao.updateWorkoutSet(set)
    }

    /**
     * Toggles set completion. When completed, checks for PRs and calculates estimated 1RM.
     * Returns PersonalRecord if a new record is established, otherwise null.
     */
    suspend fun toggleSetCompleted(set: WorkoutSet): PersonalRecord? {
        val nextCompleted = !set.isCompleted
        var awardedPr: PersonalRecord? = null

        if (nextCompleted && set.weightKg > 0 && set.reps > 0) {
            val estimated1RM = calculateEstimated1RM(set.weightKg, set.reps)
            val currentBestWeight = workoutDao.getBestPersonalRecord(set.exerciseId, "MaxWeight")
            val currentBest1RM = workoutDao.getBestPersonalRecord(set.exerciseId, "1RM")

            val isNewMaxWeight = currentBestWeight == null || set.weightKg > currentBestWeight.value
            val isNew1RM = currentBest1RM == null || estimated1RM > currentBest1RM.value

            if (isNewMaxWeight || isNew1RM) {
                val tier = when {
                    set.weightKg >= 140.0 || (currentBestWeight != null && set.weightKg >= currentBestWeight.value + 10.0) -> "Diamond"
                    set.weightKg >= 100.0 || (currentBestWeight != null && set.weightKg >= currentBestWeight.value + 5.0) -> "Gold"
                    set.weightKg >= 60.0 -> "Silver"
                    else -> "Bronze"
                }
                val pr = PersonalRecord(
                    exerciseId = set.exerciseId,
                    recordType = if (isNewMaxWeight) "MaxWeight" else "1RM",
                    value = if (isNewMaxWeight) set.weightKg else estimated1RM,
                    reps = set.reps,
                    date = System.currentTimeMillis(),
                    workoutSessionId = set.workoutSessionId,
                    badgeTier = tier,
                    previousValue = currentBestWeight?.value ?: currentBest1RM?.value ?: 0.0
                )
                workoutDao.insertPersonalRecord(pr)
                awardedPr = pr
            }
        }

        val updatedSet = set.copy(
            isCompleted = nextCompleted,
            isPr = awardedPr != null,
            timestamp = System.currentTimeMillis()
        )
        workoutDao.updateWorkoutSet(updatedSet)
        return awardedPr
    }

    suspend fun finishWorkout(session: WorkoutSession, notes: String = "") {
        val sets = workoutDao.getSetsForSessionSync(session.id).filter { it.isCompleted }
        val totalVolume = sets.sumOf { it.weightKg * it.reps }
        val totalCompletedSets = sets.size
        val updatedSession = session.copy(
            endTime = System.currentTimeMillis(),
            isCompleted = true,
            totalVolumeKg = totalVolume,
            totalSets = totalCompletedSets,
            notes = notes.trim()
        )
        workoutDao.updateWorkoutSession(updatedSession)
    }

    suspend fun discardWorkout(session: WorkoutSession) {
        workoutDao.deleteSetsForSession(session.id)
        workoutDao.deleteWorkoutSession(session)
    }

    // --- Personal Records & Trophy Badges ---
    val allPersonalRecords: Flow<List<PersonalRecord>> = workoutDao.getAllPersonalRecords()

    fun observeMilestoneHistory(): Flow<List<MilestoneHistoryItem>> {
        return combine(
            allExercises,
            workoutDao.getAllPersonalRecords()
        ) { exercises, prs ->
            val map = exercises.associateBy { it.id }
            prs.map { pr ->
                val ex = map[pr.exerciseId]
                MilestoneHistoryItem(
                    pr = pr,
                    exerciseName = ex?.name ?: "Exercise",
                    category = ex?.category ?: "Strength"
                )
            }
        }
    }

    fun observeExercisePrSummaries(): Flow<List<ExercisePrSummary>> {
        return combine(
            allExercises,
            workoutDao.getAllCompletedSets(),
            workoutDao.getAllPersonalRecords()
        ) { exercises, sets, prs ->
            val prsByExercise = prs.groupBy { it.exerciseId }
            val setsByExercise = sets.groupBy { it.exerciseId }

            exercises.mapNotNull { exercise ->
                val exerciseSets = setsByExercise[exercise.id] ?: return@mapNotNull null
                val bestWeightSet = exerciseSets.maxByOrNull { it.weightKg } ?: return@mapNotNull null
                val best1RM = exerciseSets.maxOfOrNull { calculateEstimated1RM(it.weightKg, it.reps) } ?: bestWeightSet.weightKg
                val maxReps = exerciseSets.maxOfOrNull { it.reps } ?: 0
                val exercisePrs = prsByExercise[exercise.id]
                val tier = exercisePrs?.firstOrNull()?.badgeTier ?: "Bronze"
                val latestDate = exerciseSets.maxOfOrNull { it.timestamp } ?: System.currentTimeMillis()

                ExercisePrSummary(
                    exercise = exercise,
                    best1RM = best1RM,
                    bestWeight = bestWeightSet.weightKg,
                    maxReps = maxReps,
                    bestWeightReps = bestWeightSet.reps,
                    latestDate = latestDate,
                    badgeTier = tier
                )
            }.sortedByDescending { it.bestWeight }
        }
    }

    // --- Bodyweight Tracking ---
    val allBodyweightEntries: Flow<List<BodyweightEntry>> = workoutDao.getAllBodyweightEntries()

    suspend fun logBodyweight(weightKg: Double, note: String) {
        workoutDao.insertBodyweightEntry(
            BodyweightEntry(
                date = System.currentTimeMillis(),
                weightKg = weightKg,
                note = note.trim()
            )
        )
    }

    suspend fun deleteBodyweight(entry: BodyweightEntry) {
        workoutDao.deleteBodyweightEntry(entry)
    }

    // --- Volume & Progression Stats ---
    fun observeVolumeProgression(): Flow<List<VolumeBarStat>> {
        return workoutDao.getCompletedWorkoutSessions().combine(workoutDao.getAllCompletedSets()) { sessions, sets ->
            val setsBySession = sets.groupBy { it.workoutSessionId }
            val sdf = SimpleDateFormat("MMM d", Locale.getDefault())
            sessions.takeLast(10).reversed().map { session ->
                val sessionSets = setsBySession[session.id] ?: emptyList()
                val vol = if (session.totalVolumeKg > 0) session.totalVolumeKg else sessionSets.sumOf { it.weightKg * it.reps }
                VolumeBarStat(
                    dayOrWeekLabel = sdf.format(Date(session.startTime)),
                    timestamp = session.startTime,
                    volumeKg = vol,
                    setsCount = if (session.totalSets > 0) session.totalSets else sessionSets.size
                )
            }
        }
    }

    // --- JSON Backup / Cloud Export ---
    suspend fun exportDataToJson(): String {
        val exercises = workoutDao.getAllExercises().first()
        val routines = workoutDao.getAllRoutines().first()
        val sessions = workoutDao.getCompletedWorkoutSessions().first()
        val sets = workoutDao.getAllCompletedSets().first()
        val prs = workoutDao.getAllPersonalRecords().first()
        val weights = workoutDao.getAllBodyweightEntries().first()

        val root = JSONObject()
        root.put("version", 1)
        root.put("appName", "FitPulse")
        root.put("exportTimestamp", System.currentTimeMillis())

        val exArray = JSONArray()
        for (e in exercises) {
            val obj = JSONObject()
            obj.put("id", e.id)
            obj.put("name", e.name)
            obj.put("category", e.category)
            obj.put("primaryMuscle", e.primaryMuscle)
            obj.put("isCustom", e.isCustom)
            exArray.put(obj)
        }
        root.put("exercises", exArray)

        val sessArray = JSONArray()
        for (s in sessions) {
            val obj = JSONObject()
            obj.put("id", s.id)
            obj.put("title", s.title)
            obj.put("startTime", s.startTime)
            obj.put("totalVolumeKg", s.totalVolumeKg)
            obj.put("totalSets", s.totalSets)
            sessArray.put(obj)
        }
        root.put("sessions", sessArray)

        val prArray = JSONArray()
        for (p in prs) {
            val obj = JSONObject()
            obj.put("exerciseId", p.exerciseId)
            obj.put("recordType", p.recordType)
            obj.put("value", p.value)
            obj.put("reps", p.reps)
            obj.put("badgeTier", p.badgeTier)
            obj.put("date", p.date)
            prArray.put(obj)
        }
        root.put("personalRecords", prArray)

        val bwArray = JSONArray()
        for (b in weights) {
            val obj = JSONObject()
            obj.put("weightKg", b.weightKg)
            obj.put("date", b.date)
            obj.put("note", b.note)
            bwArray.put(obj)
        }
        root.put("bodyweight", bwArray)

        return root.toString(2)
    }

    suspend fun restoreDefaultTemplateData() {
        workoutDao.insertExercises(PrepopulatedData.exercises)
        workoutDao.insertRoutines(PrepopulatedData.routines)
        workoutDao.insertRoutineExercises(PrepopulatedData.routineExercises)
        val (sessions, sets) = PrepopulatedData.createInitialWorkouts()
        for (session in sessions) {
            workoutDao.insertWorkoutSession(session)
        }
        workoutDao.insertWorkoutSets(sets)
        for (pr in PrepopulatedData.createInitialPersonalRecords()) {
            workoutDao.insertPersonalRecord(pr)
        }
        for (bw in PrepopulatedData.createInitialBodyweight()) {
            workoutDao.insertBodyweightEntry(bw)
        }
    }

    companion object {
        fun calculateEstimated1RM(weightKg: Double, reps: Int): Double {
            if (reps <= 1) return weightKg
            // Epley 1RM formula: weight * (1 + reps/30.0)
            val rm = weightKg * (1.0 + (reps / 30.0))
            return Math.round(rm * 10.0) / 10.0
        }
    }
}
