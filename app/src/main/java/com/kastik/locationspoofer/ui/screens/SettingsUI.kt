package com.kastik.locationspoofer.ui.screens

import android.text.Layout.Alignment
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Divider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.tooling.preview.Preview
import java.lang.reflect.Modifier
import androidx.compose.foundation.layout.fillMaxSize


@Preview
@Composable
fun Settings(){
        Box() {
        Column(verticalArrangement = Arrangement.Top) {
            Row() {
                Text( text = "Set custom altitude")
                TextField(value = "0", onValueChange = {})
            }
            Divider()
            Row {
                Text("Enable XPosed")
                Switch(true, onCheckedChange = {})
            }
        }
    }

}