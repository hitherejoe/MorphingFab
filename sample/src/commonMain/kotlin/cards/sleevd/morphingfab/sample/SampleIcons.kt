package cards.sleevd.morphingfab.sample

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

/**
 * The handful of Material icons the sample needs, built from their path data.
 *
 * Declared here rather than pulled from an icon pack to make the point that `:morphingfab` ships no
 * icon dependency and has no opinion about what goes in a FAB — `fabContent` is a slot, and this is
 * a sample filling it.
 */
internal object SampleIcons {
    val Add = icon("Add", "M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z")

    val ArrowBack =
        icon("ArrowBack", "M20 11H7.83l5.59-5.59L12 4l-8 8 8 8 1.41-1.41L7.83 13H20v-2z")

    val Bolt =
        icon(
            "Bolt",
            "M11 21h-1l1-7H7.5c-.58 0-.57-.32-.38-.66.19-.34.05-.08.07-.12C8.48 10.94 10.42 " +
                "7.54 13 3h1l-1 7h3.5c.49 0 .56.33.47.51l-.07.15C12.96 17.55 11 21 11 21z",
        )

    val ChevronRight = icon("ChevronRight", "M10 6L8.59 7.41 13.17 12l-4.58 4.59L10 18l6-6z")

    val Close =
        icon(
            "Close",
            "M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 " +
                "19 19 17.59 13.41 12z",
        )

    val Delete =
        icon(
            "Delete",
            "M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z",
        )

    val Home = icon("Home", "M10 20v-6h4v6h5v-8h3L12 3 2 12h3v8z")

    val Lock =
        icon(
            "Lock",
            "M18 8h-1V6c0-2.76-2.24-5-5-5S7 3.24 7 6v2H6c-1.1 0-2 .9-2 2v10c0 1.1.9 2 2 " +
                "2h12c1.1 0 2-.9 2-2V10c0-1.1-.9-2-2-2zm-6 9c-1.1 0-2-.9-2-2s.9-2 2-2 2 .9 2 " +
                "2-.9 2-2 2zm3.1-9H8.9V6c0-1.71 1.39-3.1 3.1-3.1 1.71 0 3.1 1.39 3.1 3.1v2z",
        )

    val Person =
        icon(
            "Person",
            "M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 " +
                "4v2h16v-2c0-2.66-5.33-4-8-4z",
        )

    val Search =
        icon(
            "Search",
            "M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 " +
                "9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm" +
                "-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z",
        )

    val Star =
        icon(
            "Star",
            "M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 " +
                "4.73L5.82 21z",
        )

    val Tune =
        icon(
            "Tune",
            "M3 17v2h6v-2H3zM3 5v2h10V5H3zm10 16v-2h8v-2h-8v-2h-2v6h2zM7 9v2H3v2h4v2h2V9H7zm14 " +
                "4v-2H11v2h10zm-6-4h2V7h4V5h-4V3h-2v6z",
        )
}

private fun icon(name: String, pathData: String): ImageVector =
    ImageVector.Builder(
            name = name,
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        )
        .addPath(pathData = addPathNodes(pathData), fill = SolidColor(Color.Black))
        .build()
