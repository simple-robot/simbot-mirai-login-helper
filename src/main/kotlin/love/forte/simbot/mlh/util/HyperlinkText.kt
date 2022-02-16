package love.forte.simbot.mlh.util

import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import love.forte.simbot.mlh.window.desktop
import java.awt.Desktop
import java.net.URI

const val LINK_ANNOTATED_TAG = "url"

@OptIn(ExperimentalTextApi::class)
fun AnnotatedString.Builder.appendLink(content: String, url: String) {
    withStyle(
        SpanStyle(
            color = Color.Blue.copy(alpha = 0.8F),
            textDecoration = TextDecoration.Underline,
        )
    ) {
        withAnnotation(LINK_ANNOTATED_TAG, url) {
            append(content)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Suppress("FunctionName")
@Composable
fun ClickableTextWithLinks(
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    softWrap: Boolean = true,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    builder: (AnnotatedString.Builder).() -> Unit
) {
    var showLink: String? by remember { mutableStateOf(null) }
    val annotatedText = buildAnnotatedString(builder)
    ClickableText(
        text = annotatedText,
        modifier = modifier,
        style = style,
        softWrap = softWrap,
        overflow = overflow,
        maxLines = maxLines,
        onTextLayout = onTextLayout,
    ) {

        val annotations = annotatedText.getStringAnnotations(LINK_ANNOTATED_TAG, it, it)
        val link = annotations.firstOrNull()?.item ?: return@ClickableText

        desktop(Desktop.Action.BROWSE) { desktop ->
            desktop.browse(URI(link))
        }.orDo {
            showLink = link
        }
    }

    if (showLink != null) {
        AlertDialog(
            onDismissRequest = { showLink = null },
            title = { Text("超链接") },
            buttons = {
                Button(
                    onClick = { showLink = null }
                ) {
                    Text("确认")
                }
            },
            text = {
                SelectionContainer {
                    Text(text = showLink ?: "", color = Color.Blue, textDecoration = TextDecoration.Underline)
                }
            }
        )
    }


}