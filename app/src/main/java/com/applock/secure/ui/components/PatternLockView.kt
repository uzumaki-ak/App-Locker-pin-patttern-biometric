package com.applock.secure.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.sqrt

/**
 * Pattern Lock Component
 * 3x3 grid pattern drawing
 * Connects dots as user drags
 */
@Composable
fun PatternLockView(
    onPatternComplete: (String) -> Unit,
    onPatternChange: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedDots by remember { mutableStateOf(setOf<Int>()) }
    var currentPosition by remember { mutableStateOf<Offset?>(null) }
    var isDragging by remember { mutableStateOf(false) }

    val primaryColor = MaterialTheme.colorScheme.primary
    val dotRadius = 20f
    val gridSize = 3

    BoxWithConstraints(
        modifier = modifier
            .size(300.dp)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        isDragging = true
                        currentPosition = offset
                        val dot = getDotAtPosition(offset, size.width.toFloat(), gridSize, dotRadius)
                        if (dot != null) {
                            selectedDots = setOf(dot)
                            onPatternChange(selectedDots.joinToString(""))
                        }
                    },
                    onDrag = { change, _ ->
                        currentPosition = change.position
                        val dot = getDotAtPosition(change.position, size.width.toFloat(), gridSize, dotRadius)
                        if (dot != null && !selectedDots.contains(dot)) {
                            selectedDots = selectedDots + dot
                            onPatternChange(selectedDots.joinToString(""))
                        }
                    },
                    onDragEnd = {
                        isDragging = false
                        currentPosition = null
                        if (selectedDots.size >= 4) {
                            val pattern = selectedDots.joinToString("")
                            onPatternComplete(pattern)
                        }
                        selectedDots = emptySet()
                    },
                    onDragCancel = {
                        isDragging = false
                        currentPosition = null
                        selectedDots = emptySet()
                    }
                )
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val spacing = canvasWidth / (gridSize + 1)

            // Draw dots
            for (row in 0 until gridSize) {
                for (col in 0 until gridSize) {
                    val dotIndex = row * gridSize + col
                    val x = (col + 1) * spacing
                    val y = (row + 1) * spacing

                    drawCircle(
                        color = if (selectedDots.contains(dotIndex)) primaryColor else Color.Gray,
                        radius = dotRadius,
                        center = Offset(x, y)
                    )
                }
            }

            // Draw lines between selected dots
            if (selectedDots.size > 1) {
                val dotsList = selectedDots.toList()
                for (i in 0 until dotsList.size - 1) {
                    val start = getDotPosition(dotsList[i], spacing, gridSize)
                    val end = getDotPosition(dotsList[i + 1], spacing, gridSize)

                    drawLine(
                        color = primaryColor,
                        start = start,
                        end = end,
                        strokeWidth = 8f,
                        cap = StrokeCap.Round
                    )
                }

                // Draw line to current touch position
                if (isDragging && currentPosition != null) {
                    val lastDot = getDotPosition(dotsList.last(), spacing, gridSize)
                    drawLine(
                        color = primaryColor.copy(alpha = 0.5f),
                        start = lastDot,
                        end = currentPosition!!,
                        strokeWidth = 8f,
                        cap = StrokeCap.Round
                    )
                }
            }
        }
    }
}

/**
 * Get dot index at touch position
 */
private fun getDotAtPosition(position: Offset, canvasWidth: Float, gridSize: Int, dotRadius: Float): Int? {
    val spacing = canvasWidth / (gridSize + 1)

    for (row in 0 until gridSize) {
        for (col in 0 until gridSize) {
            val x = (col + 1) * spacing
            val y = (row + 1) * spacing
            val distance = sqrt((position.x - x) * (position.x - x) + (position.y - y) * (position.y - y))

            if (distance <= dotRadius * 2) {
                return row * gridSize + col
            }
        }
    }
    return null
}

/**
 * Get dot position by index
 */
private fun getDotPosition(dotIndex: Int, spacing: Float, gridSize: Int): Offset {
    val row = dotIndex / gridSize
    val col = dotIndex % gridSize
    return Offset((col + 1) * spacing, (row + 1) * spacing)
}