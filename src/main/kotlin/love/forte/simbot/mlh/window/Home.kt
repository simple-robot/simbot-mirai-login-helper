@file:Suppress("FunctionName")

package love.forte.simbot.mlh.window

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

val CenterModifier = Modifier
    .fillMaxSize()
    .padding(20.dp)
    .wrapContentHeight(Alignment.Top)
    .wrapContentWidth(Alignment.CenterHorizontally)

/**
 * 进入的首页。
 */
@Preview
@Composable
fun Home(setTitle: SetTitle, resetStep: SetStep) {
    Box(
        modifier = CenterModifier // Modifier.fillMaxSize()
    ) {
        Column {
            Step.values().forEach { step ->
                StepButton(setTitle, step, resetStep)
            }
        }
    }
}

@Composable
private fun StepButton(setTitle: SetTitle, step: Step, resetStep: SetStep) {
    Button(
        onClick = {
            setTitle("${step.ordinal + 1}: ${step.display}")
            resetStep(step)
        }
    ) {
        Text("${step.ordinal + 1}: ${step.display}")
    }
}