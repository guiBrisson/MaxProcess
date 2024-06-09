package com.brisson.maxprocess.ui.navigation

import androidx.navigation.NavHostController
import androidx.navigation.NavOptions

object NavUtils {
    // Args
    const val CLIENT_ID_ARG = "client_id"

    // Screens
    private const val CLIENT_LIST_SCREEN = "client_list"
    private const val CLIENT_DETAIL_SCREEN = "client_detail"

    // Routes
    const val CLIENT_LIST_ROUTE = CLIENT_LIST_SCREEN
    const val CLIENT_DETAIL_ROUTE =
        "$CLIENT_DETAIL_SCREEN?$CLIENT_ID_ARG={$CLIENT_ID_ARG}"

    fun navigateToClientDetail(navHostController: NavHostController, clientId: Long? = -1) {
        val route = CLIENT_DETAIL_ROUTE.replace("{${CLIENT_ID_ARG}}", clientId.toString())
        navHostController.navigate(route, NavOptions.Builder().setLaunchSingleTop(true).build())
    }
}
