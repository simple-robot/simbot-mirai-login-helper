@file:Suppress("FunctionName")

package love.forte.simbot.mlh.window

import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
fun WindowScope.Home(setTitle: SetTitle, resetStep: SetStep, exit: () -> Unit) {
    val onExitButtonInteractionSource = MutableInteractionSource()
    var onExitButton by remember { mutableStateOf(false) }
    val brushColorA by animateColorAsState(if (onExitButton) Color.Red else Color.Green)


    LaunchedEffect(Unit) {
        onExitButtonInteractionSource.interactions
            .onEach {
                if (it is HoverInteraction) {
                    onExitButton = it is HoverInteraction.Enter
                }
            }
            .launchIn(this)

    }

    setTitle(null)

    BoxWithConstraints(
        modifier = CenterModifier//.background(Color.Red)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size((this@BoxWithConstraints.maxWidth * 0.2F))
            ) {
                logo()
            }

            Step.values().forEachIndexed { i, step ->
                this@BoxWithConstraints.StepButton(setTitle, step, resetStep, (i + 1) * 120L)
            }


            Row(
                modifier = Modifier
                    .fillMaxWidth().padding(top = 30.dp, bottom = 25.dp, start = 50.dp, end = 50.dp)
                    .align(Alignment.CenterHorizontally)
                // .absolutePadding(top = 15.dp, bottom = 15.dp)
            ) {
                // 水平线
                Spacer(
                    Modifier.background(Color.LightGray)
                        .height(1.dp)
                        .fillMaxWidth()

                )
            }


            OutlinedButton(
                modifier = Modifier
                    //.width(this@BoxWithConstraints.maxWidth * 0.2F)
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .hoverable(interactionSource = remember { onExitButtonInteractionSource }),
                onClick = {
                    exit()
                },
                border = ButtonDefaults.outlinedBorder.copy(
                    width = 2.dp,
                    brush = Brush.linearGradient(listOf(Color.Blue, brushColorA))
                ),
            ) {
                Text("退出")
            }
        }
    }
}


@OptIn(ExperimentalAnimationApi::class)
private val entryAnimates: List<() -> EnterTransition> = listOf(
    { fadeIn() },
    { scaleIn() },
    { expandIn() },
    { expandHorizontally() },
    { expandVertically() },
).let { list ->
    val newList = list.toMutableList()
    // 2*2交叉合并
    for (i in list.indices) {
        for (j in (i + 1) until list.size) {
            newList.add { list[i]() + list[j]() }
        }
    }
    newList
}

@OptIn(ExperimentalAnimationApi::class)
private val exitAnimates: List<() -> ExitTransition> = listOf(
    { fadeOut() },
    { scaleOut() },
    { shrinkOut() },
    { shrinkHorizontally() },
    { shrinkVertically() },
).let { list ->
    val newList = list.toMutableList()
    // 2*2交叉合并
    for (i in list.indices) {
        for (j in (i + 1) until list.size) {
            newList.add { list[i]() + list[j]() }
        }
    }
    newList
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun BoxScope.logo() {
    val logoShowState = remember {
        MutableTransitionState(false).apply {
            // Start the animation immediately.
            targetState = true
        }
    }
    var showNoDesktopWarn by remember { mutableStateOf(false) }
    AnimatedVisibility(
        visibleState = logoShowState,
        enter = entryAnimates.random()(),
        exit = exitAnimates.random()(),
    ) {
        Image(
            bitmap = Logo.logoBitmap,
            contentDescription = "simbot-logo",
            modifier = Modifier
                .wrapContentWidth(Alignment.CenterHorizontally)
                .wrapContentHeight(Alignment.CenterVertically)
                .fillMaxSize()
                .padding(15.dp)
                .clickable(
                    onClickLabel = "simbot",
                    role = Role.Image,
                ) {
                    desktop(Desktop.Action.BROWSE) {
                        it.browse(URI("https://github.com/ForteScarlet/simpler-robot"))
                    }.orDo {
                        showNoDesktopWarn = true
                    }
                }
        )
    }


    if (showNoDesktopWarn) {
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
private fun BoxWithConstraintsScope.StepButton(setTitle: SetTitle, step: Step, resetStep: SetStep, delayTime: Long) {
    val showState = remember {
        MutableTransitionState(false).apply {
            // Start the animation immediately.
            // targetState = true
        }
    }

    LaunchedEffect(Unit) {
        delay(delayTime)
        showState.targetState = true
    }

    AnimatedVisibility(
        visibleState = showState,
        enter = entryAnimates.random()(),
        exit = exitAnimates.random()(),
    ) {
        Button(
            modifier = Modifier
                .width(maxWidth * 0.2f),
            onClick = {
                setTitle("${step.ordinal + 1}: ${step.display}")
                resetStep(step)
            }
        ) {
            Text("${step.ordinal + 1}: ${step.display}")
        }
    }
}