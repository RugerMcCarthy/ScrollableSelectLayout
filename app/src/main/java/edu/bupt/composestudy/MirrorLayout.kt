package edu.bupt.composestudy

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.snap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.isFocused
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import dev.chrisbanes.accompanist.insets.navigationBarsWithImePadding
import edu.bupt.composestudy.ui.theme.ComposeStudyTheme
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

open class MirrorSync {
    var startEvent: Boolean by mutableStateOf(false)
        private set

    fun toggleStartEvent() {
        this.startEvent = !startEvent
    }
}

fun Modifier.flip(): Modifier {
    return this.rotate(180f).graphicsLayer(
        rotationY = 180f,
        rotationX = -75f,
        transformOrigin = TransformOrigin(0.5f, 1f),
        cameraDistance = 64f
    )
}

class CountdownTimerLayoutMirrorSync: MirrorSync() {
    var currentNum = 5
    var displayText by mutableStateOf("$currentNum")
    var textEditEnabled by mutableStateOf(true)
    var startBtnEnabled by mutableStateOf(true)
    var fontSize by mutableStateOf(50.sp)
    var sweepAngle = Animatable(0f)
    var textAlpha = Animatable(1f)
    var textScale = Animatable(1f)
    var textOffset = Animatable(0f)
    var focusEvent by mutableStateOf(false)
}

@Composable
inline fun <reified T: MirrorSync> MirrorLayout(block: @Composable (T)-> Unit, animation: (T) -> Unit) {
    val mirrorSync: T = remember{ T::class.java.newInstance() } // for safety
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        val scroll = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
                .navigationBarsWithImePadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            block(mirrorSync)
            Box(Modifier.width(IntrinsicSize.Min).height(IntrinsicSize.Min).flip().pointerInteropFilter {
                true
            }) {
                block(mirrorSync)
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black,
                                Color.Transparent,
                            )
                        )
                    )
                )
            }
            animation(mirrorSync)
        }
    }
}


@Composable
fun CountDownTimerClock(mirrorSync: CountdownTimerLayoutMirrorSync) {
    with(mirrorSync) {
        Box(modifier = androidx.compose.ui.Modifier
            .size(375.dp),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Canvas(modifier = androidx.compose.ui.Modifier
                .fillMaxSize()
                .padding(30.dp)
            ) {
                drawCircle(
                    color = Color(0xFF1E7171),
                    center = Offset(drawContext.size.width / 2f, drawContext.size.height / 2f),
                    style = Stroke(width = 20.dp.toPx())
                )
                drawArc(
                    color = Color(0xFF3BDCCE),
                    startAngle = -90f,
                    sweepAngle = sweepAngle.value,
                    useCenter = false,
                    style = Stroke(width = 20.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                )
            }
            Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                BasicTextField(
                    value = "$displayText",
                    onValueChange = {
                        if ((it.isDigitsOnly() || it.isEmpty()) && it.length <= 4) {
                            displayText = it
                            currentNum = it.toIntOrNull() ?: 1
                        }
                    },
                    textStyle = TextStyle(
                        fontSize = fontSize,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        color = androidx.compose.ui.graphics.Color.White
                    ),
                    modifier = androidx.compose.ui.Modifier
                        .graphicsLayer(
                            scaleX = textScale.value,
                            scaleY = textScale.value,
                            alpha = textAlpha.value,
                        )
                        .onFocusChanged {
                            focusEvent = it.isFocused
                            if (it.isFocused) {
                                kotlinx.coroutines.GlobalScope.launch {
                                    sweepAngle.animateTo(0f, snap())
                                }
                                if (displayText == "Compose") {
                                    fontSize = 50.sp
                                    displayText = "1"
                                }
                            } else {
                                if (displayText.isEmpty()) {
                                    displayText = "1"
                                }
                            }
                        },
                    enabled = textEditEnabled,
                    cursorBrush = SolidColor(androidx.compose.ui.graphics.Color.White)
                )
                Button(
                    onClick = {
                        toggleStartEvent()
                        startBtnEnabled = false
                        textEditEnabled = false
                    },
                    enabled = startBtnEnabled,
                    modifier = androidx.compose.ui.Modifier
                        .size(50.dp)
                        .offset(y = 30.dp)
                        .clip(RoundedCornerShape(50)),
                    colors = androidx.compose.material.ButtonDefaults.buttonColors(backgroundColor = Color(0xFFFF5722))
                ) {
                    if (!startEvent) {
                        Image(
                            painter = painterResource(id = R.drawable.countdown_start),
                            contentDescription = "Start",
                            modifier = androidx.compose.ui.Modifier.padding(start = 3.dp)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.countdown_stop),
                            contentDescription = "Stop"
                        )
                    }
                }
            }
        }
    }
}
