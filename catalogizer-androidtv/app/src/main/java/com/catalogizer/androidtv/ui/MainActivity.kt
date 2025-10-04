package com.catalogizer.androidtv.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Surface
import com.catalogizer.androidtv.ui.navigation.TVNavigation
import com.catalogizer.androidtv.ui.theme.CatalogizerTVTheme
import com.catalogizer.androidtv.ui.viewmodel.AuthViewModel
import com.catalogizer.androidtv.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CatalogizerTVTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    CatalogizerTVApp(
                        authViewModel = authViewModel,
                        mainViewModel = mainViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun CatalogizerTVApp(
    authViewModel: AuthViewModel,
    mainViewModel: MainViewModel
) {
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val isLoading by mainViewModel.isLoading.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        mainViewModel.initializeApp()
    }

    if (!isLoading) {
        TVNavigation(
            isAuthenticated = authState.isAuthenticated,
            authViewModel = authViewModel
        )
    }
}