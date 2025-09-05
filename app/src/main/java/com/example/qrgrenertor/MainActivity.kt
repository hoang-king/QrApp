package com.example.qrgrenertor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import androidx.navigation.compose.rememberNavController

import com.example.qrgrenertor.presentation.screen.QRCodeResultScreen
import com.example.qrgrenertor.presentation.screen.QRGeneratorScreen

import com.example.qrgrenertor.ui.theme.QrgrenertorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            QrgrenertorTheme {
                QRGeneratorScreen()
//                val navController = rememberNavController()
//                NavHost(navController = navController, startDestination = "main") {
//                    composable("main") {
//                        QRGeneratorScreen(navController)
//                    }
//                    composable("result") {
//                        QRCodeResultScreen(navController)
//                    }
//                }

            }
        }
    }
}

