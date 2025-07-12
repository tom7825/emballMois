package fr.inventory.emballmois

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat.setDecorFitsSystemWindows
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import fr.inventory.emballmois.ui.screens.AreaSelectionScreen
import fr.inventory.emballmois.ui.screens.LoginScreen
import fr.inventory.emballmois.ui.screens.MainScreen
import fr.inventory.emballmois.ui.screens.StockRegistrationAddScreen
import fr.inventory.emballmois.ui.screens.StockRegistrationViewScreen
import fr.inventory.emballmois.ui.theme.EmballMoisTheme
import fr.inventory.emballmois.ui.utils.MessageManager
import fr.inventory.emballmois.ui.viewmodels.MainViewModel
import fr.inventory.emballmois.ui.viewmodels.StockRegistrationViewModel
import javax.inject.Inject

object AppRoutes {
    const val LOGIN = "login_route"
    const val MAIN = "main_route"
    const val AREA_CHOICE = "area_choice_route"
    const val STOCK_REGISTRATION = "stock_registration_route"
    const val STOCK_REGISTRATION_LIST_VIEW = "stock_registration_list_view_route"
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var messageManager: MessageManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDecorFitsSystemWindows(window, false)
        setContent {
            EmballMoisTheme(
                darkTheme = isSystemInDarkTheme(),
                dynamicColor = false
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val snackbarHostState = remember { SnackbarHostState() }
                    LaunchedEffect(key1 = Unit) {
                        messageManager.messageEvents.collect { message ->
                            snackbarHostState.showSnackbar(
                                message,
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                    Scaffold(
                        snackbarHost = { SnackbarHost(snackbarHostState) }
                    ) { paddingValues ->
                        AppNavigation(modifier = Modifier.padding(paddingValues))
                    }
                }
            }
        }
    }
}

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val stockRegistrationViewModel: StockRegistrationViewModel = hiltViewModel()
    val mainViewModel: MainViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = AppRoutes.MAIN
    ) {
        composable(route = AppRoutes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(AppRoutes.MAIN) {
                        popUpTo(AppRoutes.MAIN)
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(route = AppRoutes.MAIN) {
            MainScreen(
                onNavigateToLogin = {
                    navController.navigate(AppRoutes.LOGIN) {
                        popUpTo(AppRoutes.MAIN)
                        launchSingleTop = true
                    }
                },
                onNavigateToAreaChoiceScreen = {
                    navController.navigate(AppRoutes.AREA_CHOICE) {
                        launchSingleTop = true
                    }
                },
                mainViewModel = mainViewModel
            )
        }
        composable(route = AppRoutes.AREA_CHOICE) {
            Log.d("AppNavigation", "Navigating to Area choice screen")
            AreaSelectionScreen(
                onAreaSelected = { area ->
                    stockRegistrationViewModel.selectStorageArea(area)
                    navController.navigate(AppRoutes.STOCK_REGISTRATION) {
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(route = AppRoutes.STOCK_REGISTRATION) {
            StockRegistrationAddScreen(
                stockRegistrationViewModel = stockRegistrationViewModel,

                onViewStockRegistrationSelected = {
                    navController.navigate(AppRoutes.STOCK_REGISTRATION_LIST_VIEW) {
                        launchSingleTop = true
                    }
                },
                onReturnHome = {
                    navController.navigate(AppRoutes.MAIN) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(route = AppRoutes.STOCK_REGISTRATION_LIST_VIEW) {
            StockRegistrationViewScreen(
                stockRegistrationViewModel = stockRegistrationViewModel,
                onReturnHome = {
                    navController.navigate(AppRoutes.MAIN) {
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}