package love.forte.simbot.mlh.window

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.FrameWindowScope
import love.forte.simbot.mlh.SetTitle

typealias SetStep = (Step?) -> Unit

/**
 * 进行的步骤，也是可选的内容选项
 */
enum class Step(
    val display: String,
    private val content: (@Composable (frameWindowScope: FrameWindowScope, setTitle: SetTitle, setStep: SetStep) -> Unit)
) {

    /**
     * 1 安装 cer 证书.
     */
    INSTALL_CER("安装 cer 证书", { frameWindowScope, _, setStep -> frameWindowScope.installCer(setStep) }),

    /**
     * 2 登录BOT
     */
    LOGIN_BOT("登录验证BOT", { _, setTitle, setStep -> loginBot(setTitle, setStep) }),

    /**
     * 3 查看说明
     */
    TIPS("查看说明", { _, setTitle, setStep -> theTips(setTitle, setStep) }),

    ;

    @Composable
    fun doContent(frameWindowScope: FrameWindowScope, setTitle: SetTitle, resetStep: SetStep) {
        content(frameWindowScope, setTitle, resetStep)
    }

}