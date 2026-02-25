package com.example.qrgrenertor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.example.qrgrenertor.presentation.ui.QRGeneratorNavigation
import com.example.qrgrenertor.presentation.ui.theme.QRAppColors
import com.example.qrgrenertor.presentation.ui.theme.QRAppTheme
import com.example.qrgrenertor.presentation.viewmodel.QRGeneratorViewModel
import dagger.hilt.android.AndroidEntryPoint

import android.widget.Toast
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: QRGeneratorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            
            // Lắng nghe các thông báo Toast từ ViewModel
            LaunchedEffect(viewModel.toastMessage) {
                viewModel.toastMessage.collectLatest { message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            }

            QRAppTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(QRAppColors.DarkBackground),
                    color = QRAppColors.DarkBackground
                ) {
                    val uiState = viewModel.uiState.collectAsState()
                    QRGeneratorNavigation(
                        uiState = uiState.value,
                        onEvent = { event ->
                            viewModel.onEvent(event)
                        }
                    )
                }
            }
        }
    }
}
