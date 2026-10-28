package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.BodyweightEntry
import com.example.data.model.Exercise
import com.example.data.model.PersonalRecord
import com.example.data.model.Routine
import com.example.data.model.RoutineExercise
import com.example.data.model.WorkoutSession
import com.example.data.model.WorkoutSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Exercise::class,
        Routine::class,
        RoutineExercise::class,
        WorkoutSession::class,
        WorkoutSet::class,
        PersonalRecord::class,
        BodyweightEntry::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fitpulse_database"
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Populate initial template exercises and routines
                            CoroutineScope(Dispatchers.IO).launch {
                                val dao = getInstance(context).workoutDao()
                                dao.insertExercises(PrepopulatedData.exercises)
                                dao.insertRoutines(PrepopulatedData.routines)
                                dao.insertRoutineExercises(PrepopulatedData.routineExercises)

                                val (sessions, sets) = PrepopulatedData.createInitialWorkouts()
                                for (session in sessions) {
                                    dao.insertWorkoutSession(session)
                                }
                                dao.insertWorkoutSets(sets)

                                for (pr in PrepopulatedData.createInitialPersonalRecords()) {
                                    dao.insertPersonalRecord(pr)
                                }

                                for (bw in PrepopulatedData.createInitialBodyweight()) {
                                    dao.insertBodyweightEntry(bw)
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
