package love.forte.simbot.mlh.window

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.ScrollableDefaults.flingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowScope
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.suspendCancellableCoroutine
import love.forte.simbot.mlh.SeleniumLoginSolver
import love.forte.simbot.mlh.WebDriverType
import love.forte.simbot.mlh.simbotMiraiDeviceInfo
import love.forte.simbot.utils.runWithInterruptible
import net.lightbody.bmp.BrowserMobProxy
import net.lightbody.bmp.BrowserMobProxyServer
import net.lightbody.bmp.client.ClientUtil
import net.lightbody.bmp.proxy.CaptureType
import net.mamoe.mirai.Bot
import net.mamoe.mirai.BotFactory
import org.openqa.selenium.Proxy
import org.openqa.selenium.remote.CapabilityType
import org.openqa.selenium.remote.DesiredCapabilities
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream
import java.net.URL
import kotlin.coroutines.resume


class RunLoginBotState(
    val code: String,
    val pass: String,
    val driverType: WebDriverType,
    private val doCloseRequest: () -> Unit
) {
    val logger: Logger = LoggerFactory.getLogger(RunLoginBotState::class.java)
    var proxy: BrowserMobProxy? by mutableStateOf(null)
    var solver: SeleniumLoginSolver? by mutableStateOf(null)
    var bot: Bot? by mutableStateOf(null)

    fun closeRequest() {
        doCloseRequest()
    }
}

/**
 * 出现新窗口, 并开始尝试登录.
 */
@Composable
fun runLoginBot(state: RunLoginBotState) {
    val scrollState = rememberScrollState(0)

    Window(
        onCloseRequest = {
            // abort proxy
            state.proxy?.abort()
            state.solver?.quit()
            state.bot?.close()

            state.proxy = null
            state.solver = null
            state.bot = null

            state.closeRequest()
        },
        title = "Bot登录验证",
        icon = Logo.painter
    ) {
        Box {
            Box(
                modifier = Modifier.verticalScroll(
                    state = scrollState,
                    flingBehavior = flingBehavior(),
                ).padding(40.dp)
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    if (state.bot == null) {
                        val prepareBrowserDriverState = remember { PrepareBrowserDriverState(state) }
                        // 准备浏览器驱动
                        prepareBrowserDriver(prepareBrowserDriverState)

                        // 图片验证
                        if (prepareBrowserDriverState.onSolvePicCaptcha != null) {
                            onSolvePicCaptcha(prepareBrowserDriverState)
                        }

                        // 手机设备验证
                        if (prepareBrowserDriverState.onSolveUnsafeDeviceLoginVerify != null) {
                            onSolveUnsafeDeviceLoginVerify(prepareBrowserDriverState)
                        }

                        if (state.solver != null) {
                            // 准备登录
                            doLogin(remember { DoLoginState(state) })
                        }
                    } else {
                        showBot(state.bot!!)
                    }


                }
            }

            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd),
                adapter = rememberScrollbarAdapter(scrollState)
            )
        }
    }
}


//region 准备浏览器驱动
class PrepareBrowserDriverState(val runState: RunLoginBotState) {
    var step by mutableStateOf("")

    // 图片验证码.
    var onSolvePicCaptcha: Pair<ByteArray, CancellableContinuation<String?>>? by mutableStateOf(null)

    // 设备验证码.
    var onSolveUnsafeDeviceLoginVerify: Pair<String, CancellableContinuation<String?>>? by mutableStateOf(null)
}

/**
 * 准备浏览器驱动信息。
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun prepareBrowserDriver(state: PrepareBrowserDriverState) {

    Row {
        Text(text = "1: 准备浏览器驱动... ")
        AnimatedContent(targetState = state.step) { step ->
            Text(
                text = step.takeIf { it.isNotEmpty() } ?: ""
            )
        }
    }

    if (state.runState.solver == null) {
        LaunchedEffect(Unit) {
            try {
                // 准备驱动
                val solver = doPrepareBrowserDriver(state)
                state.step = "selenium login solver准备完成"
                state.runState.solver = solver
            } catch (e: Throwable) {
                state.runState.logger.error("浏览器驱动准备失败", e)
                state.step = "浏览器驱动准备失败！\n\n\n" + e.stackTraceToString()
            }
        }
    }


}

private suspend fun doPrepareBrowserDriver(state: PrepareBrowserDriverState): SeleniumLoginSolver =
    runWithInterruptible(Dispatchers.IO) {
        // start the proxy
        state.step = "准备proxy..."
        val proxy: BrowserMobProxy = state.runState.proxy ?: run {
            state.step = "准备proxy...构建proxy..."
            BrowserMobProxyServer().also {
                val old = state.runState.proxy
                state.runState.proxy = it
                old?.abort()
                state.step = "准备proxy...启动proxy..."
                it.start()
            }
        }

        // get the Selenium proxy object
        state.step = "准备selenium proxy..."
        val seleniumProxy: Proxy = ClientUtil.createSeleniumProxy(proxy)

        // configure it as a desired capability
        val capabilities = DesiredCapabilities()
        capabilities.setCapability(CapabilityType.PROXY, seleniumProxy)

        state.step = "准备 ${state.runState.driverType.name.lowercase()} driver manager..."
        val driverManager = state.runState.driverType.driverManager()
        driverManager.config().also { config ->
            config.cachePath = ".cache"
        }
        // setup
        state.step = "设置 ${state.runState.driverType.name.lowercase()} driver manager...\n" +
                "tips: 如果持续时间过长，请尝试重新进入或重启应用"
        driverManager.setup()

        // enable more detailed HAR capture, if desired (see CaptureType for the complete list)
        // proxy.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT)
        proxy.enableHarCaptureTypes(CaptureType.getResponseCaptureTypes())

        state.step = "准备selenium login solver..."
        SeleniumLoginSolver(proxy,
            driverGetter = state.runState.driverType.driverFactory(capabilities),
            onSolvePicCaptcha = { _, data ->
                state.step = "图片验证码处理..."
                suspendCancellableCoroutine { continuation ->
                    state.onSolvePicCaptcha = data to continuation
                }
            },
            onSolveUnsafeDeviceLoginVerify = { _, url ->
                state.step = "设备验证处理..."
                suspendCancellableCoroutine { continuation ->
                    state.onSolveUnsafeDeviceLoginVerify = url to continuation
                }
            }
        )
    }
//endregion


//region 登录BOT
class DoLoginState(val runState: RunLoginBotState) {
    var step: AnnotatedString by mutableStateOf(AnnotatedString(""))
}

/**
 * 进行登录
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun doLogin(state: DoLoginState) {

    Text("2: 准备登录... ")
    AnimatedContent(state.step) { step ->
        Text(
            text = step
        )
    }

    // DoLogin
    LaunchedEffect(Unit) {
        val solver = state.runState.solver!!
        try {
            state.step = AnnotatedString("准备构建BOT...")
            val bot = BotFactory.newBot(state.runState.code.toLong(), state.runState.pass) {
                loginSolver = solver
                deviceInfo = { bot -> simbotMiraiDeviceInfo(bot.id, 1) }
                parentCoroutineContext = currentCoroutineContext()
            }.apply {
                state.step = AnnotatedString("准备登录BOT...")
                login()
                state.step = AnnotatedString("登录成功")
            }
            state.runState.bot = bot
        } catch (e: Throwable) {
            state.runState.logger.error("登录失败", e)
            state.step = buildAnnotatedString {
                append("登录失败! : ")
                withStyle(SpanStyle(Color.Red)) {
                    append(e.localizedMessage)
                }
                append("\n\n")
                withStyle(
                    ParagraphStyle(
                        textAlign = TextAlign.Justify,
                        textDirection = TextDirection.ContentOrLtr,
                    )
                ) {
                    append(e.stackTraceToString())
                }

            }
        }
        solver.close() // 关闭窗口
    }

}
//endregion


/**
 * 图片验证码
 */
@Composable
private fun WindowScope.onSolvePicCaptcha(state: PrepareBrowserDriverState) {
    val (data, con) = state.onSolvePicCaptcha ?: return
    var textValue by remember { mutableStateOf("") }
    fun doClose() {
        con.cancel()
        state.onSolvePicCaptcha = null
    }
    val verColl = rememberScrollState(0)
    Window(
        onCloseRequest = { doClose() },
        title = "图片验证码处理",
        icon = Logo.painter,
    ) {
        Box {
            Box(
                Modifier.verticalScroll(state = verColl, flingBehavior = flingBehavior())
            ) {
                Column {
                    Image(bitmap = loadImageBitmap(data.inputStream()), contentDescription = "图片验证码")
                    TextField(
                        value = textValue,
                        onValueChange = { textValue = it.trim() },
                        placeholder = { Text("输入图片验证码结果") },
                        singleLine = true,
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                con.resume(null)
                                state.onSolvePicCaptcha = null
                            }
                        ) {
                            Text("刷新")
                        }

                        OutlinedButton(
                            onClick = { doClose() }
                        ) {
                            Text("关闭")
                        }

                    }
                }

                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd), adapter = rememberScrollbarAdapter(verColl)
                )
            }

        }


    }

}

/**
 * 设备验证
 */
@Composable
private fun WindowScope.onSolveUnsafeDeviceLoginVerify(state: PrepareBrowserDriverState) {
    val (url, con) = state.onSolveUnsafeDeviceLoginVerify ?: return
    var qrImgData: ByteArray? by remember { mutableStateOf(null) }
    fun doClose() {
        con.cancel()
        state.onSolveUnsafeDeviceLoginVerify = null
    }
    Window(
        onCloseRequest = { doClose() },
        title = "设备验证处理",
        icon = Logo.painter,
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(40.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row {
                Text("请通过 ")
                Text(text = "手机QQ", color = Color.Red, fontWeight = FontWeight.Bold)
                Text(" 扫描下面的二维码")
            }

            if (qrImgData == null) {
                LaunchedEffect(Unit) {
                    qrImgData = createQrCodeImg(url)
                }
            }

            // 二维码
            qrImgData?.inputStream()?.use { inp ->
                Image(bitmap = loadImageBitmap(inp), contentDescription = "链接")
            }


            // 文字
            Row {
                Text("或直接复制并从 ")
                Text(text = "手机QQ", color = Color.Red, fontWeight = FontWeight.Bold)
                Text(" 中打开此链接: ")
            }
            Text(url)


            Button(
                onClick = { doClose() }
            ) {
                Text("操作完成")
            }

        }

    }
}


private suspend fun createQrCodeImg(url: String) = runWithInterruptible(Dispatchers.IO) {
    // 二维码基本参数设置
    val hints: MutableMap<EncodeHintType, Any?> = mutableMapOf(
        // 设置编码字符集utf-8
        EncodeHintType.CHARACTER_SET to "utf-8",
        // 设置纠错等级L/M/Q/H,纠错等级越高越不易识别，当前设置等级为最高等级H
        EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.M,
        // 可设置范围为0-10，但仅四个变化0 1(2) 3(4 5 6) 7(8 9 10)
        EncodeHintType.MARGIN to 0,
    )

    // 创建位矩阵对象
    val writer = QRCodeWriter()
    val bitMatrix = writer.encode(url, BarcodeFormat.QR_CODE, 400, 400, hints)

    // 位矩阵对象转流对象
    val os = ByteArrayOutputStream()
    MatrixToImageWriter.writeToStream(bitMatrix, "png", os)
    bitMatrix.clear()
    os.toByteArray()
}


/**
 * 展示Bot
 */
@Composable
private fun ColumnScope.showBot(bot: Bot) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(50.dp)
            .align(Alignment.CenterHorizontally)
    ) {
        val avatar = remember { URL(bot.avatarUrl) }
        var avatarBitMap: ImageBitmap? by remember { mutableStateOf(null) }
        LaunchedEffect(Unit) {
            avatarBitMap = runWithInterruptible(Dispatchers.IO) {
                avatar.openStream().use { inp -> loadImageBitmap(inp) }
            }
        }
        Surface(
            shape = RoundedCornerShape(500.dp)
        ) {
            if (avatarBitMap != null) {
                Image(
                    bitmap = avatarBitMap!!,
                    contentDescription = "头像",
                )
            } else {
                Image(
                    painter = ColorPainter(Color.Gray),
                    contentDescription = "头像",
                    modifier = Modifier.size(160.dp),
                    alpha = 0.8F
                )
            }
        }

        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.Center,

            ) {
            Text(bot.nick)
            Text(bot.id.toString())
            Text("bot friend size: ${bot.friends.size}")
            Text("bot group size:  ${bot.groups.size}")
            Text("您的bot已经成功登录。如果不出意外的话，再次从其他地方登录bot（例如您的项目）将不会再出现验证。")
            Text("现在您可以退出此软件，并回归到项目中了。")
            Text("当然，这并不是绝对的。如果你仍旧无法正常使用simbot项目，这也是常有的事情。请反馈至issue。")
        }


    }
}



