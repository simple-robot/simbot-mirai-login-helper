package love.forte.simbot.mlh

import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.json.Json
import net.lightbody.bmp.BrowserMobProxy
import net.mamoe.mirai.Bot
import net.mamoe.mirai.network.CustomLoginFailedException
import net.mamoe.mirai.network.LoginFailedException
import net.mamoe.mirai.utils.BotConfiguration
import net.mamoe.mirai.utils.LoginSolver
import org.openqa.selenium.WebDriver
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.resume

/**
 *
 * @author ForteScarlet
 */
class SeleniumLoginSolver(
    proxy: BrowserMobProxy,
    private val onSolvePicCaptcha: suspend (bot: Bot, data: ByteArray) -> String? = { bot, data ->
        val defaultSolver = BotConfiguration.Default.loginSolver ?: throw ICustomLoginFailedException(true)
        defaultSolver.onSolvePicCaptcha(bot, data)
    },

    driverGetter: () -> WebDriver,

    private val onSolveUnsafeDeviceLoginVerify: suspend (bot: Bot, url: String) -> String? = { bot, url ->
        val defaultSolver = BotConfiguration.Default.loginSolver ?: throw ICustomLoginFailedException(true)
        defaultSolver.onSolveUnsafeDeviceLoginVerify(bot, url)
    }

) : LoginSolver() {

    private val json = Json {
        isLenient = true
        ignoreUnknownKeys = true

    }
    private val driver: WebDriver = driverGetter()

    fun quit() {
        driver.quit()
    }

    fun close() {
        driver.close()
    }

    private val logger =
        LoggerFactory.getLogger("love.forte.simbot.mirai-login-helper.mlh.SeleniumLoginSolver")

    override val isSliderCaptchaSupported: Boolean
        get() = true

    private val sliderCaptchaWaiting = AtomicReference<CancellableContinuation<String>>(null)

    init {

        proxy.addResponseFilter { _, contents, messageInfo ->
            if ("https://t.captcha.qq.com/cap_union_new_verify" in messageInfo.originalUrl) {
                val textContents = contents.textContents
                logger.info("response text: $textContents")
                val resp = json.decodeFromString(SolveSliderCaptchaResponse.serializer(), textContents)
                sliderCaptchaWaiting.updateAndGet { curr ->
                    if (curr != null) {
                        if ("0" == resp.errorCode && (resp.ticket?.isNotEmpty() == true)) {
                            curr.resume(resp.ticket)
                            return@updateAndGet null
                        }
                    }
                    curr
                }
            }
        }
    }

    /**
     * 处理图片验证码.
     *
     * 返回 `null` 以表示无法处理验证码, 将会刷新验证码或重试登录.
     * 抛出一个 [LoginFailedException] 以正常地终止登录, 抛出任意其他 [Exception] 将视为异常终止
     *
     * @throws LoginFailedException
     */
    override suspend fun onSolvePicCaptcha(bot: Bot, data: ByteArray): String? {
        println("图片验证码！")
        val loginSolver = BotConfiguration.Default.loginSolver ?: throw ICustomLoginFailedException(true)
        return loginSolver.onSolvePicCaptcha(bot, data)
    }

    // suspendCancellableCoroutine { continuation ->
    //     logger.info("OnSolvePicCaptcha URL: {}", data)
    // }

    /**
     * 处理滑动验证码.
     *
     * 返回 `null` 以表示无法处理验证码, 将会刷新验证码或重试登录.
     * 抛出一个 [LoginFailedException] 以正常地终止登录, 抛出任意其他 [Exception] 将视为异常终止
     *
     * @throws LoginFailedException
     * @return 验证码解决成功后获得的 ticket.
     */
    override suspend fun onSolveSliderCaptcha(bot: Bot, url: String): String? =
        suspendCancellableCoroutine { continuation ->
            logger.info("OnSolveSliderCaptcha URL: {}", url)
            sliderCaptchaWaiting.updateAndGet { old ->
                old?.cancel()
                continuation
            }
            bot.launch {
                // open driver
                driver.get(url)
                /*
                    Request URL: https://t.captcha.qq.com/cap_union_new_verify
                    {
                        "errorCode":"0",
                        "randstr":"@hpD",
                        "ticket":"t03jO6JrbOE03h0R0jQyBbrBlFb2v7ikIIqlYXQAiQ8WOxl1DmOos9OrVl7ZAjNPF9lUMhDCy-H6MR4hqGZkraKTOFc-sJNCFOjS2HKo9YnxEBZzSy1DWsYCLp75yB7TPZy",
                        "errMessage":"",
                        "sess":""
                    }
                 */


            }
        }

    /**
     * 处理不安全设备验证.
     *
     * 返回值保留给将来使用. 目前在处理完成后返回任意内容 (包含 `null`) 均视为处理成功.
     * 抛出一个 [LoginFailedException] 以正常地终止登录, 抛出任意其他 [Exception] 将视为异常终止.
     *
     * @return 任意内容. 返回值保留以供未来更新.
     * @throws LoginFailedException
     */
    override suspend fun onSolveUnsafeDeviceLoginVerify(bot: Bot, url: String): String? {
        return onSolveUnsafeDeviceLoginVerify.invoke(bot, url)
        // val loginSolver = BotConfiguration.Default.loginSolver ?: throw ICustomLoginFailedException(true)
        // return loginSolver.onSolveUnsafeDeviceLoginVerify(bot, url)

    }
    // suspendCancellableCoroutine { continuation ->
    //     logger.info("OnSolveUnsafeDeviceLoginVerify URL: {}", url)
    //     continuation.resumeWithException(ICustomLoginFailedException(true))
    // }

}

class ICustomLoginFailedException : CustomLoginFailedException {
    constructor(killBot: Boolean) : super(killBot)
    constructor(killBot: Boolean, message: String?) : super(killBot, message)
    constructor(killBot: Boolean, message: String?, cause: Throwable?) : super(killBot, message, cause)
    constructor(killBot: Boolean, cause: Throwable?) : super(killBot, cause)
}

@kotlinx.serialization.Serializable
private data class SolveSliderCaptchaResponse(
    val errorCode: String? = null,
    val randstr: String? = null,
    val ticket: String? = null,
    val errMessage: String? = null,
    val sess: String? = null
)