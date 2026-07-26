package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.navigation.FadeBuddyAppContent
import com.example.ui.theme.FadeBuddyTheme
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Auto-connect BLE simulator on launch so user gets live telemetry right away
        viewModel.bleSimulator.connect()

        setContent {
            FadeBuddyTheme {
                FadeBuddyAppContent(viewModel = viewModel)
            }
        }
    }
}
