package love.forte.simbot.mlh.window

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * 登录bot并根据操作进行
 */
@Preview
@Composable
fun loginBot(setTitle: SetTitle, setStep: SetStep) {
    Box(
        modifier = CenterModifier
    ) {
        Column {
            // 选择浏览器
            selectDriver()

            // 返回
            Button(
                onClick = { setStep(null) }
            ) {
                Text("返回")
            }
        }
    }
}


@Composable
fun selectDriver() {
    Box(
        modifier = Modifier.height(200.dp).fillMaxWidth()
    ) {
        Column {
            Row {
                Checkbox(false, onCheckedChange = {})
                Text("Chrome")
            }
            Row {
                Checkbox(false, onCheckedChange = {})
                Text("Safari")
            }

        }
    }
}