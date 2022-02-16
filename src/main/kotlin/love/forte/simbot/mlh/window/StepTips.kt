package love.forte.simbot.mlh.window

import androidx.compose.foundation.layout.Box
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import love.forte.simbot.mlh.SetTitle


@Composable
fun theTips(setTitle: SetTitle, setStep: SetStep) {
    Box(
        modifier = Modifier
    ) {


        OutlinedButton(
            onClick = { setStep(null) }
        ) {
            Text("返回")
        }
    }
}