
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.moodtracker.database.MoodReadingEntry
import com.moodtracker.viewmodels.DatabaseViewmodel
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.component.textComponent
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.entry.entryOf
import com.patrykandpatrick.vico.views.chart.line.lineChart

@Composable
fun StatisticsScreen(viewModel: DatabaseViewmodel = androidx.lifecycle.viewmodel.compose.viewModel()) {

    val finishedData: List<MoodReadingEntry> by viewModel.finishedReadings.observeAsState(emptyList())

    val context = LocalContext.current
    val filteredData = finishedData.filter{ it.morningMood != null}

    val entries = filteredData.mapIndexed { index, moodReading ->
        entryOf(x = index.toFloat(), y = moodReading.morningMood!!.toFloat())
    }

    val xLabels = filteredData.map { it.date }
    val labelComponent = textComponent()

    val entriesLINE = (0..9).map { i -> entryOf(i.toFloat(), i.toFloat()) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Morning Mood Over Time", modifier = Modifier.padding(bottom = 8.dp))

        if (entries.isNotEmpty()) {
            @Suppress("DEPRECATION")
            Chart(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 16.dp),
                chart = lineChart(context),
                model = entryModelOf(entries),
                startAxis = startAxis(
                ),
                bottomAxis = bottomAxis(
                    label = labelComponent
                )
            )
        } else {
            Text(text = "No morning mood data available")
        }

        Chart(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            chart = lineChart(context = context),
            model = entryModelOf(entriesLINE),
            startAxis = startAxis()
        )
    }
}