package eu.kanade.presentation.reader.autoscroll

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import tachiyomi.i18n.MR
import tachiyomi.i18n.sy.SYMR
import tachiyomi.presentation.core.i18n.stringResource
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Dialog for autoscroll speed settings.
 * Positioned above bottom bar like Kotatsu for easier thumb reach.
 * Includes on/off toggle, speed slider and FAB visibility toggle.
 */
@Composable
fun AutoscrollSettingsDialog(
    isEnabled: Boolean = false,
    onToggleEnabled: (Boolean) -> Unit = {},
    currentMainSpeed: Int,
    currentMultiplier: Float,
    onMainChange: (Int) -> Unit,
    onMultiplierChange: (Float) -> Unit,
    onDismissRequest: () -> Unit,
    showFabButton: Boolean = false,
    onShowFabChange: (Boolean) -> Unit = {},
) {
    var mainSpeed by remember { mutableStateOf(currentMainSpeed.coerceIn(1, 100)) }
    var multiplier by remember { mutableStateOf(currentMultiplier) }
    var showFab by remember { mutableStateOf(showFabButton) }
    var isAutoscrollEnabled by remember { mutableStateOf(isEnabled) }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false,
        ),
    ) {
        // Position dialog above bottom bar, filling width at bottom of screen
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface),
            shape = MaterialTheme.shapes.large.copy(
                bottomEnd = MaterialTheme.shapes.large.bottomEnd,
                bottomStart = MaterialTheme.shapes.large.bottomStart,
            ),
            color = MaterialTheme.colorScheme.surface,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Header with title and close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(SYMR.strings.eh_autoscroll),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                    FilledTonalIconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.padding(end = 8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(MR.strings.action_close),
                        )
                    }
                }

                // Enable/Disable toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(SYMR.strings.eh_autoscroll),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f),
                    )
                    androidx.compose.material3.Switch(
                        checked = isAutoscrollEnabled,
                        onCheckedChange = { isChecked ->
                            isAutoscrollEnabled = isChecked
                            onToggleEnabled(isChecked)
                        },
                    )
                }

                // Main speed label
                Text(
                    text = stringResource(MR.strings.autoscroll),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp),
                )

                // Main slider (1..100)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                ) {
                    Slider(
                        value = mainSpeed.toFloat(),
                        onValueChange = { newValue ->
                            mainSpeed = newValue.roundToInt().coerceIn(1, 100)
                            onMainChange(mainSpeed)
                        },
                        valueRange = 1f..100f,
                        steps = 98,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Text(
                        text = "$mainSpeed",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 2.dp),
                    )
                }

                // Multiplier label
                Text(
                    text = stringResource(MR.strings.autoscroll_multiplier),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp),
                )

                // Multiplier options
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    val options = listOf(0.25f, 0.5f, 0.75f, 1f) + (2..10).map { it.toFloat() }
                    options.forEach { opt ->
                        val selected = opt == multiplier
                        FilledTonalIconButton(
                            onClick = {
                                multiplier = opt
                                onMultiplierChange(opt)
                            },
                        ) {
                            Text(text = if (opt >= 1f) "${opt.toInt()}x" else "${opt}" )
                        }
                    }
                }

                // FAB visibility toggle (like Kotatsu)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(MR.strings.show_floating_control_button),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f),
                    )
                    androidx.compose.material3.Switch(
                        checked = showFab,
                        onCheckedChange = { isChecked ->
                            showFab = isChecked
                            onShowFabChange(isChecked)
                        },
                    )
                }
            }
        }
    }
}
