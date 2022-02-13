package love.forte.simbot.mlh

import io.github.bonigarcia.wdm.WebDriverManager
import net.lightbody.bmp.BrowserMobProxy
import net.lightbody.bmp.BrowserMobProxyServer
import net.lightbody.bmp.client.ClientUtil
import net.lightbody.bmp.proxy.CaptureType
import net.mamoe.mirai.BotFactory
import net.mamoe.mirai.utils.LoggerAdapters
import org.openqa.selenium.Proxy
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.edge.EdgeDriver
import org.openqa.selenium.edge.EdgeOptions
import org.openqa.selenium.remote.CapabilityType
import org.openqa.selenium.remote.DesiredCapabilities


/**
 * https://www.selenium.dev/zh-cn/documentation/webdriver/getting_started/open_browser/
 */



suspend fun main(args: Array<String>) {
    LoggerAdapters.useLog4j2()

    // TODO https://blog.csdn.net/weixin_43881394/article/details/109133509
    // start the proxy
    val proxy: BrowserMobProxy = BrowserMobProxyServer()
    proxy.start()


    // get the Selenium proxy object
    val seleniumProxy: Proxy = ClientUtil.createSeleniumProxy(proxy)

    // configure it as a desired capability
    val capabilities = DesiredCapabilities()
    capabilities.setCapability(CapabilityType.PROXY, seleniumProxy)

    // start the browser up
    // val driver: WebDriver = FirefoxDriver(capabilities)


    WebDriverManager.chromedriver().setup()
    val options = ChromeOptions().merge(capabilities)
    val driver = ChromeDriver(options)


    // enable more detailed HAR capture, if desired (see CaptureType for the complete list)
    // proxy.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT)
    proxy.enableHarCaptureTypes(CaptureType.getResponseCaptureTypes())

    // proxy.newHar("ssl.captcha.qq.com")


    proxy.addRequestFilter { request, contents, messageInfo ->
        println("request text: ${contents.textContents}")
        null
    }


    proxy.addResponseFilter { response, contents, messageInfo ->
        println("response text: ${contents.textContents}")
    }


    //
    // // create a new HAR with the label "yahoo.com"
    // proxy.newHar("yahoo.com")
    //
    // // open yahoo.com
    // driver["http://yahoo.com"]
    //
    // // get the HAR data
    // val har = proxy.har



    val solver = SeleniumLoginSolver(driver, proxy)
    val bot = BotFactory.newBot(1462974622, "123456789") {
        loginSolver = solver
        deviceInfo = { bot -> simbotMiraiDeviceInfo(bot.id, 1) }
    }


    // val bot = botManager.register(3521361891, "LiChengYang9983.") {
    //     loginSolver = solver
    //     deviceInfo = { bot -> simbotMiraiDeviceInfo(bot.id, 1) }
    // }
    bot.login()

}