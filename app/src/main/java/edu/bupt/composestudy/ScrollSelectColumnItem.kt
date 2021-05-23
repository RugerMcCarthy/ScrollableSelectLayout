package edu.bupt.composestudy

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class ScrollSelectColumnItem(text: String, selected: Boolean = false) {
    val text by mutableStateOf(text)
    var selected by mutableStateOf(selected)
}
