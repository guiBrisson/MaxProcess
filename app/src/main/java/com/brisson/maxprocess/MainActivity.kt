package com.brisson.maxprocess

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.brisson.maxprocess.ui.component.snackbar.BaseSnackbar
import com.brisson.maxprocess.ui.component.snackbar.SnackbarProperties
import com.brisson.maxprocess.ui.navigation.AppNavHost
import com.brisson.maxprocess.ui.theme.MaxProcessTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.WHITE, Color.BLACK),
            navigationBarStyle = SystemBarStyle.dark(Color.BLACK),
        )

        setContent {
            val snackbarHostState = remember { SnackbarHostState() }

            MaxProcessTheme {
                Scaffold(
                    modifier = Modifier,
                    snackbarHost = {
                        SnackbarHost(
                            hostState = snackbarHostState,
                            snackbar = { data ->
                                BaseSnackbar(
                                    modifier = Modifier.padding(16.dp),
                                    customProperties = data.visuals as SnackbarProperties,
                                    performAction = data::performAction,
                                )
                            }
                        )
                    },
                ) { paddingValues ->
                    Surface(modifier = Modifier.padding(paddingValues)) {
                        AppNavHost(
                            onShowSnackbar = { props ->
                                snackbarHostState.showSnackbar(props) == SnackbarResult.ActionPerformed
                            }
                        )
                    }
                }
            }
        }
    }
}
