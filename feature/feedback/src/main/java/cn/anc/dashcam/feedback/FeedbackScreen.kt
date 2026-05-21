package cn.anc.dashcam.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun FeedbackScreen(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val feedbackManager = remember(context) { FeedbackManager(context) }
    var description by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf("") }
    var isCreating by remember { mutableStateOf(false) }

    FeedbackContent(
        description = description,
        resultText = resultText,
        isCreating = isCreating,
        onDescriptionChange = { description = it },
        onCreateFeedbackClick = {
            isCreating = true
            resultText = ""
            coroutineScope.launch {
                val result = withContext(Dispatchers.IO) {
                    feedbackManager.createFeedbackPackage(description)
                }
                resultText = result.fold(
                    onSuccess = { "反馈包已生成：${it.absolutePath}" },
                    onFailure = { "反馈包生成失败：${it.message.orEmpty()}" },
                )
                isCreating = false
            }
        },
        modifier = modifier,
    )
}

@Composable
internal fun FeedbackContent(
    description: String,
    resultText: String,
    isCreating: Boolean,
    onDescriptionChange: (String) -> Unit,
    onCreateFeedbackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "问题反馈",
                style = MaterialTheme.typography.headlineSmall,
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChange,
                modifier = Modifier.fillMaxWidth(),
                minLines = 4,
                label = { Text("问题描述") },
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                enabled = !isCreating,
                onClick = onCreateFeedbackClick,
            ) {
                Text(if (isCreating) "生成中" else "生成反馈包")
            }
            if (resultText.isNotBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = resultText,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Preview(name = "Feedback", showBackground = true)
@Composable
private fun FeedbackContentPreview() {
    FeedbackContent(
        description = "行车记录仪连接后偶现断开，请协助排查。",
        resultText = "反馈包已生成：/storage/emulated/0/Android/data/package/files/feedback.zip",
        isCreating = false,
        onDescriptionChange = {},
        onCreateFeedbackClick = {},
    )
}
