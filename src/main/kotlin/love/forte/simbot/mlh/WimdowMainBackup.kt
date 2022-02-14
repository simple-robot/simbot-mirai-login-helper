// // Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
// import androidx.compose.desktop.ui.tooling.preview.Preview
// import androidx.compose.foundation.*
// import androidx.compose.foundation.layout.*
// import androidx.compose.material.*
// import androidx.compose.runtime.*
// import androidx.compose.ui.Alignment
// import androidx.compose.ui.Modifier
// import androidx.compose.ui.graphics.Color
// import androidx.compose.ui.unit.dp
// import androidx.compose.ui.window.Window
// import androidx.compose.ui.window.application
// import kotlinx.coroutines.delay
// import love.forte.simbot.mlh.window.App
//
//
// @Composable
// @Preview
// fun App2() {
//     val text = "Hello, World!" // by remember { stateOf() }
//     var count by remember { mutableStateOf(0) }
//     var expanded by remember { mutableStateOf(false) }
//     var selected: String? by remember { mutableStateOf(null) }
//     val list = listOf("a", "b", "c")
//     var boxPadding by remember { mutableStateOf(10) }
//
//     MaterialTheme {
//         Column(Modifier.fillMaxSize()) {
//             var showSystemOut by remember { mutableStateOf(false) }
//
//             Button(modifier = Modifier.align(Alignment.CenterHorizontally), onClick = { showSystemOut = true }) {
//                 Text("ShowSystemOut")
//             }
//
//             val stateVertical = rememberScrollState(0)
//             val stateHorizontal = rememberScrollState(0)
//
//
//             if (showSystemOut) {
//                 val out = mutableStateListOf<String>() // by remember {  }
//                 LaunchedEffect(Unit) {
//                     while (true) {
//                         delay(200)
//                         out.add("Hello World\n")
//                     }
//                 }
//                 Window(onCloseRequest = {
//                     showSystemOut = false
//                 }) {
//                     Box(
//                         modifier = Modifier
//                             .fillMaxSize()
//                             .verticalScroll(stateVertical)
//                             .padding(12.dp)
//                             .horizontalScroll(stateHorizontal)
//                     ) {
//                         out.forEachIndexed { i, e ->
//                             Box(
//                                 modifier = Modifier.height(32.dp)
//                                     .width(400.dp)
//                                     .background(color = Color(200, 0, 0, 20))
//                                     .padding(start = 10.dp),
//                                 contentAlignment = Alignment.CenterStart
//                             ) {
//                                 Text("$i: $e")
//                             }
//                         }
//
//
//                         VerticalScrollbar(
//                             modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
//                             adapter = rememberScrollbarAdapter(stateVertical)
//                         )
//                         HorizontalScrollbar(
//                             modifier = Modifier.align(Alignment.BottomStart)
//                                 .fillMaxWidth()
//                                 .padding(end = 12.dp),
//                             adapter = rememberScrollbarAdapter(stateHorizontal)
//                         )
//                     }
//
//                 }
//             }
//
//
//             Row(
//                 modifier = Modifier.fillMaxSize()
//                     .wrapContentWidth(Alignment.CenterHorizontally)
//                     .wrapContentHeight(Alignment.Top)
//                     .padding(5.dp)
//             ) {
//                 Button(
//                     onClick = { expanded = !expanded },
//                 ) {
//                     Text(selected ?: "未选择")
//                 }
//
//                 DropdownMenu(
//                     expanded,
//                     onDismissRequest = { expanded = false },
//                 ) {
//                     list.forEach { element ->
//                         DropdownMenuItem(onClick = {
//                             expanded = false
//                             selected = element
//                         }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
//                             Text(element, Modifier.fillMaxSize())
//                         }
//                     }
//                 }
//             }
//         }
//
//
//     }
// }
//
// fun main() = application {
//     val title = mutableStateOf("")
//     Window(onCloseRequest = ::exitApplication) {
//         App()
//     }
// }
