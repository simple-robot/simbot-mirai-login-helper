package love.forte.simbot.mlh.window

import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import love.forte.simbot.mlh.WebDriverType

/**
 * 出现新窗口, 并开始尝试登录.
 */
@Composable
fun runLoginBot(
    code: String,
    driver: WebDriverType,
    onCloseRequest: () -> Unit
) {
    var titleStepDisplay by remember { mutableStateOf("准备浏览器驱动-${driver.name.lowercase()}") }

    Window(
        onCloseRequest = onCloseRequest,
        title = "Bot登录验证-$titleStepDisplay",
        icon = Logo.painter
    ) {




    }


}