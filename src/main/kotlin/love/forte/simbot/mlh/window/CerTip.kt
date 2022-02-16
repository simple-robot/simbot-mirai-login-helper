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
    Text(
        text = "1. selenium",
        style = MaterialTheme.typography.h5,
        modifier = Modifier.align(Alignment.CenterHorizontally).padding(5.dp)
    )


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

                append(" 中的提倡与描述，此工具将不会提供对滑动验证码的自动化验证.")
            }
        }
    }
    //endregion

    Text(
        text = "2. webdrivermanager",
        style = MaterialTheme.typography.h5,
        modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 30.dp, bottom = 15.dp)
    )

    Row {
        ClickableTextWithLinks {
            withStyle(ParagraphStyle(
                textIndent = TextIndent(firstLine = 40.sp)
            )) {
                append("ABC")

            }
        }
    }

}


