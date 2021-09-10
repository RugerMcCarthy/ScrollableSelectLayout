package edu.bupt.composestudy

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp



class MainActivity: AppCompatActivity() {
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
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
                var slideSelectBarState = rememberSaveable(saver =  SlideSelectBarState.Saver) {
                    SlideSelectBarState(2)
                }
                SlideSelectBarLayout(
                    items = items,
                    slideSelectBarState = slideSelectBarState,
                    modifier = Modifier.padding(vertical = 20.dp),
                    footer = {
                        Row(
                            Modifier
                                .fillMaxWidth()
                        ) {
                            Button(
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                                modifier = Modifier
                                    .weight(1f),
                                shape = RoundedCornerShape(0),
                                onClick = {
                                    Log.d("gzz", "index: ${slideSelectBarState.currentSwipeItemIndex}")
                                }
                            ) {
                                Text("OK")
                            }
                            Button(
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                                shape = RoundedCornerShape(0),
                                modifier = Modifier
                                    .weight(1f),
                                onClick = {
                                    Log.d("gzz", "failure")
                                }
                            ) {
                                Text("Cancel")
                            }
                        }
                    }
                ) { item, selected->
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
    }
}