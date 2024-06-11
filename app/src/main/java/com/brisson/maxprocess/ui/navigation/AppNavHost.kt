package com.brisson.maxprocess.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.brisson.maxprocess.ui.component.snackbar.SnackbarProperties
import com.brisson.maxprocess.ui.screen.detail.ClientDetailRoute
import com.brisson.maxprocess.ui.screen.list.ClientListRoute

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    onShowSnackbar: suspend (SnackbarProperties) -> Boolean,
    navController: NavHostController = rememberNavController(),
    startDestination: String = NavUtils.CLIENT_LIST_ROUTE,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(route = NavUtils.CLIENT_LIST_ROUTE) {
            ClientListRoute(
                onNewClient = { NavUtils.navigateToClientDetail(navController) },
                onClient = { id -> NavUtils.navigateToClientDetail(navController, id) },
                onShowSnackbar = onShowSnackbar,
            )
        }

        composable(
            route = NavUtils.CLIENT_DETAIL_ROUTE,
            arguments = listOf(
                navArgument(NavUtils.CLIENT_ID_ARG) {
                    type = NavType.LongType
                    defaultValue = -1
                },
            )
        ) {
            ClientDetailRoute(
                onShowSnackbar = onShowSnackbar,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
