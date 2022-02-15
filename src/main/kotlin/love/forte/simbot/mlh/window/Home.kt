@file:Suppress("FunctionName")

package love.forte.simbot.mlh.window

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import java.awt.Desktop
import java.net.URI

val CenterModifier = Modifier
    .fillMaxSize()
    .padding(20.dp)
    .wrapContentHeight(Alignment.Top)
    .wrapContentWidth(Alignment.CenterHorizontally)

/**
 * 进入的首页。
 */
@Preview
@Composable
fun Home(setTitle: SetTitle, resetStep: SetStep) {
    setTitle(null)
    Box(
        modifier = CenterModifier // Modifier.fillMaxSize()
    ) {
        Column {
            // logo
            Box(
                modifier = Modifier
                    .height(170.dp)
                    .width(170.dp)
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 30.dp)
            ) {
                logo()
            }

            Step.values().forEach { step ->
                StepButton(setTitle, step, resetStep)
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun logo() {
    var showNoDesktopWarn by remember { mutableStateOf(false) }
    Image(
        bitmap = Logo.logoBitmap,
        contentDescription = "simbot-logo",
        modifier = Modifier
            .wrapContentWidth(Alignment.CenterHorizontally)
            .wrapContentHeight(Alignment.CenterVertically)
            .fillMaxSize()
            .clickable(
                onClickLabel = "simbot",
                role = Role.Image,
            ) {
                showNoDesktopWarn = true
                // desktop(Desktop.Action.BROWSE)?.also { desktop ->
                //     desktop.browse(URI("https://github.com/ForteScarlet/simpler-robot"))
                // } ?: run {
                //     showNoDesktopWarn = true
                // }
            }
    )

    if(showNoDesktopWarn) {
        // Surface(
        //     modifier = Modifier
        // ) {
            AlertDialog(
                modifier = Modifier.height(200.dp).width(400.dp),
                title = { Text("无法打开链接") },
                onDismissRequest = { },
                buttons = {
                    Box(modifier = Modifier.fillMaxWidth().padding(10.dp)) {
                        Button(
                            modifier = Modifier.align(Alignment.CenterEnd),
                            onClick = { showNoDesktopWarn = false },
                        ) {
                            Text("确认")
                        }
                    }
                },
                text = {
                    Row {
                        Text("无法打开链接: ")
                        SelectionContainer {
                            Text(
                                modifier = Modifier,
                                color = Color.Blue,
                                fontStyle = FontStyle.Italic,
                                textDecoration = TextDecoration.Underline,
                                text = "https://github.com/ForteScarlet/simpler-robot"
                            )
                        }
                    }
                }
            )
        // }
    }

    // if (showNoDesktopWarn) {
    // }

}


@Composable
private fun StepButton(setTitle: SetTitle, step: Step, resetStep: SetStep) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally),
        onClick = {
            setTitle("${step.ordinal + 1}: ${step.display}")
            resetStep(step)
        }
    ) {
        Text("${step.ordinal + 1}: ${step.display}")
    }
}