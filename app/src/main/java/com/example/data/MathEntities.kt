package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 1L,
    val name: String,
    val gradeLevelString: String,
    val email: String,
    val avatarId: String,
    val equippedAccessory: String,
    val mathCoins: Int,
    val totalStars: Int,
    val streakDays: Int,
    val isVip: Boolean,
    val isVerified: Boolean
)

@Entity(tableName = "practice_sessions")
data class SessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val studentId: Long,
    val lessonId: String,
    val lessonTitle: String,
    val category: String,
    val score: Int,
    val totalQuestions: Int,
    val starsEarned: Int,
    val coinsEarned: Int,
    val accuracyPercent: Int,
    val durationSeconds: Int,
    val timestamp: Long
)

@Entity(tableName = "unlocked_items")
data class UnlockedItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val studentId: Long,
    val itemId: String
)
