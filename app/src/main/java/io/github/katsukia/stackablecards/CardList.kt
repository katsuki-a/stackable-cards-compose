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

/**
 * A scrollable list of cards that stack on top of each other as you scroll.
 *
 * @param count The number of cards to display.
 * @param modifier The modifier to apply to this composable.
 * @param cardSpacing The spacing between cards.
 * @param cardScaleFactor The factor by which to scale down cards as they scroll.
 * @param cardTranslationFactor The factor by which to translate cards as they scroll.
 * @param cardAlphaFactor The factor by which to decrease the alpha of cards as they scroll.
 * @param cardShadowAlphaFactor The factor by which to increase the shadow alpha of cards as they scroll.
 * @param content The content of each card.
 */
@Composable
fun CardList(
    count: Int,
    modifier: Modifier = Modifier,
    cardSpacing: Dp = 8.dp,
    cardScaleFactor: Float = CardDefaults.cardScaleFactor,
    cardTranslationFactor: Float = CardDefaults.cardTranslationFactor,
    cardAlphaFactor: Float = CardDefaults.cardAlphaFactor,
    cardShadowAlphaFactor: Float = CardDefaults.cardShadowAlphaFactor,
    content: @Composable (index: Int) -> Unit,
) {
    val listState = rememberLazyListState()
    val firstVisibleItemIndex by remember { derivedStateOf { listState.firstVisibleItemIndex } }
    val firstVisibleItemScrollOffset by remember { derivedStateOf { listState.firstVisibleItemScrollOffset.toFloat() } }
    val cardSpacingPx = with(LocalDensity.current) { cardSpacing.toPx() }

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
                cardScaleFactor = cardScaleFactor,
                cardTranslationFactor = cardTranslationFactor,
                cardAlphaFactor = cardAlphaFactor,
                cardShadowAlphaFactor = cardShadowAlphaFactor,
                content = content
            )
        }
    }
}

/**
 * Converts a float value to Dp.
 */
@Composable
private fun Float.toDp(): Dp {
    return with(LocalDensity.current) { this@toDp.toDp() }
}

/**
 * CardListの各アイテムを表示するコンポーザブル。
 * スクロール位置に基づいてカードのアニメーション効果（スケール、透明度、影など）を適用します。
 *
 * @param index カードのインデックス
 * @param cardSpacingPx カード間の間隔（ピクセル単位）
 * @param firstVisibleItemIndex 表示されている最初のアイテムのインデックス
 * @param firstVisibleItemScrollOffsetPx 表示されている最初のアイテムのスクロールオフセット（ピクセル単位）
 * @param modifier このコンポーザブルに適用する[Modifier]
 * @param content カードの内容を定義するコンポーザブル関数
 */
@Composable
private fun CardListItem(
    index: Int,
    cardSpacingPx: Float,
    firstVisibleItemIndex: Int,
    firstVisibleItemScrollOffsetPx: Float,
    modifier: Modifier = Modifier,
    cardScaleFactor: Float,
    cardTranslationFactor: Float,
    cardAlphaFactor: Float,
    cardShadowAlphaFactor: Float,
    content: @Composable (index: Int) -> Unit,
) {
    var cardHeightPx by remember { mutableFloatStateOf(0f) }
    val cardGraphicsLayerState = rememberCardGraphicsLayerState(
        index = index,
        cardSpacingPx = cardSpacingPx,
        firstVisibleItemIndex = firstVisibleItemIndex,
        firstVisibleItemScrollOffsetPx = firstVisibleItemScrollOffsetPx,
        cardHeightPx = cardHeightPx,
        cardScaleFactor = cardScaleFactor,
        cardTranslationFactor = cardTranslationFactor,
        cardAlphaFactor = cardAlphaFactor,
        cardShadowAlphaFactor = cardShadowAlphaFactor,
    )
    Card(
        modifier = modifier.graphicsLayer {
            this.translationY = cardGraphicsLayerState.translationY
            this.scaleX = cardGraphicsLayerState.scale
            this.scaleY = cardGraphicsLayerState.scale
            this.alpha = cardGraphicsLayerState.alpha
        }.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier.onGloballyPositioned {
                cardHeightPx = it.size.height.toFloat()
            }
        ) {
            content(index)
            // Overlay a shadow on top of the card
            Box(
                modifier = Modifier
                    .height(cardHeightPx.toDp())
                    .background(Color.Black.copy(alpha = cardGraphicsLayerState.shadowAlpha))
                    .fillMaxWidth()
            )
        }
    }
}

/**
 * Holds the state for the graphics layer of a card.
 */
private data class CardGraphicsLayerState(
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
    cardHeightPx: Float,
    cardScaleFactor: Float,
    cardTranslationFactor: Float,
    cardAlphaFactor: Float,
    cardShadowAlphaFactor: Float,
): CardGraphicsLayerState {
    val offset = if (firstVisibleItemIndex >= index && cardHeightPx > 0f) {
        val offsetRatio = firstVisibleItemScrollOffsetPx / (cardHeightPx + cardSpacingPx)
        firstVisibleItemIndex - index + offsetRatio
    } else {
        0f
    }
    return remember(offset, cardHeightPx) {
        val cardScale = 1f - offset * cardScaleFactor
        val cardTranslationY = offset * (cardHeightPx + cardSpacingPx) * cardTranslationFactor
        val cardAlpha = if (offset < 1f) 1f else (1f - (offset - 1f) * cardAlphaFactor).coerceIn(0f, 1f)
        val cardShadowAlpha = (offset * cardShadowAlphaFactor).coerceIn(0f, 1f)
        CardGraphicsLayerState(
            translationY = cardTranslationY,
            scale = cardScale,
            alpha = cardAlpha,
            shadowAlpha = cardShadowAlpha
        )
    }
}

object CardDefaults {
    /** The factor by which to scale down cards as they scroll. */
    const val cardScaleFactor = 0.05f
    /** The factor by which to translate cards as they scroll. */
    const val cardTranslationFactor = 0.95f
    /** The factor by which to decrease the alpha of cards as they scroll. */
    const val cardAlphaFactor = 10f
    /** The factor by which to increase the shadow alpha of cards as they scroll. */
    const val cardShadowAlphaFactor = 0.2f
}
