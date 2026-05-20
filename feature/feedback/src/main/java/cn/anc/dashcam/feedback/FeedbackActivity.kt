package cn.anc.dashcam.feedback

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.unit.dp
import cn.anc.dashcam.core.logging.AppLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FeedbackActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppLog.event("feedback_open")

        setContent {
            FeedbackScreen()
        }
    }
}

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
                onValueChange = { description = it },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4,
                label = { Text("问题描述") },
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                enabled = !isCreating,
                onClick = {
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
