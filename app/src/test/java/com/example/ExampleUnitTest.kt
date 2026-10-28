package com.example

import com.example.data.repository.WorkoutRepository
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun epley1RM_calculation_isAccurate() {
        // 100kg for 1 rep = 100kg
        assertEquals(100.0, WorkoutRepository.calculateEstimated1RM(100.0, 1), 0.1)

        // 100kg for 10 reps = 100 * (1 + 10/30) = 133.3 kg
        assertEquals(133.3, WorkoutRepository.calculateEstimated1RM(100.0, 10), 0.1)

        // 80kg for 5 reps = 80 * (1 + 5/30) = 93.3 kg
        assertEquals(93.3, WorkoutRepository.calculateEstimated1RM(80.0, 5), 0.1)
    }
}
