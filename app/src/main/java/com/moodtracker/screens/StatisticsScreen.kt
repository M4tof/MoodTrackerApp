
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.moodtracker.R
import com.moodtracker.database.MoodReadingEntry
import com.moodtracker.viewmodels.DatabaseViewmodel
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.component.lineComponent
import com.patrykandpatrick.vico.compose.component.textComponent
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.entry.entryOf
import com.patrykandpatrick.vico.views.chart.line.lineChart


@Composable
fun rememberCustomStartAxis() = startAxis(
    label = null,
    tick = null,
    guideline = lineComponent(
            color = Color.LightGray,
            thickness = 1.dp
        ),
    maxLabelCount = 6
)

@Composable
fun YAxisIcons() {
    val iconRows = listOf(
        listOf(R.drawable.happy, R.drawable.excited),  // Top row
        listOf(R.drawable.pleased),
        listOf(R.drawable.neutral),
        listOf(R.drawable.upset),
        listOf(R.drawable.angry, R.drawable.sad),     // Bottom row
    )

    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxHeight()
            .padding(end = 8.dp)
    ) {
        iconRows.forEach { icons ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                icons.forEach { iconRes ->
                    Image(
                        painter = painterResource(id = iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun StatisticsScreen(viewModel: DatabaseViewmodel = androidx.lifecycle.viewmodel.compose.viewModel()) {

    val finishedData: List<MoodReadingEntry> by viewModel.finishedReadings.observeAsState(emptyList())

    val context = LocalContext.current
    val filteredMorningData = finishedData.filter{ it.morningMood != null}
    val filteredEveningData = finishedData.filter{ it.eveningMood != null}

    val entries = filteredMorningData.mapIndexed { index, moodReading ->
        entryOf(x = index.toFloat(), y = moodReading.morningMood!!.toFloat())
    }

    val eveEntries = filteredEveningData.mapIndexed { index, moodReading ->
        entryOf(x = index.toFloat(), y = moodReading.eveningMood!!.toFloat())
    }

    val dateRangeText = if (finishedData.isNotEmpty()) {
        val sortedDates = finishedData.mapNotNull { it.date }.sorted()
        val startDate = sortedDates.firstOrNull()
        val endDate = sortedDates.lastOrNull()
        if (startDate != null && endDate != null) {
            "Readings between $startDate – $endDate"
        } else {
            ""
        }
    } else {
        ""
    }


    val labelComponent = textComponent()
    val customStartAxis = rememberCustomStartAxis()
    val scrollState = rememberScrollState()
    val modelProducer = ChartEntryModelProducer(listOf(entries, eveEntries))

    Column(modifier = Modifier.padding(16.dp)
        .verticalScroll(scrollState)) {

        if (dateRangeText.isNotEmpty()) {
            Text(
                text = dateRangeText,
                modifier = Modifier.padding(top = 4.dp),
                color = Color.Gray
            )
        }

        Text(text = "Morning Mood Over Time", modifier = Modifier.padding(bottom = 8.dp))

        if (entries.isNotEmpty()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp) // Fixed height for alignment
            ) {
                YAxisIcons()
                Chart(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp),
                    chart = lineChart(context),
                    model = entryModelOf(entries),
                    startAxis = customStartAxis,
                    bottomAxis = bottomAxis(label = labelComponent)
                )
            }
        } else {
            Text(text = "No morning mood data available")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(text = "Evening Mood Over Time", modifier = Modifier.padding(bottom = 8.dp))

        if (eveEntries.isNotEmpty()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                YAxisIcons()
                Chart(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp),
                    chart = lineChart(context),
                    model = entryModelOf(eveEntries),
                    startAxis = customStartAxis,
                    bottomAxis = bottomAxis(label = labelComponent)
                )
            }
        } else {
            Text(text = "No evening mood data available")
        }

        val modelC = modelProducer.getModel();
        Spacer(modifier = Modifier.height(32.dp))

        if (modelC?.entries!!.any { it.isNotEmpty() }) {
            Text(
                text = "Mood Range (Morning ↔ Evening)",
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                YAxisIcons()
                Chart(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 16.dp),
                    chart = lineChart(
                        context = context
                    ),
                    model = modelC,
                    startAxis = customStartAxis,
                    bottomAxis = bottomAxis(label = labelComponent)
                )
            }
        }

    }
}
