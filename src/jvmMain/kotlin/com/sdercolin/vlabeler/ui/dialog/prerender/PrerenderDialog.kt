package com.sdercolin.vlabeler.ui.dialog.prerender

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.sdercolin.vlabeler.model.AppConf
import com.sdercolin.vlabeler.model.Project
import com.sdercolin.vlabeler.ui.common.plainClickable
import com.sdercolin.vlabeler.ui.editor.ChartStore
import com.sdercolin.vlabeler.ui.string.Strings
import com.sdercolin.vlabeler.ui.string.string
import com.sdercolin.vlabeler.ui.theme.Black50

@Composable
fun PrerenderDialog(
    project: Project,
    appConf: AppConf,
    chartStore: ChartStore,
    onError: (Throwable) -> Unit,
    finish: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    var progress by remember { mutableStateOf(ChartPrerenderer.Progress(0, 0, 0, 0)) }
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    LaunchedEffect(Unit) {
        ChartPrerenderer(
            scope,
            chartStore,
            onError,
            onProgress = { progress = it },
            onComplete = {
                progress = progress.copy(finishedFiles = progress.totalFiles)
            },
        ).render(
            project = project,
            appConf = appConf,
            density = density,
            layoutDirection = layoutDirection,
        )
    }

    Box(
        modifier = Modifier.fillMaxSize()
            .background(color = Black50)
            .plainClickable(),
        contentAlignment = Alignment.Center,
    ) {
        Surface(modifier = Modifier.fillMaxWidth(0.5f)) {
            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 30.dp, horizontal = 40.dp)) {
                Spacer(Modifier.height(20.dp))
                Content(progress)
                Spacer(Modifier.height(30.dp))
                ButtonBar(isCompleted = progress.finished, finish = finish)
            }
        }
    }
}

@Composable
private fun Content(progress: ChartPrerenderer.Progress) {
    val sampleProgressText = if (progress.finished) {
        string(Strings.PrerendererSampleTextFinished, progress.finishedFiles, progress.totalFiles)
    } else {
        string(Strings.PrerendererSampleText, progress.finishedFiles, progress.totalFiles)
    }
    val chartProgressText = if (progress.finishedInFile) {
        string(Strings.PrerendererChartTextFinished, progress.finishedCharts, progress.totalCharts)
    } else {
        string(Strings.PrerendererChartText, progress.finishedCharts, progress.totalCharts)
    }
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(text = sampleProgressText, maxLines = 1, style = MaterialTheme.typography.body2)
        LinearProgressIndicator(
            progress = progress.finishedFiles.toFloat() / progress.totalFiles,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = chartProgressText, maxLines = 1, style = MaterialTheme.typography.body2)
        LinearProgressIndicator(
            progress = progress.finishedCharts.toFloat() / progress.totalCharts,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun ButtonBar(isCompleted: Boolean, finish: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        TextButton(
            onClick = { finish() },
        ) {
            Text(string(Strings.CommonCancel))
        }
        Spacer(Modifier.width(25.dp))
        Button(
            enabled = isCompleted,
            onClick = { finish() },
        ) {
            Text(string(Strings.CommonOkay))
        }
    }
}
