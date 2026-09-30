package cards.sleevd.morphingfab.sample.demos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import cards.sleevd.morphingfab.MorphingFab
import cards.sleevd.morphingfab.MorphingFabPlacement
import cards.sleevd.morphingfab.MorphingFabStyle
import cards.sleevd.morphingfab.sample.DemoScaffold
import cards.sleevd.morphingfab.sample.LocalMorphSpec
import cards.sleevd.morphingfab.sample.SampleIcons

/**
 * The slot overload, for a dialog body the convenience overload's shape won't hold.
 *
 * `dialogContent` is a `ColumnScope`, so `Spacer(Modifier.weight(1f))` pins the action row to the
 * bottom whatever the list above it does. The dialog is taller than the default here, set through
 * `MorphingFabStyle.dialogSize` — the content is laid out at that final size from the first frame
 * it's visible, so the text never re-wraps mid-morph.
 */
@Composable
internal fun CustomContentDemo(onBack: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(Grade.Near) }
    var applied by remember { mutableStateOf<Grade?>(null) }

    Box(Modifier.fillMaxSize()) {
        DemoScaffold(
            title = "Custom dialog body",
            explanation =
                "dialogContent is a slot, so the dialog can hold anything a Column can. This one " +
                    "has a selectable list and an action row held to the bottom with a weighted " +
                    "Spacer.",
            onBack = onBack,
        ) {
            Text(
                text = applied?.let { "Graded ${it.label}" } ?: "Ungraded",
                style = MaterialTheme.typography.displaySmall,
            )
        }

        MorphingFab(
            expanded = expanded,
            onFabClick = { expanded = true },
            onDismissRequest = { expanded = false },
            placement = MorphingFabPlacement.BottomEnd(),
            style = MorphingFabStyle(dialogSize = DpSize(320.dp, 400.dp)),
            animationSpec = LocalMorphSpec.current,
            fabContent = {
                Icon(
                    imageVector = SampleIcons.Star,
                    contentDescription = "Grade this card",
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            },
        ) {
            Icon(
                imageVector = SampleIcons.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )

            Text(
                text = "Condition",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
            )

            Grade.entries.forEach { grade ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color =
                        if (grade == selected) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceContainerLow,
                    modifier =
                        Modifier.fillMaxWidth()
                            .selectable(
                                selected = grade == selected,
                                onClick = { selected = grade },
                            ),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(selected = grade == selected, onClick = { selected = grade })
                        Text(grade.label, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(onClick = { expanded = false }) { Text("Cancel") }
                Spacer(Modifier.padding(horizontal = 4.dp))
                Button(
                    onClick = {
                        applied = selected
                        expanded = false
                    }
                ) {
                    Text("Apply")
                }
            }
        }
    }
}

private enum class Grade(val label: String) {
    Mint("Mint"),
    Near("Near mint"),
    Played("Lightly played"),
}
