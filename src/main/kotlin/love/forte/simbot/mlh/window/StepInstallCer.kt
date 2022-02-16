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
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.AwtWindow
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.Window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runInterruptible
import java.awt.Desktop
import java.awt.FileDialog
import java.io.File


@Preview
@Composable
fun FrameWindowScope.installCer(setStep: SetStep) {
    val scope = rememberCoroutineScope()
    var showFileSaveDialog by remember { mutableStateOf(false) }

    if (showFileSaveDialog) {
        saveCerWindow(scope)
    }


    Box(
        modifier = Modifier.fillMaxSize().padding(20.dp).wrapContentHeight(Alignment.Top)
            .wrapContentWidth(Alignment.CenterHorizontally),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 5.dp),
        ) {

            // 说明/提示
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


            // 安装按钮
            Button(modifier = Modifier.align(Alignment.CenterHorizontally), onClick = {
                desktop(Desktop.Action.OPEN) { desktop ->
                    scope.launch(Dispatchers.IO) {
                        runInterruptible {
                            File(".cache").also {
                                it.mkdir()
                                val cerFile = File(it, CER_FILE_NAME)
                                cerFile.createNewFile()
                                cerFile.writeText(CER)
                                desktop.open(cerFile)
                            }
                        }
                    }
                }.orDo {
                    showFileSaveDialog = true
                }


            }) {
                Text("安装 $CER_FILE_NAME")
            }



            OutlinedButton(modifier = Modifier.align(Alignment.CenterHorizontally), onClick = { setStep(null) }) {
                Text("返回")
            }

        }
    }


}

@Composable
fun FrameWindowScope.saveCerWindow(scope: CoroutineScope) {
    AwtWindow(
        create = {
            object : FileDialog(window, "保存CER文件", SAVE) {
                override fun setVisible(value: Boolean) {
                    super.setVisible(value)
                    if (value) {
                        if (file != null) {
                            scope.launch(Dispatchers.IO) {
                                runInterruptible {
                                    val dir = File(directory)
                                    val save = dir.resolve(file)
                                    dir.mkdirs()
                                    save.createNewFile()
                                    save.writeText(CER)
                                }
                            }
                        } else {
                            // nothing
                        }
                    }
                }
            }
        },
        dispose = java.awt.Window::dispose,

        )
}


@Preview
@Composable
fun tipWindow(onClose: () -> Unit) {
    val verColl = rememberScrollState(0)
    Window(
        onCloseRequest = onClose,
        title = "证书说明",
        icon = Logo.painter,
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