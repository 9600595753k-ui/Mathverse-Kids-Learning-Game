package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.MathDatabase
import com.example.data.MathLessonBank
import com.example.data.MathRepository
import com.example.data.PdfReportHelper
import com.example.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File

sealed class Screen {
    object Onboarding : Screen()
    object Register : Screen()
    object Home : Screen()
    data class LessonExercise(val lesson: Lesson) : Screen()
    object SpeedSprint : Screen()
    object GamesHub : Screen()
    object BalloonPopGame : Screen()
    object PizzaChefGame : Screen()
    object MemoryMatchGame : Screen()
    object BalanceScaleGame : Screen()
    object TimesTable : Screen()
    object Store : Screen()
    object ParentDashboard : Screen()
    object Subscription : Screen()
    object Updates : Screen()
    object Settings : Screen()
}

data class QuizState(
    val currentQuestionIndex: Int = 0,
    val selectedOption: String? = null,
    val isAnswerChecked: Boolean = false,
    val isCorrect: Boolean = false,
    val score: Int = 0,
    val totalCoinsEarned: Int = 0,
    val isFinished: Boolean = false,
    val startTimeMs: Long = System.currentTimeMillis()
)

data class SprintState(
    val timeLeftSeconds: Int = 60,
    val currentQuestion: MathQuestion? = null,
    val score: Int = 0,
    val streak: Int = 0,
    val isRunning: Boolean = false,
    val isFinished: Boolean = false
)

class MathViewModel(application: Application) : AndroidViewModel(application) {

    private val database = MathDatabase.getInstance(application)
    private val repository = MathRepository(database.mathDao())

    val student: StateFlow<StudentProfile> = repository.studentFlow
        .map { it ?: StudentProfile() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StudentProfile())

    val sessions: StateFlow<List<PracticeSession>> = repository.sessionsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unlockedItemIds: StateFlow<List<String>> = repository.unlockedItemsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf("wizard_hat", "nerd_glasses"))

    // Navigation and UI state
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Home)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _screenStack = mutableListOf<Screen>()

    private val _selectedLanguage = MutableStateFlow(Language.EN)
    val selectedLanguage: StateFlow<Language> = _selectedLanguage.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<LessonCategory?>(null)
    val selectedCategory: StateFlow<LessonCategory?> = _selectedCategory.asStateFlow()

    // Active Quiz State
    private val _quizState = MutableStateFlow(QuizState())
    val quizState: StateFlow<QuizState> = _quizState.asStateFlow()

    // Speed Sprint State
    private val _sprintState = MutableStateFlow(SprintState())
    val sprintState: StateFlow<SprintState> = _sprintState.asStateFlow()
    private var sprintTimerJob: Job? = null

    // Milestones and Updates
    val milestones = repository.getMilestones()
    val schoolUpdates = repository.getSchoolUpdates()
    val availableAvatarItems = repository.getAvailableAvatarItems()

    init {
        viewModelScope.launch {
            repository.ensureInitialData()
        }
    }

    fun navigateTo(screen: Screen) {
        _screenStack.add(_currentScreen.value)
        _currentScreen.value = screen
    }

    fun navigateBack(): Boolean {
        if (_screenStack.isNotEmpty()) {
            _currentScreen.value = _screenStack.removeAt(_screenStack.size - 1)
            return true
        }
        return false
    }

    fun setLanguage(lang: Language) {
        _selectedLanguage.value = lang
    }

    fun toggleDarkTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: LessonCategory?) {
        _selectedCategory.value = category
    }

    fun getFilteredLessons(): List<Lesson> {
        val all = MathLessonBank.getAllLessons()
        val cat = _selectedCategory.value
        val query = _searchQuery.value.trim().lowercase()

        return all.filter { lesson ->
            val matchesCategory = cat == null || lesson.category == cat
            val matchesSearch = query.isEmpty() ||
                    lesson.title.lowercase().contains(query) ||
                    lesson.description.lowercase().contains(query) ||
                    lesson.category.titleRes.lowercase().contains(query) ||
                    lesson.questions.any { it.questionText.lowercase().contains(query) }
            matchesCategory && matchesSearch
        }
    }

    // --- LESSON QUIZ CONTROLS ---
    fun startLesson(lesson: Lesson) {
        _quizState.value = QuizState(startTimeMs = System.currentTimeMillis())
        navigateTo(Screen.LessonExercise(lesson))
    }

    fun selectQuizOption(option: String) {
        if (!_quizState.value.isAnswerChecked) {
            _quizState.value = _quizState.value.copy(selectedOption = option)
        }
    }

    fun checkAnswer(currentQuestion: MathQuestion, lesson: Lesson) {
        val state = _quizState.value
        if (state.selectedOption == null) return

        val isCorrect = state.selectedOption.trim() == currentQuestion.correctAnswer.trim()
        val earnedCoins = if (isCorrect) 10 else 2
        val newScore = if (isCorrect) state.score + 1 else state.score

        _quizState.value = state.copy(
            isAnswerChecked = true,
            isCorrect = isCorrect,
            score = newScore,
            totalCoinsEarned = state.totalCoinsEarned + earnedCoins
        )
    }

    fun nextQuestion(lesson: Lesson) {
        val state = _quizState.value
        val nextIndex = state.currentQuestionIndex + 1

        if (nextIndex < lesson.questions.size) {
            _quizState.value = state.copy(
                currentQuestionIndex = nextIndex,
                selectedOption = null,
                isAnswerChecked = false,
                isCorrect = false
            )
        } else {
            // Finish lesson
            val starsEarned = when {
                state.score == lesson.questions.size -> 3
                state.score >= (lesson.questions.size / 2) -> 2
                else -> 1
            }
            val durationSeconds = ((System.currentTimeMillis() - state.startTimeMs) / 1000).toInt()

            viewModelScope.launch {
                repository.recordSessionCompletion(
                    studentId = student.value.id,
                    lessonId = lesson.id,
                    lessonTitle = lesson.title,
                    category = lesson.category.name,
                    score = state.score,
                    totalQuestions = lesson.questions.size,
                    starsEarned = starsEarned,
                    coinsEarned = state.totalCoinsEarned,
                    durationSeconds = durationSeconds.coerceAtLeast(10)
                )
            }

            _quizState.value = state.copy(isFinished = true)
        }
    }

    // --- SPEED SPRINT CONTROLS ---
    fun startSpeedSprint() {
        val initialQuestion = generateSprintQuestion()
        _sprintState.value = SprintState(
            timeLeftSeconds = 60,
            currentQuestion = initialQuestion,
            score = 0,
            streak = 0,
            isRunning = true,
            isFinished = false
        )
        navigateTo(Screen.SpeedSprint)

        sprintTimerJob?.cancel()
        sprintTimerJob = viewModelScope.launch {
            while (_sprintState.value.timeLeftSeconds > 0 && _sprintState.value.isRunning) {
                delay(1000)
                val newTime = _sprintState.value.timeLeftSeconds - 1
                if (newTime <= 0) {
                    finishSpeedSprint()
                } else {
                    _sprintState.value = _sprintState.value.copy(timeLeftSeconds = newTime)
                }
            }
        }
    }

    fun answerSprintQuestion(chosenAnswer: String) {
        val state = _sprintState.value
        val current = state.currentQuestion ?: return
        val isCorrect = chosenAnswer.trim() == current.correctAnswer.trim()

        val newScore = if (isCorrect) state.score + 1 else state.score
        val newStreak = if (isCorrect) state.streak + 1 else 0

        _sprintState.value = state.copy(
            score = newScore,
            streak = newStreak,
            currentQuestion = generateSprintQuestion()
        )
    }

    private fun finishSpeedSprint() {
        sprintTimerJob?.cancel()
        val finalScore = _sprintState.value.score
        val coinsEarned = finalScore * 5
        val stars = if (finalScore >= 10) 3 else if (finalScore >= 5) 2 else 1

        viewModelScope.launch {
            repository.recordSessionCompletion(
                studentId = student.value.id,
                lessonId = "sprint_session",
                lessonTitle = "Lightning Math Sprint",
                category = LessonCategory.SPEED_SPRINT.name,
                score = finalScore,
                totalQuestions = finalScore + 2,
                starsEarned = stars,
                coinsEarned = coinsEarned,
                durationSeconds = 60
            )
        }

        _sprintState.value = _sprintState.value.copy(
            timeLeftSeconds = 0,
            isRunning = false,
            isFinished = true
        )
    }

    private fun generateSprintQuestion(): MathQuestion {
        val op = listOf("+", "-", "×").random()
        val (a, b, ans) = when (op) {
            "+" -> {
                val x = (2..20).random()
                val y = (2..20).random()
                Triple(x, y, x + y)
            }
            "-" -> {
                val x = (10..30).random()
                val y = (1..x).random()
                Triple(x, y, x - y)
            }
            else -> {
                val x = (2..9).random()
                val y = (2..9).random()
                Triple(x, y, x * y)
            }
        }
        val distractors = setOf(ans + 1, (ans - 1).coerceAtLeast(0), ans + 2, ans + 3)
            .filter { it != ans }
            .take(3)
        val options = (distractors + ans).shuffled().map { it.toString() }

        return MathQuestion(
            id = "sprint_${System.currentTimeMillis()}",
            lessonId = "speed_1",
            questionText = "$a $op $b = ?",
            options = options,
            correctAnswer = ans.toString(),
            hint = "",
            explanation = "$a $op $b = $ans"
        )
    }

    // --- REWARDS STORE ---
    fun buyOrEquipItem(item: AvatarItem) {
        viewModelScope.launch {
            val isUnlocked = unlockedItemIds.value.contains(item.id)
            if (isUnlocked) {
                repository.equipItem(student.value.id, item.id)
            } else {
                repository.buyAndEquipItem(student.value.id, item)
            }
        }
    }

    // --- PROFILE & REGISTRATION ---
    fun registerStudent(name: String, grade: GradeLevel, email: String) {
        viewModelScope.launch {
            val updated = student.value.copy(
                name = name,
                gradeLevel = grade,
                email = email,
                isVerified = false
            )
            repository.saveStudent(updated)
        }
    }

    fun verifyStudentEmail() {
        viewModelScope.launch {
            repository.verifyEmail(student.value.id)
        }
    }

    fun toggleVipStatus() {
        viewModelScope.launch {
            val currentVip = student.value.isVip
            repository.setVipStatus(student.value.id, !currentVip)
        }
    }

    fun awardGameReward(coins: Int, stars: Int, gameName: String) {
        viewModelScope.launch {
            repository.recordSessionCompletion(
                studentId = student.value.id,
                lessonId = gameName.lowercase().replace(" ", "_"),
                lessonTitle = gameName,
                category = "ARCADE_GAMES",
                score = (coins / 10).coerceAtLeast(1),
                totalQuestions = (coins / 10).coerceAtLeast(1),
                starsEarned = stars,
                coinsEarned = coins,
                durationSeconds = 60
            )
        }
    }

    // --- PDF EXPORT & PRINTING ---
    fun printProgressReport(context: Context) {
        val file = PdfReportHelper.generateProgressPdf(
            context = context,
            student = student.value,
            sessions = sessions.value,
            milestones = milestones
        )
        if (file != null) {
            PdfReportHelper.printPdf(context, file)
        }
    }

    fun shareProgressReport(context: Context) {
        val file = PdfReportHelper.generateProgressPdf(
            context = context,
            student = student.value,
            sessions = sessions.value,
            milestones = milestones
        )
        if (file != null) {
            PdfReportHelper.sharePdfReport(context, file, student.value.name)
        }
    }
}
