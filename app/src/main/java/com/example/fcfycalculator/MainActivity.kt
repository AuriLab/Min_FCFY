@file:Suppress("SpellCheckingInspection")

package com.example.fcfycalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.round

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isDark = isSystemInDarkTheme()
            MaterialTheme(
                colorScheme = if (isDark) AppDarkColorScheme else AppLightColorScheme
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainAppScreen()
                }
            }
        }
    }
}

private val AppLightColorScheme = lightColorScheme(
    primary = Color(0xFF0F4C81),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6E4FF),
    onPrimaryContainer = Color(0xFF001B3D),
    secondary = Color(0xFF006C4C),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFA8C8FF),
    onSecondaryContainer = Color(0xFF002114),
    error = Color(0xFFBA1A1A),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFF8F9FF),
    surface = Color.White,
    onSurface = Color(0xFF191C20),
    surfaceVariant = Color(0xFFEEF1F8),
    onSurfaceVariant = Color(0xFF43474E)
)

private val AppDarkColorScheme = darkColorScheme(
    primary = Color(0xFF45FC20),
    onPrimary = Color(0xFF003062),
    primaryContainer = Color(0xFF00468A),
    onPrimaryContainer = Color(0xFFD6E4FF),
    secondary = Color(0xFFA8C8FF),
    onSecondary = Color(0xFF003826),
    secondaryContainer = Color(0xFF005138),
    onSecondaryContainer = Color(0xFFA8C8FF),
    error = Color(0xFFFFB4AB),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF111318),
    surface = Color(0xFF1D2024),
    onSurface = Color(0xFFE2E2E9),
    surfaceVariant = Color(0xFF282C33),
    onSurfaceVariant = Color(0xFFC3C6CF)
)

enum class CalculatorScreen {
    Fcfy,
    Banks
}

@Composable
fun MainAppScreen() {
    var currentScreen by remember { mutableStateOf(CalculatorScreen.Fcfy) }

    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (currentScreen == CalculatorScreen.Fcfy) "Minimum FCFY Calculator" else "Max P/TBV Bank Calculator",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Button(
                        onClick = {
                            currentScreen = if (currentScreen == CalculatorScreen.Fcfy) {
                                CalculatorScreen.Banks
                            } else {
                                CalculatorScreen.Fcfy
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(text = if (currentScreen == CalculatorScreen.Fcfy) "Banks" else "FCFY")
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (currentScreen == CalculatorScreen.Fcfy) {
                MinFcfyCalculatorScreenContent()
            } else {
                BanksCalculatorScreen()
            }
        }
    }
}

data class DcfCalculationResult(
    val isDivergent: Boolean,
    val errorMessage: String? = null,
    val minFcfyBase0Percent: Double = 0.0,
    val minFcfyForward1Percent: Double = 0.0,
    val intrinsicValueFactor: Double = 0.0,
    val safeValueFactor: Double = 0.0,
    val pvPhase1: Double = 0.0,
    val pvPhase2: Double = 0.0,
    val pvTerminal: Double = 0.0,
    val nominalFcfSeries: List<Double> = emptyList(),
    val discountedPvSeries: List<Double> = emptyList()
)

object DcfCalculatorEngine {
    private const val EPSILON: Double = 0.0001

    fun calculate(
        r: Double,
        g: Double,
        h: Double,
        t: Double,
        mos: Double
    ): DcfCalculationResult {
        val nominalSeries = ArrayList<Double>(11)
        val pvSeries = ArrayList<Double>(11)

        var currentFcf = 1.0
        nominalSeries.add(currentFcf)
        pvSeries.add(currentFcf)

        for (year in 1..10) {
            val growthRate: Double = if (year <= 5) g else h
            currentFcf *= (1.0 + growthRate)
            val discountFactor: Double = (1.0 + r).pow(year.toDouble())
            nominalSeries.add(currentFcf)
            pvSeries.add(currentFcf / discountFactor)
        }

        if (r <= t + 0.000001) {
            return DcfCalculationResult(
                isDivergent = true,
                errorMessage = "Target IRR (R) must be greater than Terminal Growth (t).",
                nominalFcfSeries = nominalSeries,
                discountedPvSeries = pvSeries
            )
        }

        val pvPhase1Unit: Double = if (abs(r - g) < EPSILON) {
            5.0 / (1.0 + r)
        } else {
            (1.0 - ((1.0 + g) / (1.0 + r)).pow(5.0)) / (r - g)
        }
        val pvPhase1: Double = (1.0 + g) * pvPhase1Unit

        val pvPhase2Unit: Double = if (abs(r - h) < EPSILON) {
            5.0 * (1.0 + g).pow(4.0) / (1.0 + r).pow(5.0)
        } else {
            val factor: Double = ((1.0 + g).pow(4.0) * (1.0 + h)) / ((1.0 + r).pow(5.0) * (r - h))
            val geom: Double = 1.0 - ((1.0 + h) / (1.0 + r)).pow(5.0)
            factor * geom
        }
        val pvPhase2: Double = (1.0 + g) * pvPhase2Unit

        val fcfYear10: Double = (1.0 + g).pow(5.0) * (1.0 + h).pow(5.0)
        val terminalValueYear10: Double = (fcfYear10 * (1.0 + t)) / (r - t)
        val pvTerminal: Double = terminalValueYear10 / (1.0 + r).pow(10.0)

        val intrinsicValueFactor: Double = pvPhase1 + pvPhase2 + pvTerminal
        val safeValueFactor: Double = intrinsicValueFactor * (1.0 - mos)

        val minFcfyBase0: Double = if (safeValueFactor > 0.0) {
            (1.0 / safeValueFactor) * 100.0
        } else {
            0.0
        }

        val minFcfyForward1: Double = minFcfyBase0 * (1.0 + g)

        return DcfCalculationResult(
            isDivergent = false,
            minFcfyBase0Percent = minFcfyBase0,
            minFcfyForward1Percent = minFcfyForward1,
            intrinsicValueFactor = intrinsicValueFactor,
            safeValueFactor = safeValueFactor,
            pvPhase1 = pvPhase1,
            pvPhase2 = pvPhase2,
            pvTerminal = pvTerminal,
            nominalFcfSeries = nominalSeries,
            discountedPvSeries = pvSeries
        )
    }
}

data class BanksCalculationResult(
    val isDivergent: Boolean,
    val errorMessage: String? = null,
    val maxPtBv: Double = 0.0,
    val a1: Double = 0.0,
    val a2: Double = 0.0,
    val a3: Double = 0.0,
    val bvSeries: List<Double> = emptyList()
)

object BanksCalculationEngine {
    fun calculate(
        roe: Double,
        r: Double,
        g: Double,
        h: Double,
        t: Double
    ): BanksCalculationResult {
        if (r <= t + 0.000001) {
            return BanksCalculationResult(
                isDivergent = true,
                errorMessage = "Target Cost of Equity (R) must be greater than Terminal Growth (t)."
            )
        }

        val bvSeries = ArrayList<Double>(11)
        var currentBv = 1.0
        bvSeries.add(currentBv)

        var a1 = 0.0
        var a2 = 0.0

        for (year in 1..10) {
            val growthRate = if (year <= 5) g else h
            val prevBv = currentBv
            currentBv *= (1.0 + growthRate)
            bvSeries.add(currentBv)

            val discountFactor = (1.0 + r).pow(year.toDouble())
            val excessReturnPv = prevBv / discountFactor

            if (year <= 5) {
                a1 += excessReturnPv
            } else {
                a2 += excessReturnPv
            }
        }

        val bv10 = bvSeries[10]
        val terminalExcessReturnBase = bv10 * ((1.0 + t) / (r - t))
        val a3 = terminalExcessReturnBase / (1.0 + r).pow(10.0)

        val excessReturnFactor = a1 + a2 + a3
        val maxPtBv = 1.0 + (roe - r) * excessReturnFactor

        return BanksCalculationResult(
            isDivergent = false,
            maxPtBv = maxPtBv,
            a1 = a1,
            a2 = a2,
            a3 = a3,
            bvSeries = bvSeries
        )
    }
}

private fun snapToStep(value: Float, step: Float, min: Float, max: Float): Float {
    val snapped = round(value / step) * step
    val clean = round(snapped * 100f) / 100f
    return clean.coerceIn(min, max)
}

@Composable
fun MinFcfyCalculatorScreenContent() {
    var targetIrrPercent: Float by remember { mutableFloatStateOf(11.0f) }
    var phase1GrowthPercent: Float by remember { mutableFloatStateOf(8.0f) }
    var terminalGrowthPercent: Float by remember { mutableFloatStateOf(3.0f) }
    var marginOfSafetyPercent: Float by remember { mutableFloatStateOf(15.0f) }
    var manualPhase2GrowthPercent: Float by remember { mutableFloatStateOf(5.5f) }

    var syncPhase2: Boolean by remember { mutableStateOf(true) }

    val effectivePhase2Percent: Float by remember {
        derivedStateOf {
            if (syncPhase2) {
                (phase1GrowthPercent + terminalGrowthPercent) / 2.0f
            } else {
                manualPhase2GrowthPercent
            }
        }
    }

    val dcfResult: DcfCalculationResult by remember {
        derivedStateOf {
            DcfCalculatorEngine.calculate(
                r = targetIrrPercent.toDouble() / 100.0,
                g = phase1GrowthPercent.toDouble() / 100.0,
                h = effectivePhase2Percent.toDouble() / 100.0,
                t = terminalGrowthPercent.toDouble() / 100.0,
                mos = marginOfSafetyPercent.toDouble() / 100.0
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HeroResultCard(result = dcfResult)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Valuation Parameters",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                // 1. Target IRR (R): 0.1% increments
                ParameterSlider(
                    label = "Targeted Minimum IRR (R)",
                    value = targetIrrPercent,
                    onValueChange = { targetIrrPercent = snapToStep(it, 0.1f, 0f, 20f) },
                    valueRange = 0f..20f,
                    stepIncrement = 0.1f,
                    steps = 199
                )

                SectionDivider()

                // 2. Phase 1 Growth (g): 0.5% increments
                ParameterSlider(
                    label = "Phase 1 Growth Rate (g, Yrs 1–5)",
                    value = phase1GrowthPercent,
                    onValueChange = { phase1GrowthPercent = snapToStep(it, 0.5f, 0f, 40f) },
                    valueRange = 0f..40f,
                    stepIncrement = 0.5f,
                    steps = 79
                )

                SectionDivider()

                // 3. Phase 2 Growth (h): Synced (.25% exact average) or Manual (0.5% increments)
                val hasQuarterPercent: Boolean =
                    abs(effectivePhase2Percent * 10f - round(effectivePhase2Percent * 10f)) > 0.01f
                val phase2DisplayText: String = if (syncPhase2 && hasQuarterPercent) {
                    String.format(Locale.US, "%.2f%%", effectivePhase2Percent)
                } else {
                    String.format(Locale.US, "%.1f%%", effectivePhase2Percent)
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Phase 2 Growth Rate (h, Yrs 6–10)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (syncPhase2) "Synced: (g + t) / 2" else "Manual (0.5% steps)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = phase2DisplayText,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (syncPhase2) {
                                MaterialTheme.colorScheme.secondary
                            } else {
                                MaterialTheme.colorScheme.primary
                            }
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = syncPhase2,
                            onCheckedChange = { isChecked ->
                                if (!isChecked) {
                                    manualPhase2GrowthPercent =
                                        snapToStep(effectivePhase2Percent, 0.5f, 0f, 40f)
                                }
                                syncPhase2 = isChecked
                            }
                        )
                        Text(
                            text = "Sync Phase 2 to average of g and t",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StepperButton(
                            symbol = "−",
                            enabled = effectivePhase2Percent > 0.0f,
                            onClick = {
                                val baseVal = effectivePhase2Percent
                                syncPhase2 = false
                                manualPhase2GrowthPercent =
                                    snapToStep(baseVal - 0.5f, 0.5f, 0f, 40f)
                            }
                        )

                        val isDark = isSystemInDarkTheme()
                        Slider(
                            modifier = Modifier.weight(1f),
                            value = effectivePhase2Percent,
                            onValueChange = { newValue ->
                                if (!syncPhase2) {
                                    manualPhase2GrowthPercent =
                                        snapToStep(newValue, 0.5f, 0f, 40f)
                                }
                            },
                            enabled = !syncPhase2,
                            valueRange = 0f..40f,
                            steps = 79,
                            colors = if (isDark) {
                                SliderDefaults.colors(
                                    activeTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                                    inactiveTrackColor = MaterialTheme.colorScheme.onSecondary
                                )
                            } else {
                                SliderDefaults.colors()
                            }
                        )

                        StepperButton(
                            symbol = "+",
                            enabled = effectivePhase2Percent < 40.0f,
                            onClick = {
                                val baseVal = effectivePhase2Percent
                                syncPhase2 = false
                                manualPhase2GrowthPercent =
                                    snapToStep(baseVal + 0.5f, 0.5f, 0f, 40f)
                            }
                        )
                    }
                }

                SectionDivider()

                // 4. Terminal Growth (t): 0.5% increments
                ParameterSlider(
                    label = "Terminal Growth Rate (t, Yr 10+)",
                    value = terminalGrowthPercent,
                    onValueChange = { terminalGrowthPercent = snapToStep(it, 0.5f, 0f, 5f) },
                    valueRange = 0f..5f,
                    stepIncrement = 0.5f,
                    steps = 9
                )

                SectionDivider()

                // 5. Margin of Safety (MOS): 0.5% increments
                ParameterSlider(
                    label = "Margin of Safety (MOS)",
                    value = marginOfSafetyPercent,
                    onValueChange = { marginOfSafetyPercent = snapToStep(it, 0.5f, 0f, 30f) },
                    valueRange = 0f..30f,
                    stepIncrement = 0.5f,
                    steps = 59
                )
            }
        }

        ProjectionChartCard(
            result = dcfResult,
            gPercent = phase1GrowthPercent,
            hPercent = effectivePhase2Percent
        )

        Text(
            text = "Note: This is only a first estimate of the Minimum FCFY to achieve the targeted IRR. You need to conduct a careful DCF analysis to adjust for any key factors like Capex, Net Borrowing, growth trajectory and other key assumptions.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 4.dp)
        )
    }
}

@Composable
fun BanksCalculatorScreen() {
    var roePercent: Float by remember { mutableFloatStateOf(12.0f) }
    var costOfEquityPercent: Float by remember { mutableFloatStateOf(10.0f) }
    var phase1GrowthPercent: Float by remember { mutableFloatStateOf(8.0f) }
    var terminalGrowthPercent: Float by remember { mutableFloatStateOf(3.0f) }
    var manualPhase2GrowthPercent: Float by remember { mutableFloatStateOf(5.5f) }

    var syncPhase2: Boolean by remember { mutableStateOf(true) }

    val effectivePhase2Percent: Float by remember {
        derivedStateOf {
            if (syncPhase2) {
                (phase1GrowthPercent + terminalGrowthPercent) / 2.0f
            } else {
                manualPhase2GrowthPercent
            }
        }
    }

    val banksResult: BanksCalculationResult by remember {
        derivedStateOf {
            BanksCalculationEngine.calculate(
                roe = roePercent.toDouble() / 100.0,
                r = costOfEquityPercent.toDouble() / 100.0,
                g = phase1GrowthPercent.toDouble() / 100.0,
                h = effectivePhase2Percent.toDouble() / 100.0,
                t = terminalGrowthPercent.toDouble() / 100.0
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BanksHeroResultCard(result = banksResult, roe = roePercent, r = costOfEquityPercent)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Bank Valuation Parameters",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                // 1. Return on Equity (ROE): 0.5% increments
                ParameterSlider(
                    label = "Return on Equity (ROE)",
                    value = roePercent,
                    onValueChange = { roePercent = snapToStep(it, 0.5f, 0f, 30f) },
                    valueRange = 0f..30f,
                    stepIncrement = 0.5f,
                    steps = 59
                )

                SectionDivider()

                // 2. Target Cost of Equity (R): 0.1% increments
                ParameterSlider(
                    label = "Target Cost of Equity (R)",
                    value = costOfEquityPercent,
                    onValueChange = { costOfEquityPercent = snapToStep(it, 0.1f, 0f, 20f) },
                    valueRange = 0f..20f,
                    stepIncrement = 0.1f,
                    steps = 199
                )

                SectionDivider()

                // 3. Phase 1 Growth (g): 0.5% increments
                ParameterSlider(
                    label = "Phase 1 Growth Rate (g, Yrs 1–5)",
                    value = phase1GrowthPercent,
                    onValueChange = { phase1GrowthPercent = snapToStep(it, 0.5f, 0f, 40f) },
                    valueRange = 0f..40f,
                    stepIncrement = 0.5f,
                    steps = 79
                )

                SectionDivider()

                // 4. Phase 2 Growth (h): Synced (.25% exact average) or Manual (0.5% increments)
                val hasQuarterPercent: Boolean =
                    abs(effectivePhase2Percent * 10f - round(effectivePhase2Percent * 10f)) > 0.01f
                val phase2DisplayText: String = if (syncPhase2 && hasQuarterPercent) {
                    String.format(Locale.US, "%.2f%%", effectivePhase2Percent)
                } else {
                    String.format(Locale.US, "%.1f%%", effectivePhase2Percent)
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Phase 2 Growth Rate (h, Yrs 6–10)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (syncPhase2) "Synced: (g + t) / 2" else "Manual (0.5% steps)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = phase2DisplayText,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (syncPhase2) {
                                MaterialTheme.colorScheme.secondary
                            } else {
                                MaterialTheme.colorScheme.primary
                            }
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = syncPhase2,
                            onCheckedChange = { isChecked ->
                                if (!isChecked) {
                                    manualPhase2GrowthPercent =
                                        snapToStep(effectivePhase2Percent, 0.5f, 0f, 40f)
                                }
                                syncPhase2 = isChecked
                            }
                        )
                        Text(
                            text = "Sync Phase 2 to average of g and t",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StepperButton(
                            symbol = "−",
                            enabled = effectivePhase2Percent > 0.0f,
                            onClick = {
                                val baseVal = effectivePhase2Percent
                                syncPhase2 = false
                                manualPhase2GrowthPercent =
                                    snapToStep(baseVal - 0.5f, 0.5f, 0f, 40f)
                            }
                        )

                        val isDark = isSystemInDarkTheme()
                        Slider(
                            modifier = Modifier.weight(1f),
                            value = effectivePhase2Percent,
                            onValueChange = { newValue ->
                                if (!syncPhase2) {
                                    manualPhase2GrowthPercent =
                                        snapToStep(newValue, 0.5f, 0f, 40f)
                                }
                            },
                            enabled = !syncPhase2,
                            valueRange = 0f..40f,
                            steps = 79,
                            colors = if (isDark) {
                                SliderDefaults.colors(
                                    activeTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                                    inactiveTrackColor = MaterialTheme.colorScheme.onSecondary
                                )
                            } else {
                                SliderDefaults.colors()
                            }
                        )

                        StepperButton(
                            symbol = "+",
                            enabled = effectivePhase2Percent < 40.0f,
                            onClick = {
                                val baseVal = effectivePhase2Percent
                                syncPhase2 = false
                                manualPhase2GrowthPercent =
                                    snapToStep(baseVal + 0.5f, 0.5f, 0f, 40f)
                            }
                        )
                    }
                }

                SectionDivider()

                // 5. Terminal Growth (t): 0.5% increments
                ParameterSlider(
                    label = "Terminal Growth Rate (t, Yr 10+)",
                    value = terminalGrowthPercent,
                    onValueChange = { terminalGrowthPercent = snapToStep(it, 0.5f, 0f, 5f) },
                    valueRange = 0f..5f,
                    stepIncrement = 0.5f,
                    steps = 9
                )
            }
        }

        Text(
            text = "Note: This is a Residual Income / Excess Return model estimate for financial institutions (Banks). It calculates Maximum Targeted P/TBV based on ROE, Cost of Equity (R), and growth trajectories. Conduct careful credit and balance sheet analysis before making investment decisions.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 4.dp)
        )
    }
}

@Composable
private fun SectionDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    )
}

@Composable
private fun HeroResultCard(result: DcfCalculationResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (result.isDivergent) {
                MaterialTheme.colorScheme.errorContainer
            } else {
                MaterialTheme.colorScheme.primaryContainer
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "MINIMUM REQUIRED FCF YIELD",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.1.sp,
                color = if (result.isDivergent) {
                    MaterialTheme.colorScheme.onErrorContainer
                } else {
                    MaterialTheme.colorScheme.onPrimaryContainer
                }
            )

            if (result.isDivergent) {
                Text(
                    text = "Infinity / N/A",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    text = result.errorMessage ?: "Invalid parameters",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            } else {
                Text(
                    text = String.format(Locale.US, "%.2f%%", result.minFcfyForward1Percent),
                    fontSize = 44.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = String.format(
                        Locale.US,
                        "FCF₀ Yield @ 0%% MOS: %.2f%%",
                        result.minFcfyBase0Percent
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(vertical = 10.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MetricColumn(
                        modifier = Modifier.weight(1f),
                        topLine = "Intrinsic",
                        secondLine = "Factor",
                        value = String.format(Locale.US, "%.2fx", result.intrinsicValueFactor)
                    )
                    MetricColumn(
                        modifier = Modifier.weight(1f),
                        topLine = "Safe",
                        secondLine = "Factor",
                        value = String.format(Locale.US, "%.2fx", result.safeValueFactor)
                    )
                    val tvWeight: Double = if (result.intrinsicValueFactor > 0.0) {
                        (result.pvTerminal / result.intrinsicValueFactor) * 100.0
                    } else {
                        0.0
                    }
                    MetricColumn(
                        modifier = Modifier.weight(1f),
                        topLine = "Terminal",
                        secondLine = "Weight",
                        value = String.format(Locale.US, "%.0f%%", tvWeight)
                    )
                }
            }
        }
    }
}

@Composable
private fun BanksHeroResultCard(result: BanksCalculationResult, roe: Float, r: Float) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (result.isDivergent) {
                MaterialTheme.colorScheme.errorContainer
            } else {
                MaterialTheme.colorScheme.primaryContainer
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "MAXIMUM TARGETED P/TBV",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.1.sp,
                color = if (result.isDivergent) {
                    MaterialTheme.colorScheme.onErrorContainer
                } else {
                    MaterialTheme.colorScheme.onPrimaryContainer
                }
            )

            if (result.isDivergent) {
                Text(
                    text = "Infinity / N/A",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    text = result.errorMessage ?: "Invalid parameters",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            } else {
                Text(
                    text = String.format(Locale.US, "%.2fx", result.maxPtBv),
                    fontSize = 44.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = String.format(Locale.US, "ROE: %.1f%%  •  Cost of Equity (R): %.1f%%", roe, r),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(vertical = 10.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MetricColumn(
                        modifier = Modifier.weight(1f),
                        topLine = "Phase 1 (A1)",
                        secondLine = "Factor",
                        value = String.format(Locale.US, "%.3f", result.a1)
                    )
                    MetricColumn(
                        modifier = Modifier.weight(1f),
                        topLine = "Phase 2 (A2)",
                        secondLine = "Factor",
                        value = String.format(Locale.US, "%.3f", result.a2)
                    )
                    MetricColumn(
                        modifier = Modifier.weight(1f),
                        topLine = "Terminal (A3)",
                        secondLine = "Factor",
                        value = String.format(Locale.US, "%.3f", result.a3)
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricColumn(
    modifier: Modifier = Modifier,
    topLine: String,
    secondLine: String,
    value: String
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = topLine,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = secondLine,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ParameterSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    stepIncrement: Float,
    steps: Int
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = String.format(Locale.US, "%.1f%%", value),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StepperButton(
                symbol = "−",
                enabled = value > valueRange.start,
                onClick = {
                    onValueChange(
                        snapToStep(
                            value = value - stepIncrement,
                            step = stepIncrement,
                            min = valueRange.start,
                            max = valueRange.endInclusive
                        )
                    )
                }
            )

            val isDark = isSystemInDarkTheme()
            Slider(
                modifier = Modifier.weight(1f),
                value = value,
                onValueChange = onValueChange,
                valueRange = valueRange,
                steps = steps,
                colors = if (isDark) {
                    SliderDefaults.colors(
                        activeTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                        inactiveTrackColor = MaterialTheme.colorScheme.onSecondary
                    )
                } else {
                    SliderDefaults.colors()
                }
            )

            StepperButton(
                symbol = "+",
                enabled = value < valueRange.endInclusive,
                onClick = {
                    onValueChange(
                        snapToStep(
                            value = value + stepIncrement,
                            step = stepIncrement,
                            min = valueRange.start,
                            max = valueRange.endInclusive
                        )
                    )
                }
            )
        }
    }
}

@Composable
private fun StepperButton(
    symbol: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val containerColor = if (enabled) {
        MaterialTheme.colorScheme.surfaceVariant
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    }
    val contentColor = if (enabled) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
    }

    Surface(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .clickable(enabled = enabled, onClick = onClick),
        shape = CircleShape,
        color = containerColor,
        border = BorderStroke(
            width = 1.dp,
            color = contentColor.copy(alpha = 0.3f)
        )
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = symbol,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = contentColor
            )
        }
    }
}

@Composable
private fun ProjectionChartCard(
    result: DcfCalculationResult,
    gPercent: Float,
    hPercent: Float
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "10-Year Cash Flow Projection (Base Yr 0 = 1.00)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LegendItem(color = MaterialTheme.colorScheme.primary, label = "Nominal FCF")
                LegendItem(color = MaterialTheme.colorScheme.secondary, label = "Discounted PV")
            }

            val primaryColor: Color = MaterialTheme.colorScheme.primary
            val secondaryColor: Color = MaterialTheme.colorScheme.secondary
            val gridColor: Color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.25f)

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(vertical = 8.dp)
            ) {
                val nominal: List<Double> = result.nominalFcfSeries
                val pv: List<Double> = result.discountedPvSeries

                if (nominal.size >= 11 && pv.size >= 11) {
                    val maxNominal: Double = nominal.maxOrNull() ?: 1.0
                    val maxPv: Double = pv.maxOrNull() ?: 1.0
                    val maxY: Double = max(maxNominal, maxPv).coerceAtLeast(1.2)
                    val minY = 0.0

                    val chartWidth: Float = size.width
                    val chartHeight: Float = size.height

                    for (i in 0..4) {
                        val y: Float = chartHeight * (i.toFloat() / 4f)
                        drawLine(
                            color = gridColor,
                            start = Offset(0f, y),
                            end = Offset(chartWidth, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    val year5X: Float = chartWidth * 0.5f
                    drawLine(
                        color = gridColor.copy(alpha = 0.55f),
                        start = Offset(year5X, 0f),
                        end = Offset(year5X, chartHeight),
                        strokeWidth = 1.5.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )

                    val nominalPath = Path()
                    val pvPath = Path()

                    for (idx in 0..10) {
                        val x: Float = chartWidth * (idx.toFloat() / 10f)

                        val nomNorm: Float = ((nominal[idx] - minY) / (maxY - minY))
                            .toFloat()
                            .coerceIn(0f, 1f)
                        val nomY: Float = chartHeight * (1f - nomNorm)

                        val pvNorm: Float = ((pv[idx] - minY) / (maxY - minY))
                            .toFloat()
                            .coerceIn(0f, 1f)
                        val pvY: Float = chartHeight * (1f - pvNorm)

                        if (idx == 0) {
                            nominalPath.moveTo(x, nomY)
                            pvPath.moveTo(x, pvY)
                        } else {
                            nominalPath.lineTo(x, nomY)
                            pvPath.lineTo(x, pvY)
                        }
                    }

                    drawPath(
                        path = nominalPath,
                        color = primaryColor,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    drawPath(
                        path = pvPath,
                        color = secondaryColor,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    for (idx in 0..10) {
                        val x: Float = chartWidth * (idx.toFloat() / 10f)
                        val nomY: Float =
                            chartHeight * (1f - ((nominal[idx] - minY) / (maxY - minY)).toFloat()
                                .coerceIn(0f, 1f))
                        val pvY: Float =
                            chartHeight * (1f - ((pv[idx] - minY) / (maxY - minY)).toFloat()
                                .coerceIn(0f, 1f))

                        drawCircle(
                            color = primaryColor,
                            radius = 4.dp.toPx(),
                            center = Offset(x, nomY)
                        )
                        drawCircle(
                            color = secondaryColor,
                            radius = 4.dp.toPx(),
                            center = Offset(x, pvY)
                        )
                    }
                }
            }

            val nom5: Double = result.nominalFcfSeries.getOrElse(5) { 0.0 }
            val nom10: Double = result.nominalFcfSeries.getOrElse(10) { 0.0 }
            val hasQuarterPercent: Boolean =
                abs(hPercent * 10f - round(hPercent * 10f)) > 0.01f
            val hFormat: String = if (hasQuarterPercent) "%.2f%%" else "%.1f%%"

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Yr 0: 1.00",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = String.format(Locale.US, "Yr 5 (g=%.1f%%): %.2f", gPercent, nom5),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = String.format(Locale.US, "Yr 10 (h=$hFormat): %.2f", hPercent, nom10),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color = color, shape = CircleShape)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
