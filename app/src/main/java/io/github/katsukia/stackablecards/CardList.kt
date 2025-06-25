package io.github.katsukia.stackablecards

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A scrollable list of cards that stack on top of each other as you scroll.
 *
 * @param count The number of cards to display.
 * @param modifier The modifier to apply to this composable.
 * @param cardSpacing The spacing between cards.
 * @param animationFactors Factors to control card animations (scale, translation, alpha) during scroll.
 * @param content The content of each card.
 */
@Composable
fun CardList(
    count: Int,
    modifier: Modifier = Modifier,
    orientation: Orientation = Orientation.Vertical,
    cardSpacing: Dp = 8.dp,
    animationFactors: CardAnimationFactors = CardAnimationFactors(),
    content: @Composable (index: Int) -> Unit,
) {
    val listState = rememberLazyListState()
    val firstVisibleItemIndex by remember { derivedStateOf { listState.firstVisibleItemIndex } }
    val firstVisibleItemScrollOffset by remember { derivedStateOf { listState.firstVisibleItemScrollOffset.toFloat() } }
    val cardSpacingPx = with(LocalDensity.current) { cardSpacing.toPx() }

    when (orientation) {
        Orientation.Vertical -> {
            LazyColumn(
                modifier = modifier,
                state = listState,
                contentPadding = PaddingValues(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(cardSpacing),
            ) {
                items(count = count) { index ->
                    CardListItem(
                        index = index,
                        cardSpacingPx = cardSpacingPx,
                        firstVisibleItemIndex = firstVisibleItemIndex,
                        firstVisibleItemScrollOffsetPx = firstVisibleItemScrollOffset,
                        animationFactors = animationFactors,
                        orientation = orientation,
                        content = content
                    )
                }
            }
        }
        Orientation.Horizontal -> {
            LazyRow(
                modifier = modifier,
                state = listState,
                contentPadding = PaddingValues(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(cardSpacing),
            ) {
                items(count = count) { index ->
                    CardListItem(
                        index = index,
                        cardSpacingPx = cardSpacingPx,
                        firstVisibleItemIndex = firstVisibleItemIndex,
                        firstVisibleItemScrollOffsetPx = firstVisibleItemScrollOffset,
                        animationFactors = animationFactors,
                        orientation = orientation,
                        content = content
                    )
                }
            }
        }
    }
}

/**
 * A composable that displays an individual card item in the list.
 * It applies animation effects (scaling, alpha, translation) based on the scroll position.
 *
 * @param index The index of the card.
 * @param cardSpacingPx The spacing between cards in pixels.
 * @param firstVisibleItemIndex The index of the first visible item in the list.
 * @param firstVisibleItemScrollOffsetPx The scroll offset of the first visible item in pixels.
 * @param modifier The modifier to be applied to this composable.
 * @param animationFactors Factors to control card animations.
 * @param orientation The orientation of the list.
 * @param content The composable function that defines the content of the card.
 */
@Composable
private fun CardListItem(
    index: Int,
    cardSpacingPx: Float,
    firstVisibleItemIndex: Int,
    firstVisibleItemScrollOffsetPx: Float,
    modifier: Modifier = Modifier,
    animationFactors: CardAnimationFactors,
    orientation: Orientation,
    content: @Composable (index: Int) -> Unit,
) {
    var cardMainAxisSizePx by remember { mutableFloatStateOf(0f) }
    val cardGraphicsLayerState = rememberCardGraphicsLayerState(
        index = index,
        cardSpacingPx = cardSpacingPx,
        firstVisibleItemIndex = firstVisibleItemIndex,
        firstVisibleItemScrollOffsetPx = firstVisibleItemScrollOffsetPx,
        cardMainAxisSizePx = cardMainAxisSizePx,
        animationFactors = animationFactors,
        orientation = orientation,
    )
    val cardShape = CardDefaults.shape

    val sizeModifier = if (orientation == Orientation.Vertical) {
        Modifier.fillMaxWidth()
    } else {
        Modifier.fillMaxHeight()
    }

    Card(
        modifier = modifier
            .then(sizeModifier)
            .stackableCard(
                cardGraphicsLayerState = cardGraphicsLayerState,
                cardShape = cardShape,
                orientation = orientation,
                onSizeMeasured = { cardMainAxisSizePx = it }
            )
    ) {
        content(index)
    }
}

@Composable
private fun Modifier.stackableCard(
    cardGraphicsLayerState: CardGraphicsLayerState,
    cardShape: Shape,
    orientation: Orientation,
    onSizeMeasured: (Float) -> Unit
): Modifier = this.then(
    remember(cardGraphicsLayerState, cardShape, orientation) {
        Modifier
            .graphicsLayer {
                this.translationX = cardGraphicsLayerState.translationX
                this.translationY = cardGraphicsLayerState.translationY
                this.scaleX = cardGraphicsLayerState.scale
                this.scaleY = cardGraphicsLayerState.scale
                this.alpha = cardGraphicsLayerState.alpha
            }
            .onGloballyPositioned {
                onSizeMeasured(
                    if (orientation == Orientation.Vertical) {
                        it.size.height.toFloat()
                    } else {
                        it.size.width.toFloat()
                    }
                )
            }
            .drawWithContent {
                drawContent()
                val outline = cardShape.createOutline(size, layoutDirection, this)
                when (outline) {
                    is Outline.Rectangle -> {
                        drawRect(
                            color = Color.Black,
                            alpha = cardGraphicsLayerState.shadowAlpha,
                        )
                    }

                    is Outline.Rounded -> {
                        drawRoundRect(
                            color = Color.Black,
                            alpha = cardGraphicsLayerState.shadowAlpha,
                            cornerRadius = outline.roundRect.bottomLeftCornerRadius,
                        )
                    }

                    is Outline.Generic -> {
                        drawPath(
                            path = outline.path,
                            color = Color.Black,
                            alpha = cardGraphicsLayerState.shadowAlpha,
                        )
                    }
                }
            }
    }
)

/**
 * Holds the state for the graphics layer of a card.
 */
private data class CardGraphicsLayerState(
    val translationX: Float,
    val translationY: Float,
    val scale: Float,
    val alpha: Float,
    val shadowAlpha: Float
)

/**
 * Calculates and remembers the graphics layer state for a card based on the scroll position.
 */
@Composable
private fun rememberCardGraphicsLayerState(
    index: Int,
    cardSpacingPx: Float,
    firstVisibleItemIndex: Int,
    firstVisibleItemScrollOffsetPx: Float,
    cardMainAxisSizePx: Float,
    animationFactors: CardAnimationFactors,
    orientation: Orientation,
): CardGraphicsLayerState {
    val offset = if (firstVisibleItemIndex >= index && cardMainAxisSizePx > 0f) {
        val offsetRatio = firstVisibleItemScrollOffsetPx / (cardMainAxisSizePx + cardSpacingPx)
        firstVisibleItemIndex - index + offsetRatio
    } else {
        0f
    }
    return remember(offset, cardMainAxisSizePx, animationFactors, orientation) {
        val cardScale = 1f - offset * animationFactors.scaleFactor
        val cardTranslation = offset * (cardMainAxisSizePx + cardSpacingPx) * animationFactors.translationFactor
        val cardAlpha = if (offset < 1f) 1f else (1f - (offset - 1f) * animationFactors.alphaFactor).coerceIn(0f, 1f)
        val cardShadowAlpha = (offset * animationFactors.shadowAlphaFactor).coerceIn(0f, 1f)
        CardGraphicsLayerState(
            translationX = if (orientation == Orientation.Horizontal) cardTranslation else 0f,
            translationY = if (orientation == Orientation.Vertical) cardTranslation else 0f,
            scale = cardScale,
            alpha = cardAlpha,
            shadowAlpha = cardShadowAlpha
        )
    }
}


