package love.forte.simbot.mlh.window

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
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
@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
fun loginBot(setTitle: SetTitle, setStep: SetStep) {
    val verticalScrollState = rememberScrollState(0)
    var selectedDriver: WebDriverType? by remember { mutableStateOf(null) }
    var code by remember { mutableStateOf("") }
    var codeErr by remember { mutableStateOf(false) }
    var pass by remember { mutableStateOf("") }
    var passErr by remember { mutableStateOf(false) }
    var onLoginBot by remember { mutableStateOf(false) }
    var warningMessage: String? by remember { mutableStateOf(null) }



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

                    // 输入信息
                    inputInfo(
                        code,
                        {
                            code = it
                            if (pass.isNotEmpty()) {
                                codeErr = false
                            }
                        },
                        pass,
                        {
                            pass = it
                            if (pass.isNotEmpty()) {
                                passErr = false
                            }
                        }
                    )

                    // 选择浏览器
                    selectDriver(selectedDriver) { selectedDriver = it }

                    // 底端按钮
                    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        Button(
                            onClick = {
                                when {
                                    code.isEmpty() -> {
                                        warningMessage = "账号不可为空"
                                        codeErr = true
                                    }
                                    pass.isEmpty() -> {
                                        warningMessage = "密码不可为空"
                                        passErr = true
                                    }
                                    else -> {
                                        onLoginBot = true
                                    }
                                }
                            }
                        ) {
                            Text("验证")
                        }

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


                    if (warningMessage != null) {
                        AlertDialog(
                            modifier = Modifier.background(Color.Gray)
                                .height(200.dp)
                                .width(400.dp),
                            title = { Text("警告") },
                            onDismissRequest = {},
                            buttons = {
                                Box(Modifier.fillMaxWidth().padding(10.dp)) {
                                    Button(
                                        modifier = Modifier.wrapContentWidth(Alignment.End),
                                        onClick = { warningMessage = null },
                                    ) { Text("确认") }
                                }
                            },
                            text = { Text(warningMessage ?: "") }
                        )
                    }


                    AnimatedVisibility(onLoginBot) {
                        @Suppress("RemoveSingleExpressionStringTemplate")
                        runLoginBot(code, pass, selectedDriver!!) {
                            onLoginBot = false
                        }
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
fun selectDriver(currentDriver: WebDriverType?, setDriver: (WebDriverType?) -> Unit) {
    val verticalScrollState = rememberScrollState(0)
    var tooltip: String? by remember { mutableStateOf(null) }
    val browserTextColor by animateColorAsState(
        targetValue = if (tooltip == null) Color(0xff91a4fc) else Color.Red,
        animationSpec = tween(
            durationMillis = 500,
            easing = LinearEasing
        )
    )

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
                    visible = currentDriver == null
                ) {
                    Text("选择浏览器驱动")
                }

                // selected
                AnimatedVisibility(
                    visible = currentDriver != null
                ) {
                    if (currentDriver != null) {
                        //val tooltip: String?
                        tooltip = when {
                            isWindows && !currentDriver.windowsAble -> "此浏览器驱动仅支持windows系统"
                            isMac && !currentDriver.macAble -> "此浏览器驱动仅支持macOS系统"
                            else -> null
                        }
                        Row {
                            Text("使用 ")

                            AnimatedContent(
                                targetState = currentDriver,
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

                                val currentTooltip = tooltip
                                if (currentTooltip == null) {
                                    Text(
                                        fontWeight = FontWeight.Bold,
                                        color = browserTextColor,
                                        text = target.name
                                    )
                                } else {
                                    TooltipArea(
                                        tooltip = {
                                            Box(
                                                modifier = Modifier.background(
                                                    color = Color.Black,
                                                    shape = RoundedCornerShape(5.dp),
                                                ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = currentTooltip,
                                                    color = Color.White,
                                                    modifier = Modifier.padding(6.dp)
                                                )
                                            }
                                        },
                                        delayMillis = 200,
                                    ) {
                                        Text(
                                            fontWeight = FontWeight.Bold,
                                            color = browserTextColor,
                                            text = target.name
                                        )
                                    }

                                }

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
                        setDriver(if (currentDriver == driver) null else driver)
                    }
                    Row {
                        RadioButton(
                            selected = currentDriver == driver,
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
