package io.github.katsukia.stackablecards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun cardList(
    cardSpacing: Dp = 8.dp,
    modifier: Modifier = Modifier,
    content: @Composable (index: Int) -> Unit,
) {
    val listState = rememberLazyListState()
    val firstVisibleItemIndex by remember { derivedStateOf { listState.firstVisibleItemIndex } }
    val firstVisibleItemScrollOffset by remember { derivedStateOf { listState.firstVisibleItemScrollOffset.toFloat() } }
    val cardSpacingPx = cardSpacing.toPx()
    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = PaddingValues(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(cardSpacing),
    ) {
        items(ITEM_COUNT) { index ->
            cardListItem(
                index = index,
                cardSpacingPx = cardSpacingPx,
                firstVisibleItemIndex = firstVisibleItemIndex,
                firstVisibleItemScrollOffsetPx = firstVisibleItemScrollOffset,
                content = content,
            )
        }
    }
}

@Composable
private fun Dp.toPx(): Float {
    return with(LocalDensity.current) { this@toPx.toPx() }
}

@Composable
private fun Float.toDp(): Dp {
    return with(LocalDensity.current) { this@toDp.toDp() }
}

@Composable
@Suppress("LongParameterList")
private fun cardListItem(
    index: Int,
    cardSpacingPx: Float,
    firstVisibleItemIndex: Int,
    firstVisibleItemScrollOffsetPx: Float,
    modifier: Modifier = Modifier,
    content: @Composable (index: Int) -> Unit,
) {
    var cardHeightPx by remember { mutableFloatStateOf(0f) }
    val cardGraphicsLayerState =
        rememberCardGraphicsLayerState(
            index = index,
            cardSpacingPx = cardSpacingPx,
            firstVisibleItemIndex = firstVisibleItemIndex,
            firstVisibleItemScrollOffsetPx = firstVisibleItemScrollOffsetPx,
            cardHeightPx = cardHeightPx,
        )
    Card(
        modifier =
            modifier.graphicsLayer {
                this.translationY = cardGraphicsLayerState.translationY
                this.scaleX = cardGraphicsLayerState.scale
                this.scaleY = cardGraphicsLayerState.scale
                this.alpha = cardGraphicsLayerState.alpha
            }.fillMaxWidth(),
    ) {
        Box(
            modifier =
                Modifier.onGloballyPositioned {
                    cardHeightPx = it.size.height.toFloat()
                },
        ) {
            content(index)
            Box(
                modifier =
                    Modifier
                        .height(cardHeightPx.toDp())
                        .background(Color.Black.copy(alpha = cardGraphicsLayerState.shadowAlpha))
                        .fillMaxWidth(),
            )
        }
    }
}

private class CardGraphicsLayerState(
    val translationY: Float,
    val scale: Float,
    val alpha: Float,
    val shadowAlpha: Float,
)

@Composable
private fun rememberCardGraphicsLayerState(
    index: Int,
    cardSpacingPx: Float,
    firstVisibleItemIndex: Int,
    firstVisibleItemScrollOffsetPx: Float,
    cardHeightPx: Float,
): CardGraphicsLayerState {
    val offset =
        if (firstVisibleItemIndex >= index && cardHeightPx > 0f) {
            val offsetRatio = firstVisibleItemScrollOffsetPx / (cardHeightPx + cardSpacingPx)
            firstVisibleItemIndex - index + offsetRatio
        } else {
            0f
        }
    return remember(offset) {
        val cardScale = 1f - offset * CARD_SCALE_FACTOR
        val cardTranslationY = offset * (cardHeightPx + cardSpacingPx) * CARD_TRANSLATION_FACTOR
        val cardAlpha = if (offset < 1f) 1f else (1f - (offset - 1f) * CARD_ALPHA_FACTOR).coerceIn(0f, 1f)
        val cardShadowAlpha = (offset * CARD_SHADOW_ALPHA_FACTOR).coerceIn(0f, 1f)
        CardGraphicsLayerState(
            translationY = cardTranslationY,
            scale = cardScale,
            alpha = cardAlpha,
            shadowAlpha = cardShadowAlpha,
        )
    }
}

private const val ITEM_COUNT = 50
private const val CARD_SCALE_FACTOR = 0.05f
private const val CARD_TRANSLATION_FACTOR = 0.95f
private const val CARD_ALPHA_FACTOR = 10f
private const val CARD_SHADOW_ALPHA_FACTOR = 0.2f
