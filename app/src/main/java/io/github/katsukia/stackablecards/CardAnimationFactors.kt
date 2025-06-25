package io.github.katsukia.stackablecards

import androidx.compose.runtime.Immutable

/**
 * A data class that holds the animation factors for the cards.
 *
 * @property scaleFactor The factor by which to scale down cards as they scroll.
 * @property translationFactor The factor by which to translate cards as they scroll.
 * @property alphaFactor The factor by which to decrease the alpha of cards as they scroll.
 * @property shadowAlphaFactor The factor by which to increase the shadow alpha of cards as they scroll.
 */
@Immutable
data class CardAnimationFactors(
    val scaleFactor: Float = 0.05f,
    val translationFactor: Float = 0.95f,
    val alphaFactor: Float = 10f,
    val shadowAlphaFactor: Float = 0.2f
)
