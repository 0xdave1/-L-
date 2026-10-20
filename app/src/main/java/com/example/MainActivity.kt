package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainAppScaffold
import com.example.ui.theme.SmartGroceryListTheme
import com.example.ui.viewmodel.GroceryViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartGroceryListTheme {
                val viewModel: GroceryViewModel = viewModel()
                MainAppScaffold(viewModel = viewModel)
            }
        }
    }
}
