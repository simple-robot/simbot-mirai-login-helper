package love.forte.simbot.mlh.window

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

typealias SetTitle = (String?) -> Unit

@Suppress("FunctionName")
@Composable
@Preview
fun App(resetTitle: SetTitle) {

    var step by remember { mutableStateOf<Step?>(null) }

    MaterialTheme(
        // colors = Colors(
        //     primary = Color(0x00a0d6),
        //     primaryVariant = Color(0x007cb0),
        //     secondary = Color(0x3e7c43),
        //     secondaryVariant = Color(0x5aaf61),
        //     background = Color(0x3e547c),
        //     surface = Color(0x3e547c),
        //     error = Color(0xe2586f),
        //     onPrimary = Color(0x262626),
        //     onSecondary = Color(0x262626),
        //     onBackground = Color(0x262626),
        //     onSurface = Color(0x262626),
        //     onError = Color(0x4a1a25),
        //     isLight = true
        // )
    ) {

        val setStep: SetStep = { step = it }

        with(step) {
            if (this == null) {
                Home(resetTitle, setStep)
            } else {
                doContent(resetTitle, setStep)
            }
        }

    }
}

fun main() = application {
    val default = "simbot-mirai登录工具"
    var title by remember { mutableStateOf(default) }
    Window(
        onCloseRequest = ::exitApplication,
        title = title
    ) {
        App {
            title = it ?: default
        }
    }
}


