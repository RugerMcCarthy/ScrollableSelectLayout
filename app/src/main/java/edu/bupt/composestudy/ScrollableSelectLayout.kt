package edu.bupt.composestudy

import androidx.annotation.FloatRange
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset

open class SelectionLineStyle(
    val color: Color,

    @FloatRange(from = 0.0, to = 1.0)
    val lengthFraction: Float,

    val strokeWidth: Float
) {
    object Default: SelectionLineStyle(
        color = Color(0xff83cde6),
        lengthFraction = 1f,
        strokeWidth = 3f
    )
}

private class ScrollableSelectColumnItem<T>(var item: T, selected: Boolean = false) {
    var selected by mutableStateOf(selected)
}

@Composable
private fun ScrollableSelectColumn(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Layout(
        content = content,
        modifier = modifier,
        measurePolicy = object: MeasurePolicy {
            override fun MeasureScope.measure(
                measurables: List<Measurable>,
                constraints: Constraints
            ): MeasureResult {
                var placeables = measurables.map {
                    it.measure(constraints)
                }
                var needHeight = 0
                placeables.forEach { placeable ->
                    needHeight += placeable.height
                }
                var currentY = 0
                return layout(constraints.maxWidth, needHeight) {
                    placeables.forEach {
                            placeable ->
                        placeable.placeRelative(x = 0, y = currentY)
                        currentY += placeable.height
                    }
                }
            }
        }
    )
}

@Composable
private fun <E> ScrollableSelectColumnItemLayout(
    itemHeight: Dp,
    scrollableSelectItem: ScrollableSelectColumnItem<E>,
    content: @Composable RowScope.(E, Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(itemHeight),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        content(scrollableSelectItem.item, scrollableSelectItem.selected)
    }
}

// 默认-1时初始状态为列表中间项
class ScrollableSelectState(currentSwipeItemIndex: Int) {
    var currentSwipeItemIndex by mutableStateOf(currentSwipeItemIndex)

    companion object {
        val Saver = object: Saver<ScrollableSelectState, Int> {
            override fun restore(value: Int): ScrollableSelectState {
                return ScrollableSelectState(value)
            }

            override fun SaverScope.save(value: ScrollableSelectState): Int {
                return value.currentSwipeItemIndex
            }
        }
    }
}

/**
 * A scrollable selector，can be applied to some scenes that require scrolling selection
 *
 * @param items A list contains sub-elements' info
 * @param scrollableSelectState Through the state can get the info of selected item
 * @param itemHeight Manually input the height of the sub-elements
 * @param modifier Used to decorate this component
 * @param visibleAmount Specify the number of sub-elements that can be displayed
 * @param selectionLineStyle Specify the style of the selection line
 * @param content Specify the layout style of sub-elements, You can make the selected item display different styles according to the state.
 */
@ExperimentalMaterialApi
@Composable
fun <E> ScrollableSelectLayout(
    items: List<E>,
    scrollableSelectState: ScrollableSelectState = rememberScrollableSelectState(),
    itemHeight: Dp,
    modifier: Modifier = Modifier,
    visibleAmount: Int = 3,
    selectionLineStyle: SelectionLineStyle = SelectionLineStyle.Default,
    content: @Composable RowScope.(item: E, Boolean) -> Unit
) {
    val scrollableSelectColumnItems = remember(items) {
        items.map {
            ScrollableSelectColumnItem(it)
        }
    }
    var midItemIndexStart = remember(scrollableSelectColumnItems) {
        val midItemIndexStart = if (scrollableSelectState.currentSwipeItemIndex != -1) {
            scrollableSelectState.currentSwipeItemIndex - 1
        } else (((scrollableSelectColumnItems.size - 1) / 2) - 1).coerceAtLeast(0).coerceAtMost(scrollableSelectColumnItems.size - 2)
        scrollableSelectColumnItems[midItemIndexStart + 1].selected = true
        scrollableSelectState.currentSwipeItemIndex = midItemIndexStart + 1
        midItemIndexStart
    }
    val anthors = remember(scrollableSelectColumnItems) {
        val anthors = mutableMapOf<Float, Int>()
        for (index in scrollableSelectColumnItems.indices) {
            anthors[-index * itemHeight.toPx()] = index - 1
        }
        anthors
    }
    val swipeableState = rememberSwipeableState(initialValue = midItemIndexStart) {
        scrollableSelectColumnItems[midItemIndexStart + 1].selected = false
        scrollableSelectColumnItems[it + 1].selected = true
        midItemIndexStart = it
        scrollableSelectState.currentSwipeItemIndex = midItemIndexStart + 1
        true
    }
    var selectBoxOffset: Float = if (visibleAmount.mod( 2) == 0) itemHeight.toPx() / 2f else 0f
    Box(
        modifier = Modifier
            .then(modifier)
            .fillMaxWidth()
            .height(itemHeight * visibleAmount)
            .swipeable(
                state = swipeableState,
                anchors = anthors,
                orientation = Orientation.Vertical,
                thresholds = { _, _ ->
                    FractionalThreshold(0.5f)
                }
            )
            .drawWithContent {
                var width = drawContext.size.width
                var startFraction = (1 - selectionLineStyle.lengthFraction) / 2f
                var endFraction = startFraction + selectionLineStyle.lengthFraction
                drawContent()
                drawLine(
                    color = selectionLineStyle.color,
                    start = Offset(width * startFraction, itemHeight.toPx() * ((visibleAmount - 1) / 2)),
                    end = Offset(width * endFraction, itemHeight.toPx() * (((visibleAmount - 1) / 2))),
                    strokeWidth = 3f
                )
                drawLine(
                    color = selectionLineStyle.color,
                    start = Offset(
                        width * startFraction,
                        itemHeight.toPx() * ((visibleAmount - 1) / 2 + 1)
                    ),
                    end = Offset(
                        width * endFraction,
                        itemHeight.toPx() * ((visibleAmount - 1) / 2 + 1)
                    ),
                    strokeWidth = selectionLineStyle.strokeWidth
                )
            }
            .graphicsLayer { clip = true }
    ) {
        ScrollableSelectColumn(
            modifier = Modifier
                .fillMaxWidth()
                .layout { measurable, constraints ->
                    val nonConstraints = Constraints(
                        minWidth = constraints.minWidth,
                        maxWidth = constraints.maxWidth
                    )
                    val placeable = measurable.measure(nonConstraints)
                    val currentY = placeable.height / 2 - (itemHeight.toPx() * 1.5).toInt()
                    layout(placeable.width, placeable.height) {
                        placeable.placeRelative(0, currentY - selectBoxOffset.toInt())
                    }
                }
                .offset { IntOffset(0, swipeableState.offset.value.toInt()) }
        ){
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(itemHeight)
            )
            for (scrollableSelectItem in scrollableSelectColumnItems) {
                ScrollableSelectColumnItemLayout(
                    itemHeight = itemHeight,
                    scrollableSelectItem = scrollableSelectItem,
                    content = content
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(itemHeight),
            )
        }
    }
}

@Composable
fun rememberScrollableSelectState(initialItemIndex: Int = -1) = rememberSaveable(saver =  ScrollableSelectState.Saver) {
    ScrollableSelectState(initialItemIndex)
}