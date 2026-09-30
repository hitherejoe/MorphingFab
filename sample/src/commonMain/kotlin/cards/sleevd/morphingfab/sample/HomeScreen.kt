package cards.sleevd.morphingfab.sample

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeScreen(
    reducedMotion: Boolean,
    onReducedMotionChange: (Boolean) -> Unit,
    onOpen: (Demo) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MorphingFab") },
                actions = {
                    Text("Reduce motion", style = MaterialTheme.typography.labelLarge)
                    Spacer(Modifier.width(8.dp))
                    Switch(checked = reducedMotion, onCheckedChange = onReducedMotionChange)
                    Spacer(Modifier.width(8.dp))
                },
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Text(
                    text =
                        "A floating action button and an alert dialog are the same Surface, with " +
                            "one progress value driving position, size, corner radius, colour " +
                            "and content alpha.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 4.dp),
                )
            }

            items(Demo.entries) { demo ->
                Card(
                    onClick = { onOpen(demo) },
                    colors =
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                        ),
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = demo.icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )

                        Spacer(Modifier.width(16.dp))

                        Column(Modifier.weight(1f)) {
                            Text(demo.title, style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = demo.summary,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }

                        Icon(
                            imageVector = SampleIcons.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outlineVariant,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            }
        }
    }
}

/** Shared chrome for a demo screen: a back-navigable top bar over an explanatory paragraph. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DemoScaffold(
    title: String,
    explanation: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    bottomBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    Icon(
                        imageVector = SampleIcons.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.padding(horizontal = 12.dp).clickable(onClick = onBack),
                    )
                },
            )
        },
        bottomBar = bottomBar,
        floatingActionButton = floatingActionButton,
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = explanation,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            content()
        }
    }
}
