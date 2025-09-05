package com.example.qrgrenertor.presentation.screen



import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.qrgrenertor.presentation.components.QRCodeDisplay
import com.example.qrgrenertor.presentation.viewmodel.QRGeneratorViewModel

@Composable
fun QRCodeResultScreen(
    navController: NavController,
    viewModel: QRGeneratorViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        uiState.qrBitmap?.let { bitmap ->
            QRCodeDisplay(bitmap = bitmap)
        }?:Text("Không có mã QR để hiển thị")


        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = {
            viewModel.clearQRCode() // nếu muốn xóa QR khi quay lại
            navController.popBackStack()
        }) {
            Text("Quay lại")
        }
    }
}


