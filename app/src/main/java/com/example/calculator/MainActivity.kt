package com.example.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.ui.theme.CalculatorTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalculatorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CalculatorApp()
                }
            }
        }
    }
}

@Composable
fun CalculatorApp() {
    var input by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("0") }
    var operation by remember { mutableStateOf("") }
    var num1 by remember { mutableStateOf<Double?>(null) }
    var errorMessage by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    // Handle error message timeout
    if (showError) {
        LaunchedEffect(showError) {
            delay(2000)
            showError = false
        }
    }

    fun formatNumber(number: String): String {
        return if (number.endsWith(".0")) number.substring(0, number.length - 2) else number
    }

    fun calculate() {
        if (num1 == null || input.isEmpty() || operation.isEmpty()) {
            errorMessage = "Invalid operation"
            showError = true
            return
        }

        try {
            val num2 = input.toDouble()
            val calculatedResult = when (operation) {
                "+" -> (num1!! + num2)
                "-" -> (num1!! - num2)
                "×" -> (num1!! * num2)
                "÷" -> if (num2 == 0.0) Double.NaN else (num1!! / num2)
                "%" -> (num1!! % num2)
                else -> Double.NaN
            }

            result = when {
                calculatedResult.isNaN() -> "Error"
                operation == "÷" && num2 == 0.0 -> "Cannot divide by zero"
                calculatedResult % 1 == 0.0 -> calculatedResult.toLong().toString()
                else -> calculatedResult.toString()
            }

            input = result
            num1 = null
            operation = ""
        } catch (e: Exception) {
            errorMessage = "Calculation error"
            showError = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .width(IntrinsicSize.Max),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = "Calculator - 5025221005",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // Display area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
            ) {
                // Input display
                Text(
                    text = if (num1 != null) "${formatNumber(num1!!.toString())} $operation ${formatNumber(input)}"
                    else formatNumber(input.ifEmpty { "0" }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .padding(8.dp),
                    fontSize = 24.sp,
                    textAlign = TextAlign.End,
                    maxLines = 1
                )

                // Result display
                Text(
                    text = formatNumber(result),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .padding(8.dp),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End,
                    maxLines = 1
                )
            }

            // Error message
            if (showError) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Calculator buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val buttons = listOf(
                    listOf("C", "⌫", "%", "÷"),
                    listOf("7", "8", "9", "×"),
                    listOf("4", "5", "6", "-"),
                    listOf("1", "2", "3", "+"),
                    listOf("0", ".", "=")
                )

                buttons.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        row.forEach { label ->
                            val weight = if (label == "=" || label == "0") 2f else 1f
                            val color = when (label) {
                                "C", "⌫" -> MaterialTheme.colorScheme.errorContainer
                                "=" -> MaterialTheme.colorScheme.primaryContainer
                                "+", "-", "×", "÷", "%" -> MaterialTheme.colorScheme.secondaryContainer
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }

                            Button(
                                onClick = {
                                    when (label) {
                                        "C" -> {
                                            input = ""
                                            result = "0"
                                            showError = false
                                            num1 = null
                                            operation = ""
                                        }
                                        "⌫" -> {
                                            if (input.isNotEmpty()) {
                                                input = input.dropLast(1)
                                            }
                                        }
                                        "+", "-", "×", "÷", "%" -> {
                                            if (input.isNotEmpty()) {
                                                num1 = input.toDouble()
                                                operation = label
                                                input = ""
                                            }
                                        }
                                        "=" -> {
                                            if (input.isNotEmpty()) {
                                                calculate()
                                            }
                                        }
                                        "." -> {
                                            if (input.isEmpty()) {
                                                input = "0."
                                            } else if (!input.contains(".")) {
                                                input += "."
                                            }
                                        }
                                        else -> {
                                            if (input == "0" && label != "0") {
                                                input = label
                                            } else {
                                                input += label
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .weight(weight)
                                    .aspectRatio(if (weight == 2f) 2f else 1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = color,
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CalculatorTheme {
        CalculatorApp()
    }
}