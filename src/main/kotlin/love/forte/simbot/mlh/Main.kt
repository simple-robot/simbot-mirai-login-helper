package love.forte.simbot.mlh

import io.github.bonigarcia.wdm.WebDriverManager
import net.lightbody.bmp.BrowserMobProxy
import net.lightbody.bmp.BrowserMobProxyServer
import net.lightbody.bmp.client.ClientUtil
import net.lightbody.bmp.proxy.CaptureType
import net.mamoe.mirai.utils.LoggerAdapters
import org.openqa.selenium.Proxy
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.CapabilityType
import org.openqa.selenium.remote.DesiredCapabilities


suspend fun main(args: Array<String>) {
    LoggerAdapters.useLog4j2()

    // https://www.selenium.dev/zh-cn/documentation/webdriver/getting_started/open_browser/

    // start the proxy
    val proxy: BrowserMobProxy = BrowserMobProxyServer()
    proxy.start()

    // get the Selenium proxy object
    val seleniumProxy: Proxy = ClientUtil.createSeleniumProxy(proxy)

    // configure it as a desired capability
    val capabilities = DesiredCapabilities()
    capabilities.setCapability(CapabilityType.PROXY, seleniumProxy)


    val driverManager = WebDriverManager.chromedriver()
    driverManager.setup()

    val options = ChromeOptions().merge(capabilities)

    // enable more detailed HAR capture, if desired (see CaptureType for the complete list)
    // proxy.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT)
    proxy.enableHarCaptureTypes(CaptureType.getResponseCaptureTypes())

    // val solver = SeleniumLoginSolver(proxy) {
    //     ChromeDriver(options)
    // }
    //
    // val bot = BotFactory.newBot(1462974621, "123456789") {
    //     loginSolver = solver
    //     deviceInfo = { bot -> simbotMiraiDeviceInfo(bot.id, 1) }
    // }


    // val bot = botManager.register(3521361891, "LiChengYang9983.") {
    //     loginSolver = solver
    //     deviceInfo = { bot -> simbotMiraiDeviceInfo(bot.id, 1) }
    // }
    // bot.login()

}