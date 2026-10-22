package com.example.model

object Localization {
    fun getCategoryTitle(category: LessonCategory, lang: Language): String {
        return when (lang) {
            Language.ES -> when (category) {
                LessonCategory.ADDITION -> "Suma"
                LessonCategory.SUBTRACTION -> "Resta"
                LessonCategory.MULTIPLICATION -> "Multiplicación"
                LessonCategory.DIVISION -> "División"
                LessonCategory.GEOMETRY -> "Geometría y Formas"
                LessonCategory.FRACTIONS -> "Fracciones y Pizza"
                LessonCategory.WORD_PROBLEMS -> "Problemas de Historias"
                LessonCategory.TELLING_TIME -> "La Hora y Relojes"
                LessonCategory.MONEY_MATH -> "Monedas y Dinero"
                LessonCategory.MEASUREMENT -> "Medición y Balanzas"
                LessonCategory.PATTERNS -> "Patrones y Series"
                LessonCategory.SPEED_SPRINT -> "Carrera Rápida de Matemáticas"
            }
            Language.FR -> when (category) {
                LessonCategory.ADDITION -> "Addition"
                LessonCategory.SUBTRACTION -> "Soustraction"
                LessonCategory.MULTIPLICATION -> "Multiplication"
                LessonCategory.DIVISION -> "Division"
                LessonCategory.GEOMETRY -> "Géométrie & Formes"
                LessonCategory.FRACTIONS -> "Fractions & Pizza"
                LessonCategory.WORD_PROBLEMS -> "Problèmes Écrits"
                LessonCategory.TELLING_TIME -> "Lire l'Heure & Horloges"
                LessonCategory.MONEY_MATH -> "Monnaie & Pièces"
                LessonCategory.MEASUREMENT -> "Mesure & Balances"
                LessonCategory.PATTERNS -> "Motifs & Suites"
                LessonCategory.SPEED_SPRINT -> "Sprint de Mathématiques"
            }
            Language.DE -> when (category) {
                LessonCategory.ADDITION -> "Addition"
                LessonCategory.SUBTRACTION -> "Subtraktion"
                LessonCategory.MULTIPLICATION -> "Multiplikation"
                LessonCategory.DIVISION -> "Division"
                LessonCategory.GEOMETRY -> "Geometrie & Formen"
                LessonCategory.FRACTIONS -> "Brüche & Pizza"
                LessonCategory.WORD_PROBLEMS -> "Textaufgaben"
                LessonCategory.TELLING_TIME -> "Uhrzeit & Uhren"
                LessonCategory.MONEY_MATH -> "Geld & Münzen"
                LessonCategory.MEASUREMENT -> "Messen & Waagen"
                LessonCategory.PATTERNS -> "Muster & Zahlenfolgen"
                LessonCategory.SPEED_SPRINT -> "Mathe-Sprint"
            }
            Language.HI -> when (category) {
                LessonCategory.ADDITION -> "जोड़ (Addition)"
                LessonCategory.SUBTRACTION -> "घटाव (Subtraction)"
                LessonCategory.MULTIPLICATION -> "गुणा (Multiplication)"
                LessonCategory.DIVISION -> "भाग (Division)"
                LessonCategory.GEOMETRY -> "ज्यामिति और आकार"
                LessonCategory.FRACTIONS -> "भिन्न और पिज्जा"
                LessonCategory.WORD_PROBLEMS -> "कहानी पहेलियाँ"
                LessonCategory.TELLING_TIME -> "समय और घड़ियाँ"
                LessonCategory.MONEY_MATH -> "सिक्के और पैसे"
                LessonCategory.MEASUREMENT -> "माप और तराजू"
                LessonCategory.PATTERNS -> "पैटर्न और अनुक्रम"
                LessonCategory.SPEED_SPRINT -> "स्पीड मैथ स्प्रिंट"
            }
            Language.TA -> when (category) {
                LessonCategory.ADDITION -> "கூட்டல் (Addition)"
                LessonCategory.SUBTRACTION -> "கழித்தல் (Subtraction)"
                LessonCategory.MULTIPLICATION -> "பெருக்கல் (Multiplication)"
                LessonCategory.DIVISION -> "வகுத்தல் (Division)"
                LessonCategory.GEOMETRY -> "வடிவியல் மற்றும் வடிவங்கள்"
                LessonCategory.FRACTIONS -> "பின்னங்கள் மற்றும் பீட்சா"
                LessonCategory.WORD_PROBLEMS -> "கணக்கு புதிர்கள்"
                LessonCategory.TELLING_TIME -> "நேரம் மற்றும் கடிகாரம்"
                LessonCategory.MONEY_MATH -> "நாணயங்கள் மற்றும் பணம்"
                LessonCategory.MEASUREMENT -> "அளவீடு மற்றும் தராசு"
                LessonCategory.PATTERNS -> "வடிவமைப்புகள் மற்றும் தொடர்கள்"
                LessonCategory.SPEED_SPRINT -> "வேகக் கணித ஓட்டம்"
            }
            Language.EN -> category.titleRes
        }
    }

    fun getEncouragement(isCorrect: Boolean, lang: Language): String {
        return if (isCorrect) {
            when (lang) {
                Language.ES -> listOf("¡Fantástico!", "¡Eres un genio!", "¡Sigue así!", "¡Estupendo!").random()
                Language.FR -> listOf("Bravo!", "Magnifique!", "Tu es un génie!", "Super travail!").random()
                Language.DE -> listOf("Super gemacht!", "Großartig!", "Du bist schlau!", "Klasse!").random()
                Language.HI -> listOf("शाबाश!", "बहुत बढ़िया!", "तुम कमाल हो!", "शानदार!").random()
                Language.TA -> listOf("அருமை!", "மிக நன்று!", "சிறந்த முயற்சி!", "கெட்டிக்காரன்!").random()
                Language.EN -> listOf("Awesome Job!", "Brilliant Thinking!", "You Got It!", "Super Math Hero!").random()
            }
        } else {
            when (lang) {
                Language.ES -> "¡Casi! Inténtalo de nuevo, ¡tú puedes!"
                Language.FR -> "Presque! Réessaie, tu peux y arriver!"
                Language.DE -> "Fast! Probier es noch einmal!"
                Language.HI -> "लगभग सही! फिर से प्रयास करें, आप कर सकते हैं!"
                Language.TA -> "கிட்டத்தட்ட சரி! மீண்டும் முயற்சிக்கவும்!"
                Language.EN -> "Almost! Give it another try, you can do it!"
            }
        }
    }

    fun getUiString(key: String, lang: Language): String {
        return when (key) {
            "search_placeholder" -> when (lang) {
                Language.ES -> "Buscar temas matemáticos..."
                Language.FR -> "Rechercher des sujets de maths..."
                Language.DE -> "Mathe-Themen suchen..."
                Language.HI -> "गणित के विषय खोजें..."
                Language.TA -> "கணித தலைப்புகளை தேடுங்கள்..."
                else -> "Search math topics, lessons, shapes, clocks..."
            }
            "offline_ready" -> when (lang) {
                Language.ES -> "Modo sin conexión listo"
                Language.FR -> "Mode hors ligne actif"
                Language.DE -> "Offline-Modus bereit"
                Language.HI -> "ऑफलाइन तैयार"
                Language.TA -> "ஆஃப்லைன் தயார்"
                else -> "Offline Ready • 100% Synced"
            }
            "parent_zone" -> when (lang) {
                Language.ES -> "Zona de Padres"
                Language.FR -> "Zone Parents"
                Language.DE -> "Elternbereich"
                Language.HI -> "अभिभावक क्षेत्र"
                Language.TA -> "பெற்றோர் பகுதி"
                else -> "Parent Zone"
            }
            "rewards_store" -> when (lang) {
                Language.ES -> "Tienda de Recompensas"
                Language.FR -> "Boutique de Récompenses"
                Language.DE -> "Belohnungsladen"
                Language.HI -> "इनाम स्टोर"
                Language.TA -> "வெகுமதி அங்காடி"
                else -> "Rewards Store"
            }
            "print_report" -> when (lang) {
                Language.ES -> "Imprimir Informe PDF"
                Language.FR -> "Imprimer Rapport PDF"
                Language.DE -> "PDF-Bericht drucken"
                Language.HI -> "पीडीएफ रिपोर्ट प्रिंट करें"
                Language.TA -> "பிடிஎஃப் அறிக்கையை அச்சிடு"
                else -> "Print PDF Report"
            }
            "share_report" -> when (lang) {
                Language.ES -> "Compartir en WhatsApp / Redes"
                Language.FR -> "Partager sur WhatsApp / Réseaux"
                Language.DE -> "Über WhatsApp teilen"
                Language.HI -> "व्हाट्सएप पर साझा करें"
                Language.TA -> "வாட்ஸ்அப்பில் பகிரவும்"
                else -> "Share via WhatsApp & Social"
            }
            "upgrade_vip" -> when (lang) {
                Language.ES -> "Obtener MathWhiz VIP"
                Language.FR -> "Obtenir MathWhiz VIP"
                Language.DE -> "MathWhiz VIP holen"
                Language.HI -> "MathWhiz VIP प्राप्त करें"
                Language.TA -> "MathWhiz விஐபி பெறுக"
                else -> "Get MathWhiz VIP"
            }
            else -> key
        }
    }
}
