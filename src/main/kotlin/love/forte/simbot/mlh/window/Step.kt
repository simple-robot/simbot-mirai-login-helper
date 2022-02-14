package love.forte.simbot.mlh.window

import androidx.compose.runtime.Composable

typealias SetStep = (Step?) -> Unit

/**
 * 进行的步骤，也是可选的内容选项
 */
enum class Step(
    val display: String, private val content: (@Composable (setTitle: SetTitle, setStep: SetStep) -> Unit)
) {

    /**
     * 1 安装 cer 证书.
     */
    INSTALL_CER("安装 cer 证书", { _, setStep -> installCer(setStep) }),

    /**
     * 2 登录BOT
     */
    LOGIN_BOT("登录验证BOT", { setTitle, setStep -> loginBot(setTitle, setStep) }),


    ;

    @Composable
    fun doContent(setTitle: SetTitle, resetStep: SetStep) {
        content(setTitle, resetStep)
    }

}