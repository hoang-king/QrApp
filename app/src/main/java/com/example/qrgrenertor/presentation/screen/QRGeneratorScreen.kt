package com.example.qrgrenertor.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

import com.example.qrgrenertor.presentation.components.HeaderCard
import com.example.qrgrenertor.presentation.components.InputSection
import com.example.qrgrenertor.presentation.components.QRCodeDisplay

import com.example.qrgrenertor.presentation.viewmodel.QRGeneratorViewModel

@Composable
fun QRGeneratorScreen(
//    : NavController,
    viewModel: QRGeneratorViewModel= viewModel(),


){
    val uiState by viewModel.uiState.collectAsState()
//    LaunchedEffect(uiState.qrBitmap) {
//        if (uiState.qrBitmap != null) {
//            navController.navigate("result")
//        }
//    }

    uiState.errorMessage?.let{ error ->
        AlertDialog(
            onDismissRequest = { viewModel.clearError()},
            title = { Text(" loi")},
            text = { Text("error") },
            confirmButton = {
                TextButton(onClick = {viewModel.clearError()}) {
                    Text("ok")
                }
            }
        )

    }
    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF6B73FF),
                        Color(0xFF9B59B6)
                    )
                )

            )
            .padding(24.dp)
    ){
        HeaderCard()

        Spacer(modifier = Modifier.height(32.dp))

        InputSection(
            inputText = uiState.inputText,
            isLoading = uiState.isLoading,
            onTextChange = viewModel::updateInputText,
            onClearText = viewModel::clearInputText,
            onGenerateQR = viewModel::generateQRCode
        )
        Spacer(modifier = Modifier.height(32.dp))

        uiState.qrBitmap?.let { bitmap ->
            QRCodeDisplay(bitmap = bitmap)
        }


    }



}