package io.github.katsukia.stackablecards

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * A bottom sheet content composable for adjusting animation parameters.
 *
 * @param animationFactors The current animation factors.
 * @param onAnimationFactorsChange A callback to be invoked when the animation factors change.
 * @param onResetClick A callback to be invoked when the reset button is clicked.
 * @param modifier The modifier to apply to this composable.
 */
@Composable
fun AnimationSettingsSheet(
    animationFactors: CardAnimationFactors,
    onAnimationFactorsChange: (CardAnimationFactors) -> Unit,
    onResetClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = stringResource(id = R.string.settings_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))

        SettingSlider(
            label = stringResource(R.string.scale_factor_label),
            description = stringResource(id = R.string.scale_factor_desc),
            value = animationFactors.scaleFactor,
            onValueChange = { onAnimationFactorsChange(animationFactors.copy(scaleFactor = it)) },
            valueRange = 0f..1f
        )

        SettingSlider(
            label = stringResource(R.string.translation_factor_label),
            description = stringResource(id = R.string.translation_factor_desc),
            value = animationFactors.translationFactor,
            onValueChange = { onAnimationFactorsChange(animationFactors.copy(translationFactor = it)) },
            valueRange = 0f..1f
        )

        SettingSlider(
            label = stringResource(R.string.alpha_factor_label),
            description = stringResource(id = R.string.alpha_factor_desc),
            value = animationFactors.alphaFactor,
            onValueChange = { onAnimationFactorsChange(animationFactors.copy(alphaFactor = it)) },
            valueRange = 0f..20f
        )

        SettingSlider(
            label = stringResource(R.string.shadow_alpha_factor_label),
            description = stringResource(id = R.string.shadow_alpha_factor_desc),
            value = animationFactors.shadowAlphaFactor,
            onValueChange = { onAnimationFactorsChange(animationFactors.copy(shadowAlphaFactor = it)) },
            valueRange = 0f..1f
        )

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onResetClick, modifier = Modifier.fillMaxWidth()) {
            Text(text = stringResource(id = R.string.reset_button_label))
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SettingSlider(
    label: String,
    description: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
) {
    Column {
        Text(text = "$label: %.2f".format(value), style = MaterialTheme.typography.bodyLarge)
        Text(text = description, style = MaterialTheme.typography.bodySmall)
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}
