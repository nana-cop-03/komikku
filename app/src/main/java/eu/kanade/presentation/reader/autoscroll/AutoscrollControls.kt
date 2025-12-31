package eu.kanade.presentation.reader.autoscroll

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AssistChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tachiyomi.i18n.MR
import tachiyomi.i18n.sy.SYMR
import tachiyomi.presentation.core.i18n.stringResource

/**
 * FAB button for autoscroll control
 */
@Composable
fun AutoscrollFab(
    isActive: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    FloatingActionButton(
        onClick = { onToggle(!isActive) },
        containerColor = if (isActive) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        contentColor = if (isActive) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        modifier = modifier,
    ) {
        Icon(
            imageVector = if (isActive) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = if (isActive) {
                stringResource(MR.strings.action_pause)
            } else {
                stringResource(MR.strings.action_resume)
            },
        )
    }
}

/**
 * Speed control panel for autoscroll
 */
@Composable
fun AutoscrollSpeedControl(
    currentSpeed: Float,
    onSpeedChange: (Float) -> Unit,
    visible: Boolean = true,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = visible,
        enter = expandVertically(expandFrom = Alignment.Bottom),
        exit = shrinkVertically(shrinkTowards = Alignment.Bottom),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Current speed display
            val displaySpeed = (currentSpeed * 100f).toInt()
            Text(
                text = stringResource(SYMR.strings.eh_autoscroll) + ": ${displaySpeed}%",
                style = MaterialTheme.typography.labelMedium,
            )

            // Speed slider
            Slider(
                value = currentSpeed,
                onValueChange = onSpeedChange,
                valueRange = 0f..1f,
                steps = 99,
                modifier = Modifier.fillMaxWidth(),
            )

            // Preset buttons
            Text(
                text = "Presets:",
                style = MaterialTheme.typography.labelSmall,
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                listOf(
                    1 to "01%",
                    2 to "02%",
                    3 to "03%",
                    10 to "10%",
                    25 to "25%",
                    50 to "50%",
                    75 to "75%",
                    100 to "100%",
                ).forEach { (percent, label) ->
                    val scaleValue = percent / 100f
                    AssistChip(
                        onClick = { onSpeedChange(scaleValue) },
                        label = { Text(label) },
                        modifier = Modifier.padding(4.dp),
                    )
                }
            }

            // Info text
            Text(
                text = "Percentage of screen height scrolled per second",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
