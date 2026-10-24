package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MathRepository(private val mathDao: MathDao) {

    val studentFlow: Flow<StudentProfile?> = mathDao.getStudentFlow(1L).map { entity ->
        entity?.let {
            StudentProfile(
                id = it.id,
                name = it.name,
                gradeLevel = GradeLevel.values().find { g -> g.name == it.gradeLevelString } ?: GradeLevel.GRADE_2,
                email = it.email,
                avatarId = it.avatarId,
                equippedAccessory = it.equippedAccessory,
                mathCoins = it.mathCoins,
                totalStars = it.totalStars,
                streakDays = it.streakDays,
                isVip = it.isVip,
                isVerified = it.isVerified
            )
        }
    }

    val sessionsFlow: Flow<List<PracticeSession>> = mathDao.getSessionsForStudent(1L).map { list ->
        list.map {
            PracticeSession(
                id = it.id,
                studentId = it.studentId,
                lessonId = it.lessonId,
                lessonTitle = it.lessonTitle,
                category = it.category,
                score = it.score,
                totalQuestions = it.totalQuestions,
                starsEarned = it.starsEarned,
                coinsEarned = it.coinsEarned,
                accuracyPercent = it.accuracyPercent,
                durationSeconds = it.durationSeconds,
                timestamp = it.timestamp
            )
        }
    }

    val unlockedItemsFlow: Flow<List<String>> = mathDao.getUnlockedItemIds(1L)

    val latestPaymentFlow: Flow<SubscriptionPayment?> = mathDao.getPaymentsForStudent(1L).map { list ->
        list.firstOrNull()?.let {
            SubscriptionPayment(
                id = it.id,
                studentId = it.studentId,
                transactionId = it.transactionId,
                planTitle = it.planTitle,
                amountInr = it.amountInr,
                paymentMethod = it.paymentMethod,
                payeeName = it.payeeName,
                payeeAccount = it.payeeAccount,
                status = it.status,
                timestamp = it.timestamp,
                expiryTimestamp = it.expiryTimestamp
            )
        }
    }

    suspend fun ensureInitialData() {
        val existing = mathDao.getStudentById(1L)
        if (existing == null) {
            val initial = StudentEntity(
                id = 1L,
                name = "Leo",
                gradeLevelString = GradeLevel.GRADE_2.name,
                email = "leo.student@gmail.com",
                avatarId = "mascot_robot",
                equippedAccessory = "wizard_hat",
                mathCoins = 140,
                totalStars = 18,
                streakDays = 5,
                isVip = false,
                isVerified = true
            )
            mathDao.insertOrUpdateStudent(initial)

            // Pre-seed some default unlocked items
            mathDao.unlockItem(UnlockedItemEntity(studentId = 1L, itemId = "wizard_hat"))
            mathDao.unlockItem(UnlockedItemEntity(studentId = 1L, itemId = "nerd_glasses"))

            // Seed initial past practice sessions for richer parent dashboard charts
            val now = System.currentTimeMillis()
            mathDao.insertSession(
                SessionEntity(
                    studentId = 1L,
                    lessonId = "add_1",
                    lessonTitle = "Counting Juicy Apples",
                    category = LessonCategory.ADDITION.name,
                    score = 3,
                    totalQuestions = 3,
                    starsEarned = 3,
                    coinsEarned = 30,
                    accuracyPercent = 100,
                    durationSeconds = 140,
                    timestamp = now - 86400000L * 2
                )
            )
            mathDao.insertSession(
                SessionEntity(
                    studentId = 1L,
                    lessonId = "sub_1",
                    lessonTitle = "Taking Away Cookies",
                    category = LessonCategory.SUBTRACTION.name,
                    score = 2,
                    totalQuestions = 2,
                    starsEarned = 3,
                    coinsEarned = 25,
                    accuracyPercent = 100,
                    durationSeconds = 120,
                    timestamp = now - 86400000L
                )
            )
            mathDao.insertSession(
                SessionEntity(
                    studentId = 1L,
                    lessonId = "geo_1",
                    lessonTitle = "Sides, Corners & Symmetry",
                    category = LessonCategory.GEOMETRY.name,
                    score = 3,
                    totalQuestions = 3,
                    starsEarned = 3,
                    coinsEarned = 35,
                    accuracyPercent = 100,
                    durationSeconds = 180,
                    timestamp = now - 3600000L * 3
                )
            )
        }
    }

    suspend fun saveStudent(profile: StudentProfile) {
        val entity = StudentEntity(
            id = profile.id,
            name = profile.name,
            gradeLevelString = profile.gradeLevel.name,
            email = profile.email,
            avatarId = profile.avatarId,
            equippedAccessory = profile.equippedAccessory,
            mathCoins = profile.mathCoins,
            totalStars = profile.totalStars,
            streakDays = profile.streakDays,
            isVip = profile.isVip,
            isVerified = profile.isVerified
        )
        mathDao.insertOrUpdateStudent(entity)
    }

    suspend fun recordSessionCompletion(
        studentId: Long,
        lessonId: String,
        lessonTitle: String,
        category: String,
        score: Int,
        totalQuestions: Int,
        starsEarned: Int,
        coinsEarned: Int,
        durationSeconds: Int
    ) {
        val accuracy = if (totalQuestions > 0) (score * 100) / totalQuestions else 0
        mathDao.insertSession(
            SessionEntity(
                studentId = studentId,
                lessonId = lessonId,
                lessonTitle = lessonTitle,
                category = category,
                score = score,
                totalQuestions = totalQuestions,
                starsEarned = starsEarned,
                coinsEarned = coinsEarned,
                accuracyPercent = accuracy,
                durationSeconds = durationSeconds,
                timestamp = System.currentTimeMillis()
            )
        )

        // Update student coins and stars
        val student = mathDao.getStudentById(studentId)
        if (student != null) {
            val updatedCoins = student.mathCoins + coinsEarned
            val updatedStars = student.totalStars + starsEarned
            mathDao.updateStudentRewards(studentId, updatedCoins, updatedStars, student.streakDays)
        }
    }

    suspend fun buyAndEquipItem(studentId: Long, item: AvatarItem): Boolean {
        val student = mathDao.getStudentById(studentId) ?: return false
        if (student.mathCoins >= item.priceCoins) {
            val newBalance = student.mathCoins - item.priceCoins
            mathDao.updateStudentRewards(studentId, newBalance, student.totalStars, student.streakDays)
            mathDao.unlockItem(UnlockedItemEntity(studentId = studentId, itemId = item.id))
            mathDao.updateEquippedAccessory(studentId, item.id)
            return true
        }
        return false
    }

    suspend fun equipItem(studentId: Long, itemId: String) {
        mathDao.updateEquippedAccessory(studentId, itemId)
    }

    suspend fun setVipStatus(studentId: Long, isVip: Boolean) {
        mathDao.updateVipStatus(studentId, isVip)
    }

    suspend fun recordPayment(payment: SubscriptionPaymentEntity) {
        mathDao.insertPayment(payment)
        mathDao.updateVipStatus(payment.studentId, true)
    }

    suspend fun getLatestPayment(studentId: Long): SubscriptionPayment? {
        val it = mathDao.getLatestPayment(studentId) ?: return null
        return SubscriptionPayment(
            id = it.id,
            studentId = it.studentId,
            transactionId = it.transactionId,
            planTitle = it.planTitle,
            amountInr = it.amountInr,
            paymentMethod = it.paymentMethod,
            payeeName = it.payeeName,
            payeeAccount = it.payeeAccount,
            status = it.status,
            timestamp = it.timestamp,
            expiryTimestamp = it.expiryTimestamp
        )
    }

    suspend fun verifyEmail(studentId: Long) {
        mathDao.updateVerifiedStatus(studentId, true)
    }

    fun getAvailableAvatarItems(): List<AvatarItem> = listOf(
        AvatarItem("wizard_hat", "Wizard Hat", "HATS", "🧙‍♂️", 0, isUnlocked = true),
        AvatarItem("space_helmet", "Astro Helmet", "HATS", "👨‍🚀", 80),
        AvatarItem("royal_crown", "Golden Crown", "HATS", "👑", 150),
        AvatarItem("super_cape", "Hero Cape", "CAPES", "🦸‍♂️", 100),
        AvatarItem("nerd_glasses", "Smart Shades", "GLASSES", "🕶️", 0, isUnlocked = true),
        AvatarItem("math_trophy", "Golden Abacus", "BADGES", "🏆", 200),
        AvatarItem("rainbow_badge", "Rainbow Star", "BADGES", "🌈", 60),
        AvatarItem("rocket_jetpack", "Rocket Jetpack", "CAPES", "🚀", 220)
    )

    fun getMilestones(): List<Milestone> = listOf(
        Milestone(
            id = "m1",
            title = "Addition Fluency & Double Digits",
            category = "Arithmetic",
            description = "Master sums up to 100 with carry-over",
            target = 10,
            current = 10,
            isAchieved = true
        ),
        Milestone(
            id = "m2",
            title = "Spatial Reasoning & Polygons",
            category = "Geometry",
            description = "Identify 2D & 3D shapes with vertices",
            target = 5,
            current = 5,
            isAchieved = true
        ),
        Milestone(
            id = "m3",
            title = "Fraction Part-Whole Recognition",
            category = "Fractions",
            description = "Partition circles and pizzas into equal portions",
            target = 8,
            current = 6,
            isAchieved = false
        ),
        Milestone(
            id = "m4",
            title = "Multiplication Concept by Arrays",
            category = "Operations",
            description = "Represent groups using row × column grids",
            target = 10,
            current = 7,
            isAchieved = false
        ),
        Milestone(
            id = "m5",
            title = "Multi-step Word Problem Solving",
            category = "Problem Solving",
            description = "Translate real-world stories into math equations",
            target = 6,
            current = 4,
            isAchieved = false
        )
    )

    fun getSchoolUpdates(): List<SchoolUpdate> = listOf(
        SchoolUpdate(
            id = "u1",
            title = "🚀 Elementary Math Olympiad This Friday!",
            message = "All Grade 1-5 students are invited to the playful virtual Math Olympiad. Great prizes, certificates, and mascot badges to be won!",
            dateString = "Tomorrow, 10:00 AM",
            category = "School Event",
            urgency = UpdateUrgency.IMPORTANT
        ),
        SchoolUpdate(
            id = "u2",
            title = "📢 Scheduled Maintenance: Cloud Sync Window",
            message = "Brief cloud sync synchronization update on Saturday 2:00 AM. Offline mode will remain fully accessible without interruption.",
            dateString = "Saturday, Oct 24",
            category = "System Schedule",
            urgency = UpdateUrgency.NORMAL
        ),
        SchoolUpdate(
            id = "u3",
            title = "⚠️ Winter Weather Safety Alert: Early Release",
            message = "District weather advisory: School buses will depart 1 hour early today. Homework packets available in offline mode.",
            dateString = "Today, 1:15 PM",
            category = "Urgent Alert",
            urgency = UpdateUrgency.EMERGENCY
        )
    )
}
