package love.forte.simbot.mlh.window

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.FrameWindowScope
import love.forte.simbot.mlh.SetTitle

class NormalVerifyData {
    var url by mutableStateOf("") // by remember { mutableStateOf("") }
    var urlErr by mutableStateOf(false) // remember { mutableStateOf(false) }
}

@Preview
@Composable
fun FrameWindowScope.normalVerifyStep(setTitle: SetTitle, setStep: SetStep) {
    val data = remember { NormalVerifyData() }
    
    Box(
        modifier = Modifier.fillMaxSize().padding(20.dp).wrapContentHeight(Alignment.Top)
            .wrapContentWidth(Alignment.CenterHorizontally),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 5.dp),
        ) {
            
            urlInputInfo(data) {
                data.url = it.trim().filter(Char::isDigit)
            }
            
        }
    }
}


/**
 * 账号信息输入
 */
@Composable
fun urlInputInfo(data: NormalVerifyData, setUrl: (String) -> Unit,) {
    
    Box(
        modifier = Modifier
            .height(150.dp)
            .fillMaxSize()
            .wrapContentHeight(Alignment.Top)
            .wrapContentWidth(Alignment.CenterHorizontally)
    ) {
        Column {
            OutlinedTextField(
                value = data.url,
                isError = data.urlErr,
                onValueChange = { setUrl(it) },
                label = { Text("滑块验证链接") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "Account",
                    )
                },
                placeholder = { Text("http://xxxxxxx") },
            )
        }
    }
}