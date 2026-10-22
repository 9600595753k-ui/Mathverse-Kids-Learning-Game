package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MathDao {
    @Query("SELECT * FROM students WHERE id = :id LIMIT 1")
    fun getStudentFlow(id: Long): Flow<StudentEntity?>

    @Query("SELECT * FROM students WHERE id = :id LIMIT 1")
    suspend fun getStudentById(id: Long): StudentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStudent(student: StudentEntity): Long

    @Query("UPDATE students SET mathCoins = :coins, totalStars = :stars, streakDays = :streak WHERE id = :studentId")
    suspend fun updateStudentRewards(studentId: Long, coins: Int, stars: Int, streak: Int)

    @Query("UPDATE students SET isVip = :isVip WHERE id = :studentId")
    suspend fun updateVipStatus(studentId: Long, isVip: Boolean)

    @Query("UPDATE students SET equippedAccessory = :accessory WHERE id = :studentId")
    suspend fun updateEquippedAccessory(studentId: Long, accessory: String)

    @Query("UPDATE students SET isVerified = :isVerified WHERE id = :studentId")
    suspend fun updateVerifiedStatus(studentId: Long, isVerified: Boolean)

    // Practice Sessions
    @Insert
    suspend fun insertSession(session: SessionEntity): Long

    @Query("SELECT * FROM practice_sessions WHERE studentId = :studentId ORDER BY timestamp DESC")
    fun getSessionsForStudent(studentId: Long): Flow<List<SessionEntity>>

    @Query("SELECT * FROM practice_sessions WHERE studentId = :studentId ORDER BY timestamp DESC")
    suspend fun getSessionsListForStudent(studentId: Long): List<SessionEntity>

    // Unlocked store items
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun unlockItem(item: UnlockedItemEntity)

    @Query("SELECT itemId FROM unlocked_items WHERE studentId = :studentId")
    fun getUnlockedItemIds(studentId: Long): Flow<List<String>>

    @Query("SELECT itemId FROM unlocked_items WHERE studentId = :studentId")
    suspend fun getUnlockedItemIdsList(studentId: Long): List<String>
}
