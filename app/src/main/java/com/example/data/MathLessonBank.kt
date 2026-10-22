package com.example.data

import com.example.model.*

object MathLessonBank {
    fun getAllLessons(): List<Lesson> = listOf(
        // --- ADDITION ---
        Lesson(
            id = "add_1",
            category = LessonCategory.ADDITION,
            title = "Counting Juicy Apples",
            description = "Count the red apples together to find the sum!",
            gradeLevel = GradeLevel.GRADE_1,
            order = 1,
            isPremium = false,
            questions = listOf(
                MathQuestion(
                    id = "add_1_q1",
                    lessonId = "add_1",
                    questionText = "Count the apples in both baskets. What is 3 + 2?",
                    visualType = VisualType.COUNTER_DOTS,
                    visualData = VisualData(count1 = 3, count2 = 2, itemEmoji = "🍎"),
                    options = listOf("4", "5", "6", "7"),
                    correctAnswer = "5",
                    hint = "Count each apple one by one: 1, 2, 3... then 4, 5!",
                    explanation = "3 apples plus 2 apples equals 5 apples!"
                ),
                MathQuestion(
                    id = "add_1_q2",
                    lessonId = "add_1",
                    questionText = "Count the shiny stars! What is 4 + 3?",
                    visualType = VisualType.COUNTER_DOTS,
                    visualData = VisualData(count1 = 4, count2 = 3, itemEmoji = "⭐"),
                    options = listOf("6", "7", "8", "9"),
                    correctAnswer = "7",
                    hint = "Start from 4 and count up 3 more: 5, 6, 7!",
                    explanation = "4 stars plus 3 stars equals 7 stars!"
                ),
                MathQuestion(
                    id = "add_1_q3",
                    lessonId = "add_1",
                    questionText = "What is 5 + 5?",
                    visualType = VisualType.COUNTER_DOTS,
                    visualData = VisualData(count1 = 5, count2 = 5, itemEmoji = "🍪"),
                    options = listOf("8", "9", "10", "11"),
                    correctAnswer = "10",
                    hint = "Think about two full hands of fingers: 5 and 5!",
                    explanation = "5 cookies + 5 cookies = 10 cookies!"
                )
            )
        ),
        Lesson(
            id = "add_2",
            category = LessonCategory.ADDITION,
            title = "Frog Jump on Number Line",
            description = "Help the frog jump forward to land on the answer!",
            gradeLevel = GradeLevel.GRADE_2,
            order = 2,
            isPremium = false,
            questions = listOf(
                MathQuestion(
                    id = "add_2_q1",
                    lessonId = "add_2",
                    questionText = "The frog is at 4 and jumps 5 steps forward. Where does it land?",
                    visualType = VisualType.NUMBER_LINE,
                    visualData = VisualData(numberLineStart = 0, numberLineEnd = 12, numberLineHop1 = 4, numberLineHop2 = 5),
                    options = listOf("8", "9", "10", "11"),
                    correctAnswer = "9",
                    hint = "Look at the hop arc: 4 + 5 = ?",
                    explanation = "Starting at 4 and jumping 5 units lands at 9!"
                ),
                MathQuestion(
                    id = "add_2_q2",
                    lessonId = "add_2",
                    questionText = "The rabbit hops from 6 and jumps 4 steps. 6 + 4 = ?",
                    visualType = VisualType.NUMBER_LINE,
                    visualData = VisualData(numberLineStart = 0, numberLineEnd = 12, numberLineHop1 = 6, numberLineHop2 = 4),
                    options = listOf("9", "10", "11", "12"),
                    correctAnswer = "10",
                    hint = "6 + 4 is a friendly ten pair!",
                    explanation = "6 + 4 = 10!"
                )
            )
        ),
        Lesson(
            id = "add_3",
            category = LessonCategory.ADDITION,
            title = "Double-Digit Power Sums",
            description = "Master two-digit addition with carry over!",
            gradeLevel = GradeLevel.GRADE_3,
            order = 3,
            isPremium = true,
            questions = listOf(
                MathQuestion(
                    id = "add_3_q1",
                    lessonId = "add_3",
                    questionText = "Add the tens and ones: What is 25 + 17?",
                    visualType = VisualType.NONE,
                    options = listOf("38", "41", "42", "45"),
                    correctAnswer = "42",
                    hint = "First add the ones: 5 + 7 = 12 (carry 1). Then 2 + 1 + 1 = 4!",
                    explanation = "25 + 17 = 42"
                ),
                MathQuestion(
                    id = "add_3_q2",
                    lessonId = "add_3",
                    questionText = "What is 48 + 35?",
                    visualType = VisualType.NONE,
                    options = listOf("73", "81", "83", "85"),
                    correctAnswer = "83",
                    hint = "8 + 5 = 13. Carry the 1 to the tens: 4 + 3 + 1 = 8!",
                    explanation = "48 + 35 = 83"
                )
            )
        ),

        // --- SUBTRACTION ---
        Lesson(
            id = "sub_1",
            category = LessonCategory.SUBTRACTION,
            title = "Taking Away Cookies",
            description = "Start with a pile and see how many are left!",
            gradeLevel = GradeLevel.GRADE_1,
            order = 1,
            isPremium = false,
            questions = listOf(
                MathQuestion(
                    id = "sub_1_q1",
                    lessonId = "sub_1",
                    questionText = "You had 7 cookies. You ate 3. How many are left?",
                    visualType = VisualType.COUNTER_DOTS,
                    visualData = VisualData(count1 = 7, count2 = 3, itemEmoji = "🍪"),
                    options = listOf("3", "4", "5", "6"),
                    correctAnswer = "4",
                    hint = "Count backwards from 7 by 3: 6, 5, 4!",
                    explanation = "7 minus 3 leaves 4 delicious cookies!"
                ),
                MathQuestion(
                    id = "sub_1_q2",
                    lessonId = "sub_1",
                    questionText = "There are 9 colorful balloons. 4 pop! 🎈 How many are left?",
                    visualType = VisualType.COUNTER_DOTS,
                    visualData = VisualData(count1 = 9, count2 = 4, itemEmoji = "🎈"),
                    options = listOf("4", "5", "6", "7"),
                    correctAnswer = "5",
                    hint = "9 take away 4: 9 - 4 = ?",
                    explanation = "9 - 4 = 5 balloons remaining!"
                )
            )
        ),

        // --- MULTIPLICATION ---
        Lesson(
            id = "mul_1",
            category = LessonCategory.MULTIPLICATION,
            title = "Star Arrays & Groups",
            description = "Discover multiplication by looking at rows and columns!",
            gradeLevel = GradeLevel.GRADE_2,
            order = 1,
            isPremium = false,
            questions = listOf(
                MathQuestion(
                    id = "mul_1_q1",
                    lessonId = "mul_1",
                    questionText = "Look at the grid: 3 rows with 4 stars in each row. How many stars in total?",
                    visualType = VisualType.ARRAY_GRID,
                    visualData = VisualData(gridRows = 3, gridCols = 4, itemEmoji = "⭐"),
                    options = listOf("10", "12", "14", "16"),
                    correctAnswer = "12",
                    hint = "3 rows × 4 stars = 4 + 4 + 4!",
                    explanation = "3 × 4 = 12 stars!"
                ),
                MathQuestion(
                    id = "mul_1_q2",
                    lessonId = "mul_1",
                    questionText = "What is 4 × 5?",
                    visualType = VisualType.ARRAY_GRID,
                    visualData = VisualData(gridRows = 4, gridCols = 5, itemEmoji = "💎"),
                    options = listOf("18", "20", "22", "25"),
                    correctAnswer = "20",
                    hint = "Count by 5s four times: 5, 10, 15, 20!",
                    explanation = "4 × 5 = 20!"
                )
            )
        ),
        Lesson(
            id = "mul_2",
            category = LessonCategory.MULTIPLICATION,
            title = "Fast Times Tables (6 to 9)",
            description = "Become a wizard at the upper multiplication facts!",
            gradeLevel = GradeLevel.GRADE_4,
            order = 2,
            isPremium = true,
            questions = listOf(
                MathQuestion(
                    id = "mul_2_q1",
                    lessonId = "mul_2",
                    questionText = "What is 7 × 8?",
                    visualType = VisualType.NONE,
                    options = listOf("54", "56", "58", "64"),
                    correctAnswer = "56",
                    hint = "Remember: 5, 6, 7, 8 -> 56 = 7 × 8!",
                    explanation = "7 × 8 = 56"
                ),
                MathQuestion(
                    id = "mul_2_q2",
                    lessonId = "mul_2",
                    questionText = "What is 9 × 6?",
                    visualType = VisualType.NONE,
                    options = listOf("52", "54", "56", "63"),
                    correctAnswer = "54",
                    hint = "9 × 6: the digits 5 + 4 add up to 9!",
                    explanation = "9 × 6 = 54"
                )
            )
        ),

        // --- DIVISION ---
        Lesson(
            id = "div_1",
            category = LessonCategory.DIVISION,
            title = "Fair Sharing with Friends",
            description = "Share treats equally among your classmates!",
            gradeLevel = GradeLevel.GRADE_3,
            order = 1,
            isPremium = false,
            questions = listOf(
                MathQuestion(
                    id = "div_1_q1",
                    lessonId = "div_1",
                    questionText = "12 cupcakes are shared equally among 3 children. How many cupcakes does each child get?",
                    visualType = VisualType.COUNTER_DOTS,
                    visualData = VisualData(count1 = 12, count2 = 3, itemEmoji = "🧁"),
                    options = listOf("3", "4", "5", "6"),
                    correctAnswer = "4",
                    hint = "Think: 3 × ? = 12",
                    explanation = "12 ÷ 3 = 4 cupcakes each!"
                ),
                MathQuestion(
                    id = "div_1_q2",
                    lessonId = "div_1",
                    questionText = "What is 15 ÷ 5?",
                    visualType = VisualType.COUNTER_DOTS,
                    visualData = VisualData(count1 = 15, count2 = 5, itemEmoji = "🍬"),
                    options = listOf("2", "3", "4", "5"),
                    correctAnswer = "3",
                    hint = "5 groups of what makes 15?",
                    explanation = "15 ÷ 5 = 3"
                )
            )
        ),

        // --- GEOMETRY & SHAPES ---
        Lesson(
            id = "geo_1",
            category = LessonCategory.GEOMETRY,
            title = "Sides, Corners & Symmetry",
            description = "Explore triangles, squares, pentagons, and hexagons!",
            gradeLevel = GradeLevel.GRADE_2,
            order = 1,
            isPremium = false,
            questions = listOf(
                MathQuestion(
                    id = "geo_1_q1",
                    lessonId = "geo_1",
                    questionText = "How many sides and vertices does a Triangle have?",
                    visualType = VisualType.SHAPE_GEOMETRY,
                    visualData = VisualData(shapeType = "Triangle", shapeSides = 3),
                    options = listOf("2", "3", "4", "5"),
                    correctAnswer = "3",
                    hint = "Count the sharp corners pointing out!",
                    explanation = "A triangle has exactly 3 sides and 3 corners!"
                ),
                MathQuestion(
                    id = "geo_1_q2",
                    lessonId = "geo_1",
                    questionText = "What shape has 5 equal sides and 5 vertices?",
                    visualType = VisualType.SHAPE_GEOMETRY,
                    visualData = VisualData(shapeType = "Pentagon", shapeSides = 5),
                    options = listOf("Square", "Pentagon", "Hexagon", "Octagon"),
                    correctAnswer = "Pentagon",
                    hint = "'Penta' means five!",
                    explanation = "A 5-sided polygon is a Pentagon!"
                ),
                MathQuestion(
                    id = "geo_1_q3",
                    lessonId = "geo_1",
                    questionText = "How many sides does a Hexagon have?",
                    visualType = VisualType.SHAPE_GEOMETRY,
                    visualData = VisualData(shapeType = "Hexagon", shapeSides = 6),
                    options = listOf("5", "6", "7", "8"),
                    correctAnswer = "6",
                    hint = "A honeycomb cell is a hexagon with 6 sides!",
                    explanation = "A hexagon has 6 sides and 6 vertices!"
                )
            )
        ),

        // --- FRACTIONS & PIZZA ---
        Lesson(
            id = "frac_1",
            category = LessonCategory.FRACTIONS,
            title = "Pizza Slices: Halves & Quarters",
            description = "Yum! Slice the pizza to understand fractions!",
            gradeLevel = GradeLevel.GRADE_2,
            order = 1,
            isPremium = false,
            questions = listOf(
                MathQuestion(
                    id = "frac_1_q1",
                    lessonId = "frac_1",
                    questionText = "A pizza is cut into 4 equal slices. You eat 1 slice. What fraction did you eat?",
                    visualType = VisualType.PIZZA_FRACTION,
                    visualData = VisualData(fractionNumerator = 1, fractionDenominator = 4),
                    options = listOf("1/2", "1/3", "1/4", "3/4"),
                    correctAnswer = "1/4",
                    hint = "1 slice eaten out of 4 total equal slices!",
                    explanation = "1 out of 4 equal parts is written as 1/4 (one quarter)!"
                ),
                MathQuestion(
                    id = "frac_1_q2",
                    lessonId = "frac_1",
                    questionText = "Look at the shaded pizza slices. What fraction is shaded?",
                    visualType = VisualType.PIZZA_FRACTION,
                    visualData = VisualData(fractionNumerator = 3, fractionDenominator = 4),
                    options = listOf("1/4", "2/4", "3/4", "4/4"),
                    correctAnswer = "3/4",
                    hint = "Count the golden cheese slices: 3 out of 4!",
                    explanation = "3 out of 4 parts is 3/4 (three fourths)!"
                ),
                MathQuestion(
                    id = "frac_1_q3",
                    lessonId = "frac_1",
                    questionText = "Which fraction is equal to 1/2 of a pie cut into 6 slices?",
                    visualType = VisualType.PIZZA_FRACTION,
                    visualData = VisualData(fractionNumerator = 3, fractionDenominator = 6),
                    options = listOf("2/6", "3/6", "4/6", "5/6"),
                    correctAnswer = "3/6",
                    hint = "Half of 6 is 3!",
                    explanation = "3/6 is equal to 1/2 because 3 is half of 6!"
                )
            )
        ),

        // --- WORD PROBLEMS ---
        Lesson(
            id = "word_1",
            category = LessonCategory.WORD_PROBLEMS,
            title = "Zoo & Space Story Puzzles",
            description = "Read the fun story and crack the math clue!",
            gradeLevel = GradeLevel.GRADE_3,
            order = 1,
            isPremium = false,
            questions = listOf(
                MathQuestion(
                    id = "word_1_q1",
                    lessonId = "word_1",
                    questionText = "Leo the Astronaut had 18 space crystals. He gave 5 to Maya and found 7 more on Mars. How many space crystals does Leo have now?",
                    visualType = VisualType.WORD_STORY,
                    options = listOf("19", "20", "21", "22"),
                    correctAnswer = "20",
                    hint = "First calculate: 18 - 5 = 13. Then add the new crystals: 13 + 7 = ?",
                    explanation = "18 - 5 = 13, and 13 + 7 = 20 space crystals!"
                ),
                MathQuestion(
                    id = "word_1_q2",
                    lessonId = "word_1",
                    questionText = "A toy store has 6 boxes of LEGO sets. Each box contains 8 mini figures. How many mini figures are there in all?",
                    visualType = VisualType.WORD_STORY,
                    options = listOf("42", "46", "48", "54"),
                    correctAnswer = "48",
                    hint = "Multiply number of boxes by mini figures per box: 6 × 8",
                    explanation = "6 × 8 = 48 mini figures!"
                )
            )
        ),

        // --- SPEED SPRINT ---
        Lesson(
            id = "speed_1",
            category = LessonCategory.SPEED_SPRINT,
            title = "Lightning Math Sprint",
            description = "Solve as many fast arithmetic questions as you can in 60 seconds!",
            gradeLevel = GradeLevel.GRADE_2,
            order = 1,
            isPremium = false,
            questions = listOf(
                MathQuestion(
                    id = "spd_1",
                    lessonId = "speed_1",
                    questionText = "6 + 7 = ?",
                    options = listOf("12", "13", "14", "15"),
                    correctAnswer = "13",
                    hint = "6 + 6 + 1 = 13",
                    explanation = "6 + 7 = 13"
                ),
                MathQuestion(
                    id = "spd_2",
                    lessonId = "speed_1",
                    questionText = "14 - 8 = ?",
                    options = listOf("5", "6", "7", "8"),
                    correctAnswer = "6",
                    hint = "14 - 8 = 6",
                    explanation = "14 - 8 = 6"
                ),
                MathQuestion(
                    id = "spd_3",
                    lessonId = "speed_1",
                    questionText = "5 × 6 = ?",
                    options = listOf("25", "30", "35", "40"),
                    correctAnswer = "30",
                    hint = "5 × 6 = 30",
                    explanation = "5 × 6 = 30"
                ),
                MathQuestion(
                    id = "spd_4",
                    lessonId = "speed_1",
                    questionText = "24 ÷ 4 = ?",
                    options = listOf("4", "5", "6", "7"),
                    correctAnswer = "6",
                    hint = "4 × 6 = 24",
                    explanation = "24 ÷ 4 = 6"
                )
            )
        ),

        // --- TELLING TIME & CLOCKS ---
        Lesson(
            id = "time_1",
            category = LessonCategory.TELLING_TIME,
            title = "Reading the Clock: Exact Hours",
            description = "Look at the short hour hand and long minute hand to read the time!",
            gradeLevel = GradeLevel.GRADE_1,
            order = 1,
            isPremium = false,
            questions = listOf(
                MathQuestion(
                    id = "time_1_q1",
                    lessonId = "time_1",
                    questionText = "Look at the clock face. The short hour hand points to 3 and minute hand to 12. What time is it?",
                    visualType = VisualType.ANALOG_CLOCK,
                    visualData = VisualData(clockHour = 3, clockMinute = 0),
                    options = listOf("2:00", "3:00", "4:00", "12:00"),
                    correctAnswer = "3:00",
                    hint = "When the long red minute hand is at 12, it is o'clock! Look at where the blue hour hand points.",
                    explanation = "The hour hand points to 3, so it is 3:00 o'clock!"
                ),
                MathQuestion(
                    id = "time_1_q2",
                    lessonId = "time_1",
                    questionText = "The hour hand points to 7 and the minute hand points to 12. What time is it?",
                    visualType = VisualType.ANALOG_CLOCK,
                    visualData = VisualData(clockHour = 7, clockMinute = 0),
                    options = listOf("6:00", "7:00", "8:00", "12:00"),
                    correctAnswer = "7:00",
                    hint = "Look at the short hand: 7!",
                    explanation = "It is exactly 7:00!"
                ),
                MathQuestion(
                    id = "time_1_q3",
                    lessonId = "time_1",
                    questionText = "The clock shows the minute hand halfway at 6 (30 minutes past 4). What time is it?",
                    visualType = VisualType.ANALOG_CLOCK,
                    visualData = VisualData(clockHour = 4, clockMinute = 30),
                    options = listOf("4:15", "4:30", "5:00", "5:30"),
                    correctAnswer = "4:30",
                    hint = "6 on the clock stands for 30 minutes. It's half past 4!",
                    explanation = "Half past 4 is written as 4:30!"
                )
            )
        ),

        // --- MONEY & COINS ---
        Lesson(
            id = "money_1",
            category = LessonCategory.MONEY_MATH,
            title = "Counting Coins & Cents",
            description = "Pennies (1¢), Nickels (5¢), Dimes (10¢), and Quarters (25¢)!",
            gradeLevel = GradeLevel.GRADE_2,
            order = 1,
            isPremium = false,
            questions = listOf(
                MathQuestion(
                    id = "money_1_q1",
                    lessonId = "money_1",
                    questionText = "You have 2 Dimes (10¢ each) and 1 Nickel (5¢). How many cents in total?",
                    visualType = VisualType.MONEY_COINS,
                    visualData = VisualData(coinsSummary = "2 Dimes + 1 Nickel", coinsValues = listOf(10, 10, 5)),
                    options = listOf("20¢", "25¢", "30¢", "35¢"),
                    correctAnswer = "25¢",
                    hint = "Add them up: 10 + 10 = 20, then 20 + 5 = 25¢!",
                    explanation = "10¢ + 10¢ + 5¢ = 25¢ (equal to one Quarter)!"
                ),
                MathQuestion(
                    id = "money_1_q2",
                    lessonId = "money_1",
                    questionText = "Which coin has a value of 25 cents?",
                    visualType = VisualType.MONEY_COINS,
                    visualData = VisualData(coinsSummary = "1 Quarter", coinsValues = listOf(25)),
                    options = listOf("Penny", "Nickel", "Dime", "Quarter"),
                    correctAnswer = "Quarter",
                    hint = "A Quarter is 1/4 of a dollar (25 cents)!",
                    explanation = "A Quarter is worth 25 cents!"
                ),
                MathQuestion(
                    id = "money_1_q3",
                    lessonId = "money_1",
                    questionText = "You have 1 Quarter (25¢) and 1 Dime (10¢). What is the total?",
                    visualType = VisualType.MONEY_COINS,
                    visualData = VisualData(coinsSummary = "1 Quarter + 1 Dime", coinsValues = listOf(25, 10)),
                    options = listOf("30¢", "35¢", "40¢", "45¢"),
                    correctAnswer = "35¢",
                    hint = "25 + 10 = 35!",
                    explanation = "25¢ + 10¢ = 35¢"
                )
            )
        ),

        // --- MEASUREMENT & SCALES ---
        Lesson(
            id = "meas_1",
            category = LessonCategory.MEASUREMENT,
            title = "Rulers & Heavy vs Light",
            description = "Measure objects in centimeters and compare balance scales!",
            gradeLevel = GradeLevel.GRADE_2,
            order = 1,
            isPremium = false,
            questions = listOf(
                MathQuestion(
                    id = "meas_1_q1",
                    lessonId = "meas_1",
                    questionText = "A pencil on a ruler starts at 2 cm and reaches 9 cm. How long is the pencil?",
                    visualType = VisualType.NONE,
                    options = listOf("5 cm", "6 cm", "7 cm", "8 cm"),
                    correctAnswer = "7 cm",
                    hint = "Subtract the starting point from the ending point: 9 - 2 = ?",
                    explanation = "9 cm - 2 cm = 7 cm long!"
                ),
                MathQuestion(
                    id = "meas_1_q2",
                    lessonId = "meas_1",
                    questionText = "A balance scale has 8 apples on the left. The right side has 5 apples. How many more apples are needed on the right to balance?",
                    visualType = VisualType.NONE,
                    options = listOf("2", "3", "4", "5"),
                    correctAnswer = "3",
                    hint = "Think: 5 + ? = 8",
                    explanation = "8 - 5 = 3 apples needed to balance the scale!"
                )
            )
        ),

        // --- PATTERNS & SEQUENCES ---
        Lesson(
            id = "pat_1",
            category = LessonCategory.PATTERNS,
            title = "Skip Counting Train Express",
            description = "Hop by 2s, 5s, and 10s to fill the train carriages!",
            gradeLevel = GradeLevel.GRADE_1,
            order = 1,
            isPremium = false,
            questions = listOf(
                MathQuestion(
                    id = "pat_1_q1",
                    lessonId = "pat_1",
                    questionText = "What number belongs in the mystery train carriage: 2, 4, 6, ?, 10?",
                    visualType = VisualType.PATTERN_TRAIN,
                    visualData = VisualData(patternSequence = listOf("2", "4", "6", "?", "10")),
                    options = listOf("7", "8", "9", "12"),
                    correctAnswer = "8",
                    hint = "Count by 2s: 2, 4, 6, 8, 10!",
                    explanation = "Adding 2 each time: 6 + 2 = 8!"
                ),
                MathQuestion(
                    id = "pat_1_q2",
                    lessonId = "pat_1",
                    questionText = "What number comes next: 15, 20, 25, ?, 35?",
                    visualType = VisualType.PATTERN_TRAIN,
                    visualData = VisualData(patternSequence = listOf("15", "20", "25", "?", "35")),
                    options = listOf("26", "28", "30", "32"),
                    correctAnswer = "30",
                    hint = "Skip count by 5: 25 + 5 = ?",
                    explanation = "Counting by 5s: 15, 20, 25, 30, 35!"
                )
            )
        )
    )
}
