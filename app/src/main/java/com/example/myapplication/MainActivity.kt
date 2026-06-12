package com.example.emergencyhelper

import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputType
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Switch
import android.widget.TextView

class MainActivity : Activity() {
    private lateinit var scrollView: ScrollView
    private lateinit var mainLayout: LinearLayout
    private lateinit var titleText: TextView
    private lateinit var languageHeading: TextView
    private lateinit var accessibilityHeading: TextView
    private lateinit var statsHeading: TextView
    private lateinit var workoutHeading: TextView
    private lateinit var stepsText: TextView
    private lateinit var caloriesText: TextView
    private lateinit var waterText: TextView
    private lateinit var workoutText: TextView
    private lateinit var workoutInput: EditText
    private lateinit var highContrastSwitch: Switch
    private lateinit var largeTextSwitch: Switch
    private lateinit var settingsPanel: LinearLayout

    private val languageButtons = mutableMapOf<String, Button>()
    private val actionButtons = mutableMapOf<String, Button>()
    private val buttons = mutableListOf<Button>()
    private val textViews = mutableListOf<TextView>()

    private var language = "en"
    private var deviceMode = "phone"
    private var steps = 0
    private var caloriesEaten = 0
    private var water = 0
    private var workoutPlan = ""
    private var settingsOpen = false

    private val copy = mapOf(
        "en" to mapOf(
            "title" to "Accessible Fitness Tracker",
            "language" to "Language",
            "accessibility" to "Accessibility",
            "settings" to "Settings",
            "hide_settings" to "Hide settings",
            "stats" to "Today",
            "workout" to "Workout Plan",
            "large" to "Large text",
            "contrast" to "High contrast",
            "english" to "English",
            "spanish" to "Spanish",
            "french" to "French",
            "add_steps" to "+500 steps",
            "sub_steps" to "-500 steps",
            "add_calories" to "+100 calories",
            "sub_calories" to "-100 calories",
            "add_water" to "+1 water",
            "sub_water" to "-1 water",
            "save_plan" to "Save plan",
            "delete_plan" to "Delete task",
            "steps" to "Steps",
            "calories" to "Calories eaten",
            "water" to "Water",
            "plan_empty" to "No workout plan saved yet.",
            "plan_hint" to "Type your workout plan"
        ),
        "es" to mapOf(
            "title" to "Rastreador Fisico Accesible",
            "language" to "Idioma",
            "accessibility" to "Accesibilidad",
            "settings" to "Ajustes",
            "hide_settings" to "Ocultar ajustes",
            "stats" to "Hoy",
            "workout" to "Plan de ejercicio",
            "large" to "Texto grande",
            "contrast" to "Alto contraste",
            "english" to "Ingles",
            "spanish" to "Espanol",
            "french" to "Frances",
            "add_steps" to "+500 pasos",
            "sub_steps" to "-500 pasos",
            "add_calories" to "+100 calorias",
            "sub_calories" to "-100 calorias",
            "add_water" to "+1 agua",
            "sub_water" to "-1 agua",
            "save_plan" to "Guardar plan",
            "delete_plan" to "Borrar tarea",
            "steps" to "Pasos",
            "calories" to "Calorias comidas",
            "water" to "Agua",
            "plan_empty" to "No hay plan guardado.",
            "plan_hint" to "Escribe tu plan"
        ),
        "fr" to mapOf(
            "title" to "Suivi Fitness Accessible",
            "language" to "Langue",
            "accessibility" to "Accessibilite",
            "settings" to "Parametres",
            "hide_settings" to "Masquer parametres",
            "stats" to "Aujourd'hui",
            "workout" to "Plan sportif",
            "large" to "Grand texte",
            "contrast" to "Contraste eleve",
            "english" to "Anglais",
            "spanish" to "Espagnol",
            "french" to "Francais",
            "add_steps" to "+500 pas",
            "sub_steps" to "-500 pas",
            "add_calories" to "+100 calories",
            "sub_calories" to "-100 calories",
            "add_water" to "+1 eau",
            "sub_water" to "-1 eau",
            "save_plan" to "Enregistrer",
            "delete_plan" to "Supprimer",
            "steps" to "Pas",
            "calories" to "Calories mangees",
            "water" to "Eau",
            "plan_empty" to "Aucun plan enregistre.",
            "plan_hint" to "Ecris ton plan"
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        deviceMode = detectedDeviceMode()
        buildInterface()
        updateLanguage()
        updateStats()
        applyAccessibilityOptions()
    }

    private fun buildInterface() {
        scrollView = ScrollView(this).apply { isFillViewport = true }
        mainLayout = LinearLayout(this).apply {
            orientation = if (deviceMode == "tablet") LinearLayout.HORIZONTAL else LinearLayout.VERTICAL
            gravity = Gravity.TOP
            val sidePadding = if (deviceMode == "watch") 10 else 22
            val topPadding = when (deviceMode) {
                "watch" -> 14
                "tablet" -> 30
                else -> 58
            }
            setPadding(dp(sidePadding), dp(topPadding), dp(sidePadding), dp(40))
        }

        val controlsColumn = column()
        val statsColumn = column()

        titleText = heading(if (deviceMode == "watch") 22f else 29f)
        languageHeading = heading(19f)
        accessibilityHeading = heading(19f)
        statsHeading = heading(22f)
        workoutHeading = heading(20f)
        stepsText = statText()
        caloriesText = statText()
        waterText = statText()
        workoutText = body()
        workoutInput = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
            minLines = if (deviceMode == "watch") 1 else 2
            maxLines = 4
            setSingleLine(false)
            setPadding(dp(12), dp(10), dp(12), dp(10))
            background = roundedBackground(Color.WHITE, 14)
            textViews.add(this)
        }

        controlsColumn.addView(titleText)
        controlsColumn.addView(spacer(12))
        controlsColumn.addView(actionButton("settings") {
            settingsOpen = !settingsOpen
            settingsPanel.visibility = if (settingsOpen) View.VISIBLE else View.GONE
            updateLanguage()
        })

        settingsPanel = column().apply {
            visibility = if (settingsOpen) View.VISIBLE else View.GONE
            setPadding(dp(14), dp(12), dp(14), dp(14))
            background = roundedBackground(Color.rgb(226, 232, 240), 18)
        }
        settingsPanel.addView(languageHeading)
        settingsPanel.addView(buttonRow(listOf(languageButton("en"), languageButton("es"), languageButton("fr"))))
        settingsPanel.addView(spacer(10))
        settingsPanel.addView(accessibilityHeading)

        largeTextSwitch = Switch(this).apply { setOnCheckedChangeListener { _, _ -> applyAccessibilityOptions() } }
        highContrastSwitch = Switch(this).apply { setOnCheckedChangeListener { _, _ -> applyAccessibilityOptions() } }
        textViews.add(largeTextSwitch)
        textViews.add(highContrastSwitch)
        settingsPanel.addView(largeTextSwitch)
        settingsPanel.addView(highContrastSwitch)
        controlsColumn.addView(spacer(10))
        controlsColumn.addView(settingsPanel)

        statsColumn.addView(statsHeading)
        statsColumn.addView(statCard(stepsText, Color.rgb(219, 234, 254)))
        statsColumn.addView(actionRow(listOf(
            actionButton("add_steps") { steps += 500; updateStats() },
            actionButton("sub_steps") { steps = (steps - 500).coerceAtLeast(0); updateStats() }
        )))
        statsColumn.addView(statCard(caloriesText, Color.rgb(220, 252, 231)))
        statsColumn.addView(actionRow(listOf(
            actionButton("add_calories") { caloriesEaten += 100; updateStats() },
            actionButton("sub_calories") { caloriesEaten = (caloriesEaten - 100).coerceAtLeast(0); updateStats() }
        )))
        statsColumn.addView(statCard(waterText, Color.rgb(224, 242, 254)))
        statsColumn.addView(actionRow(listOf(
            actionButton("add_water") { water += 1; updateStats() },
            actionButton("sub_water") { water = (water - 1).coerceAtLeast(0); updateStats() }
        )))
        statsColumn.addView(spacer(14))
        statsColumn.addView(workoutHeading)
        statsColumn.addView(workoutInput)
        statsColumn.addView(actionRow(listOf(
            actionButton("save_plan") {
                workoutPlan = workoutInput.text.toString().trim()
                updateStats()
            },
            actionButton("delete_plan") {
                workoutPlan = ""
                workoutInput.setText("")
                updateStats()
            }
        )))
        statsColumn.addView(workoutText)

        if (deviceMode == "tablet") {
            mainLayout.addView(controlsColumn, weightedLayout())
            mainLayout.addView(statsColumn, weightedLayout())
        } else {
            mainLayout.addView(controlsColumn)
            mainLayout.addView(statsColumn)
        }

        scrollView.addView(mainLayout)
        setContentView(scrollView)
    }

    private fun updateLanguage() {
        titleText.text = text("title")
        languageHeading.text = text("language")
        accessibilityHeading.text = text("accessibility")
        statsHeading.text = text("stats")
        workoutHeading.text = text("workout")
        workoutInput.hint = text("plan_hint")
        largeTextSwitch.text = text("large")
        highContrastSwitch.text = text("contrast")

        languageButtons["en"]?.text = text("english")
        languageButtons["es"]?.text = text("spanish")
        languageButtons["fr"]?.text = text("french")
        actionButtons["settings"]?.text = text(if (settingsOpen) "hide_settings" else "settings")
        actionButtons["add_steps"]?.text = text("add_steps")
        actionButtons["sub_steps"]?.text = text("sub_steps")
        actionButtons["add_calories"]?.text = text("add_calories")
        actionButtons["sub_calories"]?.text = text("sub_calories")
        actionButtons["add_water"]?.text = text("add_water")
        actionButtons["sub_water"]?.text = text("sub_water")
        actionButtons["save_plan"]?.text = text("save_plan")
        actionButtons["delete_plan"]?.text = text("delete_plan")

        buttons.forEach { it.contentDescription = it.text }
        workoutInput.contentDescription = text("plan_hint")
        largeTextSwitch.contentDescription = text("large")
        highContrastSwitch.contentDescription = text("contrast")
    }

    private fun updateStats() {
        stepsText.text = "${text("steps")}: $steps"
        caloriesText.text = "${text("calories")}: $caloriesEaten"
        waterText.text = "${text("water")}: $water"
        workoutText.text = if (workoutPlan.isBlank()) text("plan_empty") else workoutPlan
        listOf(stepsText, caloriesText, waterText, workoutText).forEach { it.contentDescription = it.text }
    }

    private fun applyAccessibilityOptions() {
        if (!::largeTextSwitch.isInitialized || !::highContrastSwitch.isInitialized) return
        val highContrast = highContrastSwitch.isChecked
        val largeText = largeTextSwitch.isChecked
        val background = if (highContrast) Color.BLACK else Color.rgb(248, 250, 252)
        val foreground = if (highContrast) Color.WHITE else Color.rgb(22, 28, 36)
        val buttonBackground = if (highContrast) Color.rgb(32, 32, 32) else Color.rgb(37, 99, 235)

        scrollView.setBackgroundColor(background)
        mainLayout.setBackgroundColor(background)
        if (::settingsPanel.isInitialized) {
            settingsPanel.background = roundedBackground(if (highContrast) Color.rgb(20, 20, 20) else Color.rgb(226, 232, 240), 18)
        }
        if (::workoutInput.isInitialized) {
            workoutInput.background = roundedBackground(if (highContrast) Color.rgb(32, 32, 32) else Color.WHITE, 14)
            workoutInput.setHintTextColor(if (highContrast) Color.LTGRAY else Color.GRAY)
        }
        window.statusBarColor = background
        window.navigationBarColor = background
        window.decorView.systemUiVisibility = if (highContrast) 0 else View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR

        textViews.forEach { view ->
            view.setTextColor(foreground)
            val baseSize = when (view) {
                titleText -> if (deviceMode == "watch") 22f else 29f
                statsHeading, workoutHeading -> 21f
                languageHeading, accessibilityHeading -> 19f
                stepsText, caloriesText, waterText -> if (deviceMode == "watch") 17f else 20f
                else -> if (deviceMode == "watch") 14f else 16f
            }
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (largeText) baseSize + 5f else baseSize)
        }
        buttons.forEach { button ->
            button.setTextColor(Color.WHITE)
            button.background = roundedBackground(buttonBackground, 14)
            button.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (largeText) 16f else 13f)
            button.minHeight = dp(if (largeText) 56 else 48)
            button.setPadding(dp(8), 0, dp(8), 0)
        }
    }

    private fun languageButton(code: String): Button = Button(this).apply {
        languageButtons[code] = this
        buttons.add(this)
        setOnClickListener {
            language = code
            updateLanguage()
            updateStats()
            applyAccessibilityOptions()
        }
    }

    private fun actionButton(key: String, action: () -> Unit): Button = Button(this).apply {
        actionButtons[key] = this
        buttons.add(this)
        setAllCaps(false)
        setOnClickListener { action() }
    }

    private fun detectedDeviceMode(): String {
        val config = resources.configuration
        val widthDp = config.screenWidthDp
        val heightDp = config.screenHeightDp
        val smallestWidthDp = config.smallestScreenWidthDp
        val isWatch = packageManager.hasSystemFeature(PackageManager.FEATURE_WATCH) ||
            (widthDp <= 320 && heightDp <= 320)
        val isTablet = smallestWidthDp >= 600 || widthDp >= 720

        return when {
            isWatch -> "watch"
            isTablet -> "tablet"
            else -> "phone"
        }
    }

    private fun column(): LinearLayout = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(dp(6), dp(6), dp(6), dp(6))
    }

    private fun actionRow(rowButtons: List<Button>): LinearLayout = LinearLayout(this).apply {
        orientation = if (deviceMode == "watch") LinearLayout.VERTICAL else LinearLayout.HORIZONTAL
        rowButtons.forEach { button ->
            val width = if (deviceMode == "watch") ViewGroup.LayoutParams.MATCH_PARENT else 0
            val weight = if (deviceMode == "watch") 0f else 1f
            addView(button, LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT, weight).apply {
                setMargins(dp(4), dp(4), dp(4), dp(4))
            })
        }
    }

    private fun statCard(content: TextView, color: Int): LinearLayout = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(dp(10), dp(8), dp(10), dp(8))
        background = roundedBackground(color, 16)
        addView(content)
        layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(0, dp(4), 0, dp(6))
        }
    }

    private fun buttonRow(rowButtons: List<Button>): LinearLayout = LinearLayout(this).apply {
        orientation = if (deviceMode == "watch") LinearLayout.VERTICAL else LinearLayout.HORIZONTAL
        rowButtons.forEach { button ->
            val width = if (deviceMode == "watch") ViewGroup.LayoutParams.MATCH_PARENT else 0
            val weight = if (deviceMode == "watch") 0f else 1f
            addView(button, LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT, weight).apply {
                setMargins(dp(4), dp(4), dp(4), dp(4))
            })
        }
    }

    private fun heading(size: Float): TextView = TextView(this).apply {
        setTypeface(typeface, Typeface.BOLD)
        setTextSize(TypedValue.COMPLEX_UNIT_SP, size)
        setPadding(0, dp(5), 0, dp(5))
        textViews.add(this)
    }

    private fun body(): TextView = TextView(this).apply {
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        setPadding(0, dp(6), 0, dp(6))
        textViews.add(this)
    }

    private fun statText(): TextView = TextView(this).apply {
        setTypeface(typeface, Typeface.BOLD)
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
        setPadding(dp(12), dp(10), dp(12), dp(10))
        textViews.add(this)
    }

    private fun roundedBackground(color: Int, radius: Int): GradientDrawable = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        cornerRadius = dp(radius).toFloat()
        setColor(color)
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

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}


