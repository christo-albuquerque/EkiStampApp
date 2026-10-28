package com.ekistamp.app.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp

/**
 * App-wide backdrop: a tinted base with soft accent "light blobs". Glass surfaces sit on top of it,
 * so the translucent panels pick up the colour behind them.
 */
@Composable
fun GlassBackground(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val dark = LocalDarkTheme.current
    val accent = MaterialTheme.colorScheme.primary
    val base = MaterialTheme.colorScheme.background
    val second = lerp(accent, Color(0xFF5E5CE6), 0.5f)
    val strength = if (dark) 0.42f else 0.34f

    Box(
        modifier
            .fillMaxSize()
            .background(base)
            .drawBehind {
                drawRect(
                    Brush.radialGradient(
                        listOf(accent.copy(alpha = strength), Color.Transparent),
                        center = Offset(size.width * 0.05f, size.height * 0.05f),
                        radius = size.width * 0.95f
                    )
                )
                drawRect(
                    Brush.radialGradient(
                        listOf(second.copy(alpha = strength * 0.8f), Color.Transparent),
                        center = Offset(size.width * 0.98f, size.height * 0.5f),
                        radius = size.width * 1.0f
                    )
                )
                drawRect(
                    Brush.radialGradient(
                        listOf(accent.copy(alpha = strength * 0.7f), Color.Transparent),
                        center = Offset(size.width * 0.1f, size.height * 1.0f),
                        radius = size.width * 0.9f
                    )
                )
            }
    ) { content() }
}

val GlassShape = RoundedCornerShape(22.dp)

@Composable
fun glassFill(): Brush {
    val dark = LocalDarkTheme.current
    return Brush.verticalGradient(
        if (dark) listOf(Color.White.copy(alpha = 0.14f), Color.White.copy(alpha = 0.06f))
        else listOf(Color.White.copy(alpha = 0.72f), Color.White.copy(alpha = 0.42f))
    )
}

@Composable
fun glassBorder(): Brush {
    val dark = LocalDarkTheme.current
    return Brush.linearGradient(
        if (dark) listOf(Color.White.copy(alpha = 0.38f), Color.White.copy(alpha = 0.04f))
        else listOf(Color.White, Color.White.copy(alpha = 0.3f))
    )
}

/** Frosted-glass panel: translucent gradient fill with a bright specular rim. */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = GlassShape,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier
            .clip(shape)
            .background(glassFill())
            .border(1.dp, glassBorder(), shape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        content = content
    )
}

@Composable
fun glassFieldColors(): TextFieldColors {
    val dark = LocalDarkTheme.current
    val fill = if (dark) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.55f)
    val rim = if (dark) Color.White.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.9f)
    return OutlinedTextFieldDefaults.colors(
        focusedContainerColor = fill,
        unfocusedContainerColor = fill,
        unfocusedBorderColor = rim,
        focusedBorderColor = MaterialTheme.colorScheme.primary
    )
}
