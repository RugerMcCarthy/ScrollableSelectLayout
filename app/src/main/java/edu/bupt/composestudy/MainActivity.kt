package edu.bupt.composestudy

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.SwipeableState
import androidx.compose.material.Text
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.intellij.lang.annotations.JdkConstants
import kotlin.math.roundToInt

class MainActivity: AppCompatActivity() {
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                ScrollSelectLayout()
            }
        }
    }
}

@Composable
fun ScrollSelectLayoutColumn(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
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
@Preview
@Composable
fun ScrollSelectColumnPreview() {
    Box(Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        ScrollSelectLayout()
    }
}


@ExperimentalMaterialApi
@Composable
fun <E> ScrollSelectLayout(items: List<E>, content: RowScope.(E) -> Unit) {

    var names = mutableListOf (
        ScrollSelectColumnItem(""),
        ScrollSelectColumnItem("Hello"),
        ScrollSelectColumnItem("Guan"),
        ScrollSelectColumnItem("Ruger"),
        ScrollSelectColumnItem("Compose"),
        ScrollSelectColumnItem("Scroller"),
        ScrollSelectColumnItem("World"),
        ScrollSelectColumnItem("Tom"),
        ScrollSelectColumnItem(""),
    )
    var itemWidth = 200.dp
    var itemWidthPx = with(LocalDensity.current) {
        itemWidth.toPx()
    }
    var itemHeight = 50.dp
    var itemHeightPx = with(LocalDensity.current) {
        itemHeight.toPx()
    }
    var anthors = mutableMapOf<Float, Int>()

    for (index in 0 .. names.size - 3) {
        anthors[-index * itemHeightPx] = index
    }
    var midItemIndexStart = (((names.size - 1) / 2) - 1).coerceAtLeast(0).coerceAtMost(names.size - 1)

    names[midItemIndexStart + 1].selected = true
    var swipeableState = rememberSwipeableState(initialValue = midItemIndexStart) {
        names[midItemIndexStart + 1].selected = false
        names[it + 1].selected = true
        midItemIndexStart = it
        true
    }
    Surface(
        elevation = 5.dp,
        shape = RoundedCornerShape(5.dp),
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
        ScrollSelectLayoutColumn(
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
            for (name in names) {
                Log.d("gzz", "name: ${name.text}")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = name.text,
                        color = if (name.selected) Color(0xff0288ce) else Color(0xffbbbbbb),
                        fontWeight = FontWeight.W500,
                        style = MaterialTheme.typography.body1
                    )
                }
            }
        }
    }
}
