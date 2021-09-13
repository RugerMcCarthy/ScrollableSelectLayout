package edu.bupt.composestudy

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
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
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

private class SlideSelectBarColumnItem<T>(var item: T, selected: Boolean = false) {
    var selected by mutableStateOf(selected)
}

@Composable
private fun SlideSelectBarColumn(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
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
private fun <E> SlideSelectBarColumnContent(
    itemHeight: Dp,
    slideSelectBarColumnItem: SlideSelectBarColumnItem<E>,
    content: @Composable RowScope.(E, Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(itemHeight),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        content(slideSelectBarColumnItem.item, slideSelectBarColumnItem.selected)
    }
}

// 默认-1时初始状态为列表中间项
class SlideSelectBarState(currentSwipeItemIndex: Int = -1) {
    var currentSwipeItemIndex by mutableStateOf(currentSwipeItemIndex)

    companion object {
        val Saver = object: Saver<SlideSelectBarState, Int> {
            override fun restore(value: Int): SlideSelectBarState {
                return SlideSelectBarState(value)
            }

            override fun SaverScope.save(value: SlideSelectBarState): Int {
                return value.currentSwipeItemIndex
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun <E> SlideSelectBarLayout(
    items: List<E>,
    slideSelectBarState: SlideSelectBarState,
    itemHeight: Dp,
    modifier: Modifier = Modifier,
    visibleCount: Int = 3,
    content: @Composable RowScope.(E, Boolean) -> Unit
) {
    val slideSelectBarColumnItems = remember(items) {
        items.map {
            SlideSelectBarColumnItem(it)
        }
    }
    var midItemIndexStart = remember(slideSelectBarColumnItems) {
        val midItemIndexStart = if (slideSelectBarState.currentSwipeItemIndex != -1) {
            slideSelectBarState.currentSwipeItemIndex - 1
        } else (((slideSelectBarColumnItems.size - 1) / 2) - 1).coerceAtLeast(0).coerceAtMost(slideSelectBarColumnItems.size - 2)
        slideSelectBarColumnItems[midItemIndexStart + 1].selected = true
        slideSelectBarState.currentSwipeItemIndex = midItemIndexStart + 1
        midItemIndexStart
    }
    val anthors = remember(slideSelectBarColumnItems) {
        val anthors = mutableMapOf<Float, Int>()
        for (index in slideSelectBarColumnItems.indices) {
            anthors[-index * itemHeight.toPx()] = index - 1
        }
        anthors
    }
    val swipeableState = rememberSwipeableState(initialValue = midItemIndexStart) {
        slideSelectBarColumnItems[midItemIndexStart + 1].selected = false
        slideSelectBarColumnItems[it + 1].selected = true
        midItemIndexStart = it
        slideSelectBarState.currentSwipeItemIndex = midItemIndexStart + 1
        true
    }
    var selectBoxOffset: Float = if (visibleCount.mod( 2) == 0) itemHeight.toPx() / 2f else 0f
    Box(
        modifier = Modifier
            .then(modifier)
            .fillMaxWidth()
            .height(itemHeight * visibleCount)
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
                drawContent()
                drawLine(
                    color = Color(0xff83cde6),
                    start = Offset(width * (1 / 6f), itemHeight.toPx() * ((visibleCount - 1) / 2)),
                    end = Offset(width * (5 / 6f), itemHeight.toPx() * (((visibleCount - 1) / 2))),
                    strokeWidth = 3f
                )
                drawLine(
                    color = Color(0xff83cde6),
                    start = Offset(width * (1 / 6f), itemHeight.toPx() * ((visibleCount - 1) / 2 + 1)),
                    end = Offset(width * (5 / 6f), itemHeight.toPx() * ((visibleCount - 1) / 2 + 1)),
                    strokeWidth = 3f
                )
            }
            .graphicsLayer { clip = true }
    ) {
        SlideSelectBarColumn(
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
                        placeable.placeRelative(0, currentY  - selectBoxOffset.toInt())
                    }
                }
                .offset { IntOffset(0, swipeableState.offset.value.toInt()) }
        ){
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(itemHeight)
            )
            for (slideSelectBarColumnItem in slideSelectBarColumnItems) {
                SlideSelectBarColumnContent(
                    itemHeight = itemHeight,
                    slideSelectBarColumnItem = slideSelectBarColumnItem,
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
@ExperimentalMaterialApi
@Preview
@Composable
fun ScrollSelectColumnPreview() {
    initDensity()
    var items = remember {
        mutableListOf (
            "Tom",
            "Lily",
            "Jack",
            "Bob",
            "Alice",
            "Jessy",
            "Nancy"
        )
    }
    Box(Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        var slideSelectBarState = remember {
            SlideSelectBarState()
        }
        SlideSelectBarLayout(
            items = items,
            slideSelectBarState = slideSelectBarState,
            itemHeight = 50.dp
        ) { item, selected ->
            Icon(painter = painterResource(id = R.drawable.ic_launcher_foreground)
                , contentDescription = "test",
                Modifier
                    .width(20.dp)
                    .height(20.dp)
            )
            Text(
                text = item,
                color = if (selected) Color(0xff0288ce) else Color(0xffbbbbbb),
                fontWeight = FontWeight.W500,
                style = MaterialTheme.typography.body1
            )
        }
    }
}

@Composable
fun rememberSlideBarState(currentSwipeItemIndex: Int = -1) = rememberSaveable(saver =  SlideSelectBarState.Saver) {
    SlideSelectBarState(currentSwipeItemIndex)
}