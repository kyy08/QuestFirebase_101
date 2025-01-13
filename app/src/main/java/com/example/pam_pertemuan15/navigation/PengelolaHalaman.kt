package com.example.pam_pertemuan15.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.example.pam_pertemuan15.ui.view.HomeScreen
import com.example.pam_pertemuan15.ui.view.InsertMhsView
import com.example.pam_pertemuan15.ui.viewmodel.DetailViewModel

@Composable
fun PengelolaHalaman (
    modifier: Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost (
        navController = navController,
        startDestination = DestinasiHome.route,
        modifier = modifier
    ) {
        composable(route = DestinasiHome.route) {
            HomeScreen (
                navigateToltemEntry = { navController.navigate(DestinasiInsert.route) }
            )
        }

        composable(route = DestinasiInsert.route) {
            InsertMhsView(
                onBack = { navController.popBackStack() },
                onNavigate = { navController.navigate(DestinasiHome.route) }
            )
        }
    }
}