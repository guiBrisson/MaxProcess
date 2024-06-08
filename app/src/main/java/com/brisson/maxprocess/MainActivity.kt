package com.brisson.maxprocess

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import com.brisson.maxprocess.ui.navigation.AppNavHost
import com.brisson.maxprocess.ui.theme.MaxProcessTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaxProcessTheme {
                Surface {
                    AppNavHost()
                }
            }
        }
    }
}
