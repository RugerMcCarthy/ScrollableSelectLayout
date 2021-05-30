package edu.bupt.composestudy

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.remember
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
                SlideSelectBarLayout(items,
                    onSuccess = {
                        Log.d("gzz", "$it")
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