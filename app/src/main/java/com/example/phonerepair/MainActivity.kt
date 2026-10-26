package com.example.phonerepair

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.phonerepair.ui.RepairTicketScreen
import com.example.phonerepair.ui.theme.PhoneRepairTheme
import com.example.phonerepair.viewmodel.RepairTicketViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PhoneRepairTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding()
                ) {
                    val viewModel: RepairTicketViewModel = viewModel()
                    RepairTicketScreen(viewModel = viewModel)
                }
            }
        }
    }
}
