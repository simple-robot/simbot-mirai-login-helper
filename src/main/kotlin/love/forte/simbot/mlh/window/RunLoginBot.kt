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
 * ???????????????, ?????????????????????.
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
        title = "Bot????????????",
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
                        // ?????????????????????
                        prepareBrowserDriver(prepareBrowserDriverState)

                        // ????????????
                        if (prepareBrowserDriverState.onSolvePicCaptcha != null) {
                            onSolvePicCaptcha(prepareBrowserDriverState)
                        }

                        // ??????????????????
                        if (prepareBrowserDriverState.onSolveUnsafeDeviceLoginVerify != null) {
                            onSolveUnsafeDeviceLoginVerify(prepareBrowserDriverState)
                        }

                        if (state.solver != null) {
                            // ????????????
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


//region ?????????????????????
class PrepareBrowserDriverState(val runState: RunLoginBotState) {
    var step by mutableStateOf("")

    // ???????????????.
    var onSolvePicCaptcha: Pair<ByteArray, CancellableContinuation<String?>>? by mutableStateOf(null)

    // ???????????????.
    var onSolveUnsafeDeviceLoginVerify: Pair<String, CancellableContinuation<String?>>? by mutableStateOf(null)
}

/**
 * ??????????????????????????????
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun prepareBrowserDriver(state: PrepareBrowserDriverState) {

    Row {
        Text(text = "1: ?????????????????????... ")
        AnimatedContent(targetState = state.step) { step ->
            Text(
                text = step.takeIf { it.isNotEmpty() } ?: ""
            )
        }
    }

    if (state.runState.solver == null) {
        LaunchedEffect(Unit) {
            try {
                // ????????????
                val solver = doPrepareBrowserDriver(state)
                state.step = "selenium login solver????????????"
                state.runState.solver = solver
            } catch (e: Throwable) {
                state.runState.logger.error("???????????????????????????", e)
                state.step = "??????????????????????????????\n\n\n" + e.stackTraceToString()
            }
        }
    }


}

private suspend fun doPrepareBrowserDriver(state: PrepareBrowserDriverState): SeleniumLoginSolver =
    runWithInterruptible(Dispatchers.IO) {
        // start the proxy
        state.step = "??????proxy..."
        val proxy: BrowserMobProxy = state.runState.proxy ?: run {
            state.step = "??????proxy...??????proxy..."
            BrowserMobProxyServer().also {
                val old = state.runState.proxy
                state.runState.proxy = it
                old?.abort()
                state.step = "??????proxy...??????proxy..."
                it.start()
            }
        }

        // get the Selenium proxy object
        state.step = "??????selenium proxy..."
        val seleniumProxy: Proxy = ClientUtil.createSeleniumProxy(proxy)

        // configure it as a desired capability
        val capabilities = DesiredCapabilities()
        capabilities.setCapability(CapabilityType.PROXY, seleniumProxy)

        state.step = "?????? ${state.runState.driverType.name.lowercase()} driver manager..."
        val driverManager = state.runState.driverType.driverManager()
        driverManager.config().also { config ->
            config.cachePath = ".cache"
        }
        // setup
        state.step = "?????? ${state.runState.driverType.name.lowercase()} driver manager...\n" +
                "tips: ???????????????????????????????????????????????????????????????"
        driverManager.setup()

        // enable more detailed HAR capture, if desired (see CaptureType for the complete list)
        // proxy.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT)
        proxy.enableHarCaptureTypes(CaptureType.getResponseCaptureTypes())

        state.step = "??????selenium login solver..."
        SeleniumLoginSolver(proxy,
            driverGetter = state.runState.driverType.driverFactory(capabilities),
            onSolvePicCaptcha = { _, data ->
                state.step = "?????????????????????..."
                suspendCancellableCoroutine { continuation ->
                    state.onSolvePicCaptcha = data to continuation
                }
            },
            onSolveUnsafeDeviceLoginVerify = { _, url ->
                state.step = "??????????????????..."
                suspendCancellableCoroutine { continuation ->
                    state.onSolveUnsafeDeviceLoginVerify = url to continuation
                }
            }
        )
    }
//endregion


//region ??????BOT
class DoLoginState(val runState: RunLoginBotState) {
    var step: AnnotatedString by mutableStateOf(AnnotatedString(""))
}

/**
 * ????????????
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun doLogin(state: DoLoginState) {

    Text("2: ????????????... ")
    AnimatedContent(state.step) { step ->
        Text(
            text = step
        )
    }

    // DoLogin
    LaunchedEffect(Unit) {
        val solver = state.runState.solver!!
        try {
            state.step = AnnotatedString("????????????BOT...")
            val bot = BotFactory.newBot(state.runState.code.toLong(), state.runState.pass) {
                loginSolver = solver
                deviceInfo = { bot -> simbotMiraiDeviceInfo(bot.id, 1) }
                parentCoroutineContext = currentCoroutineContext()
            }.apply {
                state.step = AnnotatedString("????????????BOT...")
                login()
                state.step = AnnotatedString("????????????")
            }
            state.runState.bot = bot
        } catch (e: Throwable) {
            state.runState.logger.error("????????????", e)
            state.step = buildAnnotatedString {
                append("????????????! : ")
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
        solver.close() // ????????????
    }

}
//endregion


/**
 * ???????????????
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
        title = "?????????????????????",
        icon = Logo.painter,
    ) {
        Box {
            Box(
                Modifier.verticalScroll(state = verColl, flingBehavior = flingBehavior())
            ) {
                Column {
                    Image(bitmap = loadImageBitmap(data.inputStream()), contentDescription = "???????????????")
                    TextField(
                        value = textValue,
                        onValueChange = { textValue = it.trim() },
                        placeholder = { Text("???????????????????????????") },
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
                            Text("??????")
                        }

                        OutlinedButton(
                            onClick = { doClose() }
                        ) {
                            Text("??????")
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
 * ????????????
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
        title = "??????????????????",
        icon = Logo.painter,
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(40.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row {
                Text("????????? ")
                Text(text = "??????QQ", color = Color.Red, fontWeight = FontWeight.Bold)
                Text(" ????????????????????????")
            }

            if (qrImgData == null) {
                LaunchedEffect(Unit) {
                    qrImgData = createQrCodeImg(url)
                }
            }

            // ?????????
            qrImgData?.inputStream()?.use { inp ->
                Image(bitmap = loadImageBitmap(inp), contentDescription = "??????")
            }


            // ??????
            Row {
                Text("????????????????????? ")
                Text(text = "??????QQ", color = Color.Red, fontWeight = FontWeight.Bold)
                Text(" ??????????????????: ")
            }
            Text(url)


            Button(
                onClick = { doClose() }
            ) {
                Text("????????????")
            }

        }

    }
}


private suspend fun createQrCodeImg(url: String) = runWithInterruptible(Dispatchers.IO) {
    // ???????????????????????????
    val hints: MutableMap<EncodeHintType, Any?> = mutableMapOf(
        // ?????????????????????utf-8
        EncodeHintType.CHARACTER_SET to "utf-8",
        // ??????????????????L/M/Q/H,?????????????????????????????????????????????????????????????????????H
        EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.M,
        // ??????????????????0-10?????????????????????0 1(2) 3(4 5 6) 7(8 9 10)
        EncodeHintType.MARGIN to 0,
    )

    // ?????????????????????
    val writer = QRCodeWriter()
    val bitMatrix = writer.encode(url, BarcodeFormat.QR_CODE, 400, 400, hints)

    // ???????????????????????????
    val os = ByteArrayOutputStream()
    MatrixToImageWriter.writeToStream(bitMatrix, "png", os)
    bitMatrix.clear()
    os.toByteArray()
}


/**
 * ??????Bot
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
                    contentDescription = "??????",
                )
            } else {
                Image(
                    painter = ColorPainter(Color.Gray),
                    contentDescription = "??????",
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
            Text("??????bot???????????????????????????????????????????????????????????????????????????bot???????????????????????????????????????????????????")
            Text("????????????????????????????????????????????????????????????")
            Text("??????????????????????????????????????????????????????????????????simbot????????????????????????????????????????????????issue???")
            Text("???????????????bot?????????????????????????????????????????? `deviceInfo.json` ????????????")


        }


    }
}



