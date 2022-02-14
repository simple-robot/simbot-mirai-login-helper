package love.forte.simbot.mlh.window

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window


@Preview
@Composable
fun installCer(setStep: SetStep) {
    Box(
        modifier = Modifier.fillMaxSize().padding(20.dp).wrapContentHeight(Alignment.Top)
            .wrapContentWidth(Alignment.CenterHorizontally),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 5.dp),
        ) {
            Button(modifier = Modifier.align(Alignment.CenterHorizontally), onClick = {
                // TODO save file to local
            }) {
                Text("保存 $CER_FILE_NAME 到本地")
            }

            var showTipWindow by remember { mutableStateOf(false) }
            Button(modifier = Modifier.align(Alignment.CenterHorizontally), onClick = {
                showTipWindow = !showTipWindow
            }) {
                Text("查看说明")
            }

            // 提示窗口
            if (showTipWindow) {
                tipWindow { showTipWindow = false }
            }


            Button(modifier = Modifier.align(Alignment.CenterHorizontally), onClick = { setStep(null) }) {
                Text("返回")
            }

        }
    }


}


@Preview
@Composable
fun tipWindow(onClose: () -> Unit) {
    val verColl = rememberScrollState(0)
    Window(
        onCloseRequest = onClose,
        title = "证书说明",
    ) {
        Box {
            Box(
                Modifier.padding(start = 40.dp, end = 40.dp, top = 10.dp, bottom = 10.dp)
                    .verticalScroll(state = verColl, flingBehavior = ScrollableDefaults.flingBehavior())
            ) {
                val text = "长篇大论".repeat(500)
                SelectionContainer {
                    Text(text)
                }
            }

            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd), adapter = rememberScrollbarAdapter(verColl)
            )
        }
    }
}