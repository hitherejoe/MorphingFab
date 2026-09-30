package cards.sleevd.morphingfab.sample.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cards.sleevd.morphingfab.sample.SampleApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Edge to edge on purpose: MorphingFab's scrim is drawn in a window-level overlay, so it
        // should dim the status and navigation bars too. Letterboxing it would hide that.
        enableEdgeToEdge()
        setContent { SampleApp() }
    }
}
