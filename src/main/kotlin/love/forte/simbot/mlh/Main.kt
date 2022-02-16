package love.forte.simbot.mlh


import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import love.forte.simbot.mlh.window.Home
import love.forte.simbot.mlh.window.Logo
import love.forte.simbot.mlh.window.SetStep
import love.forte.simbot.mlh.window.Step

typealias SetTitle = (String?) -> Unit

@OptIn(ExperimentalAnimationApi::class)
@Suppress("FunctionName")
@Composable
@Preview
fun FrameWindowScope.App(resetTitle: SetTitle, exit: () -> Unit) {

    var step by remember { mutableStateOf<Step?>(null) }


    val setStep: SetStep = { step = it }

    AnimatedContent(
        targetState = step
    ) { target ->
        if (target == null) {
            Home(resetTitle, setStep, exit)
        } else {
            target.doContent(this@App, resetTitle, setStep)
        }
    }

}


fun main() = application {
    //LoggerFactory.getLogger("simbot.mirai.login.helper.main")
    val default = "simbot-mirai登录工具"
    var title by remember { mutableStateOf(default) }
    MaterialTheme {
        Window(
            onCloseRequest = ::exitApplication,
            title = title,
            icon = Logo.painter
        ) {
            App(
                { title = it ?: default },
            ) {
                exitApplication()
            }
        }
    }
}


