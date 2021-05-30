package edu.bupt.composestudy

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp

private lateinit var density: Density

@Composable
fun initDensity() {
    density = LocalDensity.current
}

fun Dp.toPx(): Float {
    with(density) {
        return this@toPx.toPx()
    }
}