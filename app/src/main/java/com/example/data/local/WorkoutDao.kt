package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.BodyweightEntry
import com.example.data.model.Exercise
import com.example.data.model.PersonalRecord
import com.example.data.model.Routine
import com.example.data.model.RoutineExercise
import com.example.data.model.WorkoutSession
import com.example.data.model.WorkoutSet
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    // Exercises
    @Query("SELECT * FROM exercises ORDER BY name ASC")
    fun getAllExercises(): Flow<List<Exercise>>

    @Query("SELECT * FROM exercises WHERE id = :id LIMIT 1")
    suspend fun getExerciseById(id: Long): Exercise?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: Exercise): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<Exercise>): List<Long>

    @Delete
    suspend fun deleteExercise(exercise: Exercise)

    // Routines
    @Query("SELECT * FROM routines ORDER BY isTemplate DESC, id ASC")
    fun getAllRoutines(): Flow<List<Routine>>

    @Query("SELECT * FROM routines WHERE id = :id LIMIT 1")
    suspend fun getRoutineById(id: Long): Routine?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(routine: Routine): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutines(routines: List<Routine>): List<Long>

    @Delete
    suspend fun deleteRoutine(routine: Routine)

    // Routine Exercises
    @Query("SELECT * FROM routine_exercises WHERE routineId = :routineId ORDER BY orderIndex ASC")
    fun getRoutineExercises(routineId: Long): Flow<List<RoutineExercise>>

    @Query("SELECT * FROM routine_exercises WHERE routineId = :routineId ORDER BY orderIndex ASC")
    suspend fun getRoutineExercisesSync(routineId: Long): List<RoutineExercise>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutineExercise(routineExercise: RoutineExercise): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutineExercises(items: List<RoutineExercise>): List<Long>

    @Query("DELETE FROM routine_exercises WHERE routineId = :routineId")
    suspend fun deleteRoutineExercises(routineId: Long)

    // Workout Sessions
    @Query("SELECT * FROM workout_sessions WHERE isCompleted = 1 ORDER BY startTime DESC")
    fun getCompletedWorkoutSessions(): Flow<List<WorkoutSession>>

    @Query("SELECT * FROM workout_sessions WHERE isCompleted = 0 LIMIT 1")
    fun getActiveWorkoutSession(): Flow<WorkoutSession?>

    @Query("SELECT * FROM workout_sessions WHERE isCompleted = 0 LIMIT 1")
    suspend fun getActiveWorkoutSessionSync(): WorkoutSession?

    @Query("SELECT * FROM workout_sessions WHERE id = :id LIMIT 1")
    suspend fun getWorkoutSessionById(id: Long): WorkoutSession?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutSession(session: WorkoutSession): Long

    @Update
    suspend fun updateWorkoutSession(session: WorkoutSession)

    @Delete
    suspend fun deleteWorkoutSession(session: WorkoutSession)

    // Workout Sets
    @Query("SELECT * FROM workout_sets WHERE workoutSessionId = :sessionId ORDER BY exerciseId ASC, setNumber ASC")
    fun getSetsForSession(sessionId: Long): Flow<List<WorkoutSet>>

    @Query("SELECT * FROM workout_sets WHERE workoutSessionId = :sessionId ORDER BY exerciseId ASC, setNumber ASC")
    suspend fun getSetsForSessionSync(sessionId: Long): List<WorkoutSet>

    @Query("SELECT * FROM workout_sets WHERE exerciseId = :exerciseId AND isCompleted = 1 ORDER BY timestamp ASC")
    fun getAllSetsForExercise(exerciseId: Long): Flow<List<WorkoutSet>>

    @Query("SELECT * FROM workout_sets WHERE isCompleted = 1 ORDER BY timestamp DESC")
    fun getAllCompletedSets(): Flow<List<WorkoutSet>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutSet(set: WorkoutSet): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutSets(sets: List<WorkoutSet>): List<Long>

    @Update
    suspend fun updateWorkoutSet(set: WorkoutSet)

    @Delete
    suspend fun deleteWorkoutSet(set: WorkoutSet)

    @Query("DELETE FROM workout_sets WHERE workoutSessionId = :sessionId")
    suspend fun deleteSetsForSession(sessionId: Long)

    // Personal Records
    @Query("SELECT * FROM personal_records ORDER BY date DESC")
    fun getAllPersonalRecords(): Flow<List<PersonalRecord>>

    @Query("SELECT * FROM personal_records WHERE exerciseId = :exerciseId ORDER BY value DESC")
    fun getPersonalRecordsForExercise(exerciseId: Long): Flow<List<PersonalRecord>>

    @Query("SELECT * FROM personal_records WHERE exerciseId = :exerciseId AND recordType = :recordType ORDER BY value DESC LIMIT 1")
    suspend fun getBestPersonalRecord(exerciseId: Long, recordType: String): PersonalRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPersonalRecord(pr: PersonalRecord): Long

    @Delete
    suspend fun deletePersonalRecord(pr: PersonalRecord)

    // Bodyweight Entries
    @Query("SELECT * FROM bodyweight_entries ORDER BY date DESC")
    fun getAllBodyweightEntries(): Flow<List<BodyweightEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBodyweightEntry(entry: BodyweightEntry): Long

    @Delete
    suspend fun deleteBodyweightEntry(entry: BodyweightEntry)
}
