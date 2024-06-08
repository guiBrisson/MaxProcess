package com.brisson.maxprocess.ui.navigation

import androidx.navigation.NavHostController

object NavUtils {
    // Args
    const val CLIENT_ID_ARG = "client_id"

    // Screens
    const val CLIENT_LIST_SCREEN = "client_list"
    const val CLIENT_DETAIL_SCREEN = "client_detail"

    // Routes
    const val CLIENT_LIST_ROUTE = CLIENT_LIST_SCREEN
    const val CLIENT_DETAIL_ROUTE =
        "$CLIENT_DETAIL_SCREEN?$CLIENT_ID_ARG={$CLIENT_ID_ARG}"

    fun navigateToClientDetail(navHostController: NavHostController, clientId: Long? = -1) {
        val route = CLIENT_DETAIL_ROUTE.replace("{${CLIENT_ID_ARG}}", clientId.toString())
        navHostController.navigate(route)
    }
}
