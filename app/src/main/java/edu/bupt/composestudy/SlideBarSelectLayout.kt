package edu.bupt.composestudy

import android.util.Log
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

open class SlideSelectBarColumnItem(selected: Boolean = false) {
    var selected by mutableStateOf(selected)
}
class DefaultSlideSelectBarColumnItem(text: String, selected: Boolean = false):  SlideSelectBarColumnItem(selected) {
    var text by mutableStateOf(text)
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
                var newConstraints = Constraints()
                var placeables = measurables.map {
                    it.measure(newConstraints)
                }
                var needHeight = 0
                placeables.forEach { placeable ->
                    needHeight += placeable.height
                }
                var currentY = 0
                return layout(constraints.maxWidth, needHeight) {
                    placeables.forEach {
                            placeable ->
                        var currentX = (constraints.maxWidth - placeable.width) / 2
                        placeable.placeRelative(x = currentX, y = currentY)
                        Log.d("gzz","placeable: ${currentX} and ${currentY}")
                        currentY += placeable.height
                    }
                }
            }
        }
    )
}


@ExperimentalMaterialApi
@Composable
fun <E: SlideSelectBarColumnItem> SlideSelectBarLayout(items: List<E>, content: @Composable RowScope.(E) -> Unit) {
    var itemWidth = 200.dp
    var itemWidthPx = with(LocalDensity.current) {
        itemWidth.toPx()
    }
    var itemHeight = 50.dp
    var itemHeightPx = with(LocalDensity.current) {
        itemHeight.toPx()
    }
    var anthors = mutableMapOf<Float, Int>()

    for (index in -1 .. items.size - 2) {
        anthors[-(index + 1) * itemHeightPx] = index
    }
    var midItemIndexStart = (((items.size - 1) / 2) - 1).coerceAtLeast(0).coerceAtMost(items.size - 2)
    items[midItemIndexStart + 1].selected = true
    var swipeableState = rememberSwipeableState(initialValue = midItemIndexStart) {
        items[midItemIndexStart + 1].selected = false
        items[it + 1].selected = true
        midItemIndexStart = it
        true
    }
    Surface(
        elevation = 5.dp,
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier
            .width(200.dp)
            .height(itemHeight * 3)
            .swipeable(
                state = swipeableState,
                anchors = anthors,
                orientation = Orientation.Vertical,
                thresholds = { _, _ ->
                    FractionalThreshold(0.5f)
                }
            )
            .drawWithContent {
                drawContent()
                drawLine(
                    color = Color(0xff83cde6),
                    start = Offset(itemWidthPx * (1 / 6f), itemHeightPx),
                    end = Offset(itemWidthPx * (5 / 6f), itemHeightPx)
                )
                drawLine(
                    color = Color(0xff83cde6),
                    start = Offset(itemWidthPx * (1 / 6f), itemHeightPx * 2),
                    end = Offset(itemWidthPx * (5 / 6f), itemHeightPx * 2)
                )
            }
    ) {
        SlideSelectBarColumn(
            Modifier
                .fillMaxSize()
                .layout { measurable, constraints ->
                    var newConstraints = Constraints(minWidth = constraints.minWidth, maxWidth = constraints.maxWidth)
                    var placeable = measurable.measure(newConstraints)
                    var currentY = placeable.height / 2 - (itemHeightPx * 1.5).toInt()
                    layout(placeable.width, placeable.height) {
                        placeable.placeRelative(0, currentY)
                    }
                }
                .offset { IntOffset(0, swipeableState.offset.value.toInt()) }
        ){
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(itemHeight)
            )
            for (item in items) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    content(item)
                }
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
    var items = remember {
        mutableListOf (
            DefaultSlideSelectBarColumnItem("Hello"),
            DefaultSlideSelectBarColumnItem("Guan"),
            DefaultSlideSelectBarColumnItem("Ruger"),
            DefaultSlideSelectBarColumnItem("Compose"),
            DefaultSlideSelectBarColumnItem("Scroller"),
            DefaultSlideSelectBarColumnItem("World"),
            DefaultSlideSelectBarColumnItem("Tom")
        )
    }
    Box(Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        SlideSelectBarLayout(items) {
            Text(
                text = it.text,
                color = if (it.selected) Color(0xff0288ce) else Color(0xffbbbbbb),
                fontWeight = FontWeight.W500,
                style = MaterialTheme.typography.body1
            )
        }
    }
}
