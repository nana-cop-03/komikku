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

/**
 * Dialog for autoscroll speed settings.
 * Positioned above bottom bar like Kotatsu for easier thumb reach.
 * Includes speed slider and FAB visibility toggle.
 */
@Composable
fun AutoscrollSettingsDialog(
    currentSpeed: Float,
    onSpeedChange: (Float) -> Unit,
    onDismissRequest: () -> Unit,
    showFabButton: Boolean = false,
    onShowFabChange: (Boolean) -> Unit = {},
) {
    // Speed value (0.000001 to 0.97) - continuous range like Kotatsu
    var sliderValue by remember { mutableStateOf(currentSpeed) }
    var showFab by remember { mutableStateOf(showFabButton) }

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
                        style = MaterialTheme.typography.titleMedium,
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

                // Speed label
                Text(
                    text = stringResource(MR.strings.speed),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp),
                )

                // Smooth slider for speed (0.000001 to 0.97, like Kotatsu)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                ) {
                    Slider(
                        value = sliderValue,
                        onValueChange = { newValue ->
                            sliderValue = newValue
                            // Only update if change is significant
                            if (abs(newValue - currentSpeed) > 0.0001) {
                                onSpeedChange(newValue)
                            }
                        },
                        valueRange = 0.000001f..0.97f,
                        steps = 96, // 97 possible values for smooth feel
                        modifier = Modifier.fillMaxWidth(),
                    )

                    // Speed display (0.1x to 10.0x)
                    val displaySpeed = 0.1f + (sliderValue * 10f)
                    Text(
                        text = String.format("%.1fx", displaySpeed),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 2.dp),
                    )
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
                    Checkbox(
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
