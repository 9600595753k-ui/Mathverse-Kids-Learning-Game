package com.example.model

enum class Language(val code: String, val displayName: String, val flagEmoji: String) {
    EN("en", "English", "🇺🇸"),
    ES("es", "Español", "🇪🇸"),
    FR("fr", "Français", "🇫🇷"),
    DE("de", "Deutsch", "🇩🇪"),
    HI("hi", "हिन्दी", "🇮🇳"),
    TA("ta", "தமிழ்", "🇮🇳")
}

enum class GradeLevel(val label: String, val ageGroup: String) {
    KINDERGARTEN("Kindergarten", "Age 4-5"),
    GRADE_1("1st Grade", "Age 6-7"),
    GRADE_2("2nd Grade", "Age 7-8"),
    GRADE_3("3rd Grade", "Age 8-9"),
    GRADE_4("4th Grade", "Age 9-10"),
    GRADE_5("5th Grade", "Age 10-11")
}

enum class LessonCategory(val id: String, val titleRes: String, val icon: String, val colorHex: Long) {
    ADDITION("addition", "Addition", "➕", 0xFF4F46E5),
    SUBTRACTION("subtraction", "Subtraction", "➖", 0xFF0284C7),
    MULTIPLICATION("multiplication", "Multiplication", "✖️", 0xFF8B5CF6),
    DIVISION("division", "Division", "➗", 0xFF10B981),
    GEOMETRY("geometry", "Geometry & Shapes", "🔷", 0xFFF59E0B),
    FRACTIONS("fractions", "Fractions & Pizza", "🍕", 0xFFF43F5E),
    WORD_PROBLEMS("word_problems", "Story Problems", "🧩", 0xFF0D9488),
    TELLING_TIME("time", "Telling Time & Clocks", "⏰", 0xFF0284C7),
    MONEY_MATH("money", "Money & Coins", "💰", 0xFFD97706),
    MEASUREMENT("measurement", "Measurement & Scales", "📏", 0xFF059669),
    PATTERNS("patterns", "Patterns & Sequences", "🔢", 0xFF7C3AED),
    SPEED_SPRINT("speed_sprint", "Speed Math Sprint", "⚡", 0xFFEC4899)
}

enum class VisualType {
    NONE,
    COUNTER_DOTS,
    PIZZA_FRACTION,
    SHAPE_GEOMETRY,
    NUMBER_LINE,
    ARRAY_GRID,
    WORD_STORY,
    ANALOG_CLOCK,
    MONEY_COINS,
    BALANCE_SCALE,
    PATTERN_TRAIN
}

data class VisualData(
    val count1: Int = 0,
    val count2: Int = 0,
    val itemEmoji: String = "🍎",
    val fractionNumerator: Int = 1,
    val fractionDenominator: Int = 4,
    val shapeType: String = "Triangle", // Triangle, Square, Pentagon, Hexagon, Circle, Cube
    val shapeSides: Int = 3,
    val numberLineStart: Int = 0,
    val numberLineEnd: Int = 10,
    val numberLineHop1: Int = 3,
    val numberLineHop2: Int = 4,
    val gridRows: Int = 3,
    val gridCols: Int = 4,
    val clockHour: Int = 3,
    val clockMinute: Int = 0,
    val coinsSummary: String = "2 Dimes + 1 Nickel",
    val coinsValues: List<Int> = listOf(10, 10, 5),
    val scaleLeftWeight: Int = 8,
    val scaleRightWeight: Int = 5,
    val patternSequence: List<String> = listOf("2", "4", "6", "?", "10")
)

data class MathQuestion(
    val id: String,
    val lessonId: String,
    val questionText: String,
    val visualType: VisualType = VisualType.NONE,
    val visualData: VisualData = VisualData(),
    val options: List<String>,
    val correctAnswer: String,
    val hint: String,
    val explanation: String
)

data class Lesson(
    val id: String,
    val category: LessonCategory,
    val title: String,
    val description: String,
    val gradeLevel: GradeLevel,
    val order: Int,
    val isPremium: Boolean,
    val questions: List<MathQuestion>
)

data class StudentProfile(
    val id: Long = 1L,
    val name: String = "Leo",
    val gradeLevel: GradeLevel = GradeLevel.GRADE_2,
    val email: String = "leo.student@example.com",
    val avatarId: String = "mascot_robot",
    val equippedAccessory: String = "wizard_hat",
    val mathCoins: Int = 120,
    val totalStars: Int = 24,
    val streakDays: Int = 5,
    val isVip: Boolean = false,
    val isVerified: Boolean = true
)

data class PracticeSession(
    val id: Long = 0L,
    val studentId: Long = 1L,
    val lessonId: String,
    val lessonTitle: String,
    val category: String,
    val score: Int,
    val totalQuestions: Int,
    val starsEarned: Int,
    val coinsEarned: Int,
    val accuracyPercent: Int,
    val durationSeconds: Int,
    val timestamp: Long = System.currentTimeMillis()
)

data class Milestone(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val target: Int,
    val current: Int,
    val isAchieved: Boolean
)

data class AvatarItem(
    val id: String,
    val name: String,
    val category: String, // "HATS", "GLASSES", "CAPES", "BADGES"
    val iconEmoji: String,
    val priceCoins: Int,
    val isUnlocked: Boolean = false
)

enum class UpdateUrgency { NORMAL, IMPORTANT, EMERGENCY }

data class SchoolUpdate(
    val id: String,
    val title: String,
    val message: String,
    val dateString: String,
    val category: String,
    val urgency: UpdateUrgency
)
