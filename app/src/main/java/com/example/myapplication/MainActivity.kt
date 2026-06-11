package com.example.emergencyhelper

import android.app.Activity
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Switch
import android.widget.TextView

class MainActivity : Activity() {
    private lateinit var scrollView: ScrollView
    private lateinit var mainLayout: LinearLayout
    private lateinit var titleText: TextView
    private lateinit var subtitleText: TextView
    private lateinit var languageHeading: TextView
    private lateinit var accessibilityHeading: TextView
    private lateinit var phraseHeading: TextView
    private lateinit var statusText: TextView
    private lateinit var outputText: TextView
    private lateinit var highContrastSwitch: Switch
    private lateinit var largeTextSwitch: Switch

    private val languageButtons = mutableMapOf<String, Button>()
    private val phraseButtons = mutableMapOf<String, Button>()
    private val textViews = mutableListOf<TextView>()
    private val buttons = mutableListOf<Button>()

    private var language = "en"
    private var selectedPhraseKey: String? = null

    private val copy = mapOf(
        "en" to mapOf(
            "title" to "Emergency Helper",
            "subtitle" to "Choose a language, then tap a phrase to show it clearly.",
            "language" to "Language",
            "accessibility" to "Accessibility",
            "phrases" to "Emergency Phrases",
            "ready" to "Ready. Select a phrase.",
            "selected" to "Phrase selected.",
            "large" to "Large text",
            "contrast" to "High contrast",
            "english" to "English",
            "spanish" to "Spanish",
            "french" to "French",
            "help" to "I need help",
            "call" to "Call emergency services",
            "lost" to "I am lost",
            "medical" to "I need medical help",
            "help_phrase" to "I need help.",
            "call_phrase" to "Please call emergency services.",
            "lost_phrase" to "I am lost. Please help me find a safe place.",
            "medical_phrase" to "I need medical help."
        ),
        "es" to mapOf(
            "title" to "Ayuda de Emergencia",
            "subtitle" to "Elige un idioma y toca una frase para mostrarla claramente.",
            "language" to "Idioma",
            "accessibility" to "Accesibilidad",
            "phrases" to "Frases de emergencia",
            "ready" to "Listo. Selecciona una frase.",
            "selected" to "Frase seleccionada.",
            "large" to "Texto grande",
            "contrast" to "Alto contraste",
            "english" to "Ingles",
            "spanish" to "Espanol",
            "french" to "Frances",
            "help" to "Necesito ayuda",
            "call" to "Llama a emergencias",
            "lost" to "Estoy perdido",
            "medical" to "Necesito ayuda medica",
            "help_phrase" to "Necesito ayuda.",
            "call_phrase" to "Por favor llama a emergencias.",
            "lost_phrase" to "Estoy perdido. Ayudame a encontrar un lugar seguro.",
            "medical_phrase" to "Necesito ayuda medica."
        ),
        "fr" to mapOf(
            "title" to "Aide Urgence",
            "subtitle" to "Choisis une langue, puis touche une phrase pour l'afficher clairement.",
            "language" to "Langue",
            "accessibility" to "Accessibilite",
            "phrases" to "Phrases d'urgence",
            "ready" to "Pret. Selectionne une phrase.",
            "selected" to "Phrase selectionnee.",
            "large" to "Grand texte",
            "contrast" to "Contraste eleve",
            "english" to "Anglais",
            "spanish" to "Espagnol",
            "french" to "Francais",
            "help" to "J'ai besoin d'aide",
            "call" to "Appelle les secours",
            "lost" to "Je suis perdu",
            "medical" to "J'ai besoin d'aide medicale",
            "help_phrase" to "J'ai besoin d'aide.",
            "call_phrase" to "Appelle les secours, s'il te plait.",
            "lost_phrase" to "Je suis perdu. Aide-moi a trouver un endroit sur.",
            "medical_phrase" to "J'ai besoin d'aide medicale."
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildInterface()
        updateLanguage()
        applyAccessibilityOptions()
    }

    private fun buildInterface() {
        scrollView = ScrollView(this).apply {
            isFillViewport = true
        }

        mainLayout = LinearLayout(this).apply {
            orientation = if (isWideLayout()) LinearLayout.HORIZONTAL else LinearLayout.VERTICAL
            gravity = Gravity.TOP
            setPadding(dp(20), dp(20), dp(20), dp(20))
        }

        val introColumn = column()
        val actionColumn = column()

        titleText = heading(size = 30f)
        subtitleText = body()
        languageHeading = heading(size = 21f)
        accessibilityHeading = heading(size = 21f)
        phraseHeading = heading(size = 21f)
        statusText = body()
        outputText = heading(size = 26f).apply {
            gravity = Gravity.CENTER
            minHeight = dp(120)
            setPadding(dp(18), dp(18), dp(18), dp(18))
        }

        introColumn.addView(titleText)
        introColumn.addView(subtitleText)
        introColumn.addView(spacer(18))
        introColumn.addView(languageHeading)
        introColumn.addView(buttonRow(listOf(
            languageButton("en"),
            languageButton("es"),
            languageButton("fr")
        )))
        introColumn.addView(spacer(18))
        introColumn.addView(accessibilityHeading)

        largeTextSwitch = Switch(this).apply {
            setOnCheckedChangeListener { _, _ -> applyAccessibilityOptions() }
        }
        highContrastSwitch = Switch(this).apply {
            setOnCheckedChangeListener { _, _ -> applyAccessibilityOptions() }
        }
        textViews.add(largeTextSwitch)
        textViews.add(highContrastSwitch)
        introColumn.addView(largeTextSwitch)
        introColumn.addView(highContrastSwitch)

        actionColumn.addView(phraseHeading)
        actionColumn.addView(statusText)
        actionColumn.addView(spacer(12))
        actionColumn.addView(phraseButton("help"))
        actionColumn.addView(phraseButton("call"))
        actionColumn.addView(phraseButton("lost"))
        actionColumn.addView(phraseButton("medical"))
        actionColumn.addView(spacer(16))
        actionColumn.addView(outputText)

        if (isWideLayout()) {
            mainLayout.addView(introColumn, weightedLayout())
            mainLayout.addView(actionColumn, weightedLayout())
        } else {
            mainLayout.addView(introColumn)
            mainLayout.addView(actionColumn)
        }

        scrollView.addView(mainLayout)
        setContentView(scrollView)
    }

    private fun updateLanguage() {
        titleText.text = text("title")
        subtitleText.text = text("subtitle")
        languageHeading.text = text("language")
        accessibilityHeading.text = text("accessibility")
        phraseHeading.text = text("phrases")
        largeTextSwitch.text = text("large")
        highContrastSwitch.text = text("contrast")
        statusText.text = if (selectedPhraseKey == null) text("ready") else text("selected")

        languageButtons["en"]?.text = text("english")
        languageButtons["es"]?.text = text("spanish")
        languageButtons["fr"]?.text = text("french")

        phraseButtons["help"]?.text = text("help")
        phraseButtons["call"]?.text = text("call")
        phraseButtons["lost"]?.text = text("lost")
        phraseButtons["medical"]?.text = text("medical")

        selectedPhraseKey?.let { outputText.text = text("${it}_phrase") } ?: run {
            outputText.text = ""
        }

        languageButtons.forEach { (_, button) ->
            button.contentDescription = "${text("language")}: ${button.text}"
        }
        phraseButtons.forEach { (_, button) ->
            button.contentDescription = button.text
        }
        largeTextSwitch.contentDescription = text("large")
        highContrastSwitch.contentDescription = text("contrast")
        outputText.contentDescription = outputText.text
    }

    private fun applyAccessibilityOptions() {
        val highContrast = highContrastSwitch.isChecked
        val largeText = largeTextSwitch.isChecked
        val background = if (highContrast) Color.BLACK else Color.rgb(248, 250, 252)
        val foreground = if (highContrast) Color.WHITE else Color.rgb(22, 28, 36)
        val buttonBackground = if (highContrast) Color.rgb(32, 32, 32) else Color.rgb(218, 231, 255)
        val outputBackground = if (highContrast) Color.rgb(10, 10, 10) else Color.WHITE

        scrollView.setBackgroundColor(background)
        mainLayout.setBackgroundColor(background)
        textViews.forEach { view ->
            view.setTextColor(foreground)
            val baseSize = when (view) {
                titleText -> 30f
                outputText -> 26f
                languageHeading, accessibilityHeading, phraseHeading -> 21f
                else -> 17f
            }
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (largeText) baseSize + 5f else baseSize)
        }
        outputText.setBackgroundColor(outputBackground)
        buttons.forEach { button ->
            button.setTextColor(foreground)
            button.setBackgroundColor(buttonBackground)
            button.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (largeText) 19f else 16f)
            button.minHeight = dp(if (largeText) 58 else 48)
        }
    }

    private fun languageButton(code: String): Button {
        return Button(this).apply {
            languageButtons[code] = this
            buttons.add(this)
            setOnClickListener {
                language = code
                updateLanguage()
                applyAccessibilityOptions()
            }
        }
    }

    private fun phraseButton(key: String): Button {
        return Button(this).apply {
            phraseButtons[key] = this
            buttons.add(this)
            setAllCaps(false)
            setOnClickListener {
                selectedPhraseKey = key
                updateLanguage()
                applyAccessibilityOptions()
            }
        }
    }

    private fun column(): LinearLayout = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(dp(6), dp(6), dp(6), dp(6))
    }

    private fun buttonRow(buttonsInRow: List<Button>): LinearLayout = LinearLayout(this).apply {
        orientation = if (isWideLayout()) LinearLayout.VERTICAL else LinearLayout.HORIZONTAL
        buttonsInRow.forEach { button ->
            addView(button, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f).apply {
                setMargins(dp(3), dp(3), dp(3), dp(3))
            })
        }
    }

    private fun heading(size: Float): TextView = TextView(this).apply {
        setTypeface(typeface, Typeface.BOLD)
        setTextSize(TypedValue.COMPLEX_UNIT_SP, size)
        setPadding(0, dp(6), 0, dp(6))
        textViews.add(this)
    }

    private fun body(): TextView = TextView(this).apply {
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 17f)
        setPadding(0, dp(4), 0, dp(4))
        textViews.add(this)
    }

    private fun spacer(height: Int): View = View(this).apply {
        layoutParams = LinearLayout.LayoutParams(1, dp(height))
    }

    private fun weightedLayout(): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f).apply {
            setMargins(dp(8), dp(8), dp(8), dp(8))
        }
    }

    private fun text(key: String): String = copy[language]?.get(key) ?: copy.getValue("en").getValue(key)

    private fun isWideLayout(): Boolean {
        return resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE ||
            resources.configuration.screenWidthDp >= 600
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
