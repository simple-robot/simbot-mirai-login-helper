package love.forte.simbot.mlh.window

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import love.forte.simbot.mlh.DriverSelector
import java.awt.color.ColorSpace

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
                        onClick = { setStep(null) }
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


@OptIn(ExperimentalTextApi::class)
@Composable
fun selectDriver() {
    val verticalScrollState = rememberScrollState(0)
    Box(
        modifier = Modifier
            .height(500.dp)
            .fillMaxSize()
            .padding(top = 5.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            var selectedDriver: DriverSelector? by remember { mutableStateOf(null) }

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .wrapContentHeight(Alignment.Top)
                    .wrapContentWidth(Alignment.CenterHorizontally),
                text = selectedDriver?.let {
                    buildAnnotatedString {
                        append("使用 ")
                        val name = it.name
                        val currentOs = System.getProperty("os.name")?.lowercase() ?: ""
                        val isWindows = "window" in currentOs
                        val isMac = "mac" in currentOs
                        withStyle(
                            TextStyle.Default.toSpanStyle().copy(
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    !isWindows && !isMac -> Color(0xffffeb46) // Color.Yellow
                                    isWindows && !it.windowsAble -> Color.Red // MaterialTheme.colors.onError
                                    isMac && !it.macAble -> Color.Red
                                    else ->  Color(0xff91a4fc) // Color.Blue // MaterialTheme.colors.onBackground
                                }
                            )
                        ) {
                            append(name)
                        }


                        append(" 浏览器")
                    }
                } ?: AnnotatedString("选择浏览器驱动")
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(verticalScrollState)
                    .padding(top = 5.dp, start = 100.dp)
                    .background(Color.White)
                    .selectableGroup()
            ) {

                DriverSelector.values().forEach { driver ->
                    val onClick: () -> Unit = { selectedDriver = driver }
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
