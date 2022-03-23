package love.forte.simbot.mlh.window

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import love.forte.simbot.mlh.util.ClickableTextWithLinks
import love.forte.simbot.mlh.util.appendLink


/**
 * 说明信息. // 超链接
 */
@OptIn(ExperimentalTextApi::class)
@Composable
fun ColumnScope.tip() {
    Text(
        text = "说明",
        style = MaterialTheme.typography.h3,
        modifier = Modifier.align(Alignment.CenterHorizontally).padding(5.dp)
    )

    //region 1.selenium
    subTitle("1. Selenium")


    Row {
        ClickableTextWithLinks {
            withStyle(ParagraphStyle(
                textAlign = TextAlign.Left,
                textIndent = TextIndent(firstLine = 4.sp)
            )) {
                append("此工具进行浏览器访问是基于 ")

                appendLink("selenium", "https://www.selenium.dev/zh-cn/documentation/")

                append(" 实现的。鉴于个人技术水平问题以及 ")

                appendLink(
                    "selenium - 验证码",
                    "https://www.selenium.dev/zh-cn/documentation/test_practices/discouraged/captchas/"
                )

                append(" 中的提倡与描述，此工具将不会提供对滑动验证码的自动化验证。")
            }
        }
    }
    //endregion

    subTitle("2. WebDriver Manager")

    Row {
        ClickableTextWithLinks {
            withStyle(ParagraphStyle(
                textIndent = TextIndent(firstLine = 40.sp)
            )) {
                append("浏览器驱动的使用与安装，是通过")
                appendLink("WebDriver Manager", "https://github.com/bonigarcia/webdrivermanager")
                append("实现的。你可以在selenium的文档")
                appendLink("安装浏览器驱动", "https://www.selenium.dev/zh-cn/documentation/webdriver/getting_started/install_drivers/#1-%E9%A9%B1%E5%8A%A8%E7%AE%A1%E7%90%86%E8%BD%AF%E4%BB%B6")
                append("中找到其对 WebDriver Manager 的说明与介绍。")
            }
        }
    }

    subTitle("3. BrowserMob Proxy")

    Row {
        ClickableTextWithLinks {
            withStyle(ParagraphStyle(
                textIndent = TextIndent(firstLine = 40.sp)
            )) {
                append("对于滑动验证码来讲，此工具需要对所控制的浏览器进行请求捕获，因此需要使用到代理（Proxy）来监控指定请求。此工具所使用的进行代理的依赖为")
                appendLink("BrowserMob Proxy", "https://github.com/lightbody/browsermob-proxy#readme")
                append("。你可以在说明中的 ")
                appendLink("Using With Selenium", "https://github.com/lightbody/browsermob-proxy#using-with-selenium")
                append(" 处查看BrowserMob Proxy对Selenium的支持。\n\n")
                append("同样的，下文中的 ")
                appendLink("SSL Support", "https://github.com/lightbody/browsermob-proxy#ssl-support")
                append(" 描述了对于SSH的支持以及 ")
                appendLink("ca-certificate-rsa.cer", "https://github.com/lightbody/browsermob-proxy/blob/master/browsermob-core/src/main/resources/sslSupport/ca-certificate-rsa.cer")
                append(" 文件的说明。\n\n")
                append("因为滑动验证等相关链接是https的，因此如果想要使用代理，则需要安装此证书并将其设为信任证书，且建议在不需要使用此工具后移除此证书。")
                append("未来也许会支持使用者生成自定义证书来提供更安全的使用。")
            }
        }

    }

}

@Composable
private fun subTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.h5,
        modifier = Modifier.subTitlePadding()
    )
}

private fun Modifier.subTitlePadding(): Modifier = padding(top = 30.dp, bottom = 15.dp)