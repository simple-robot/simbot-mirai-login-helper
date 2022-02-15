package love.forte.simbot.mlh.window

import androidx.compose.animation.*
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import love.forte.simbot.mlh.WebDriverType

/**
 * 登录bot并根据操作进行
 */
@Preview
@Composable
fun loginBot(setTitle: SetTitle, setStep: SetStep) {
    val verticalScrollState = rememberScrollState(0)
    Box {
        Box(
            modifier = CenterModifier.verticalScroll(verticalScrollState)
        ) {
            Box(
                modifier = Modifier.padding(start = 150.dp, end = 150.dp)
            ) {
                Column(
                    modifier = Modifier.padding(top = 5.dp)
                ) {
                    var code by remember { mutableStateOf("") }
                    var pass by remember { mutableStateOf("") }

                    // 输入信息
                    inputInfo(
                        code, { code = it },
                        pass, { pass = it }
                    )

                    // 选择浏览器
                    selectDriver()


                    // 返回
                    OutlinedButton(
                        onClick = { setStep(null) },
                        modifier = Modifier
                            .wrapContentHeight(Alignment.Top)
                            .wrapContentWidth(Alignment.CenterHorizontally)

                    ) {
                        Text("返回")
                    }
                }
            }

        }

        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd),
            adapter = rememberScrollbarAdapter(verticalScrollState)
        )
    }

}

/**
 * 账号信息输入
 */
@Composable
fun inputInfo(
    code: String, setCode: (String) -> Unit,
    pass: String, setPass: (String) -> Unit,
) {
    Box(
        modifier = Modifier
            .height(150.dp)
            .fillMaxSize()
            .wrapContentHeight(Alignment.Top)
            .wrapContentWidth(Alignment.CenterHorizontally)
    ) {
        Column {
            OutlinedTextField(
                value = code,
                onValueChange = { setCode(it) },
                label = { Text("账号") },
                placeholder = { Text("请输入账号") }
            )
            OutlinedTextField(
                value = pass,
                onValueChange = { setPass(it) },
                label = { Text("密码") },
                visualTransformation = PasswordVisualTransformation(),
                placeholder = { Text("请输入密码") }
            )
        }
    }
}

/**
 * 选择浏览器驱动
 */
@OptIn(ExperimentalTextApi::class, ExperimentalAnimationApi::class)
@Composable
fun selectDriver() {
    val verticalScrollState = rememberScrollState(0)
    Box(
        modifier = Modifier
            .height(300.dp)
            .fillMaxSize()
            .padding(top = 5.dp)
            .border(
                width = 2.dp,
                brush = Brush.sweepGradient(listOf(Color.Gray, Color.Red, Color.Blue)),
                shape = MaterialTheme.shapes.small
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            var selectedDriver: WebDriverType? by remember { mutableStateOf(null) }
            val driver = selectedDriver
            val currentOs = System.getProperty("os.name")?.lowercase() ?: ""
            val isWindows = "window" in currentOs
            val isMac = "mac" in currentOs


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .padding(10.dp)
                    .wrapContentHeight(Alignment.Top)
                    .wrapContentWidth(Alignment.CenterHorizontally),
            ) {
                // nothing
                AnimatedVisibility(
                    visible = driver == null
                ) {
                    Text("选择浏览器驱动")
                }

                // selected
                AnimatedVisibility(
                    visible = driver != null
                ) {
                    if (driver != null) {
                        val color = when {
                            !isWindows && !isMac -> Color(0xffffeb46) // Color.Yellow
                            isWindows && !driver.windowsAble -> Color.Red // MaterialTheme.colors.onError
                            isMac && !driver.macAble -> Color.Red
                            else -> Color(0xff91a4fc) // Color.Blue // MaterialTheme.colors.onBackground
                        }
                        Row {
                            Text("使用 ")

                            AnimatedContent(
                                targetState = driver,
                                transitionSpec = {
                                    // see https://developer.android.google.cn/jetpack/compose/animation?authuser=0#animatedcontent

                                    // Compare the incoming number with the previous number.
                                    if (targetState.ordinal > initialState.ordinal) {
                                        // If the target number is larger, it slides up and fades in
                                        // while the initial (smaller) number slides up and fades out.
                                        slideInVertically { height -> height } + fadeIn() with
                                                slideOutVertically { height -> -height } + fadeOut()
                                    } else {
                                        // If the target number is smaller, it slides down and fades in
                                        // while the initial number slides down and fades out.
                                        slideInVertically { height -> -height } + fadeIn() with
                                                slideOutVertically { height -> height } + fadeOut()
                                    }.using(
                                        // Disable clipping since the faded slide-in/out should
                                        // be displayed out of bounds.
                                        SizeTransform(clip = false)
                                    )
                                }
                            ) { target ->
                                Text(
                                    fontWeight = FontWeight.Bold,
                                    color = color,
                                    text = target.name
                                )
                            }

                            Text(" 浏览器")
                        }
                    }

                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(verticalScrollState)
                    .padding(top = 5.dp, start = 100.dp, end = 100.dp)
                    .selectableGroup()
            ) {

                WebDriverType.values().forEach { driver ->
                    val onClick: () -> Unit = {
                        selectedDriver = if (selectedDriver == driver) null else driver
                    }
                    Row {
                        RadioButton(
                            selected = selectedDriver == driver,
                            modifier = Modifier.size(30.dp),
                            onClick = onClick
                        )
                        Text(
                            modifier = Modifier
                                .fillMaxSize()
                                .align(Alignment.CenterVertically)
                                .clickable(onClick = onClick),
                            text = driver.name
                        )
                    }
                }
            }

        }
    }
}
