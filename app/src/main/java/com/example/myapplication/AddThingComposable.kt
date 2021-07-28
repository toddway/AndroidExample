package com.example.myapplication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AddThing(viewModel: AddThingViewModel) {
    val lastThingText = rememberSaveable { mutableStateOf("") }
    val thingState = viewModel.thingFlow.collectAsState(null)
    thingState.value?.let { lastThingText.value = "$it" }
    AddThing(viewModel::generateThingButtonClicked, lastThingText.value)
}

@Preview
@Composable
fun AddThing(onClick : () -> Unit = {}, text : String = "") {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = text, Modifier.padding(8.dp), fontSize = 20.sp)
        Button(onClick = onClick) {
            Text(text = "Add a thing")
        }
    }
}