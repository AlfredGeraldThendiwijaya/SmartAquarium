package com.example.smartaquarium.navigation

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.smartaquarium.ViewUI.HomeScreen
import androidx.compose.ui.Modifier
import com.example.smartaquarium.ViewModel.HomeViewModel
import com.example.smartaquarium.ViewUI.DetailScreen
import com.example.smartaquarium.ViewUI.DetectScreen
import com.example.smartaquarium.ViewUI.ScheduleScreen
import com.example.smartaquarium.ViewUI.SettingScreen
import com.example.smartaquarium.ViewUI.LoginScreen

import com.example.smartaquarium.ViewUI.SplashScreen
import com.example.smartaquarium.ViewUI.TermsAndConditionsScreen

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("ViewModelConstructorInComposable")
@Composable
fun NavigationApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
        }
        composable(Screen.login.route){
            LoginScreen(navController = navController)
        }
        composable(Screen.Home.route) {
            HomeScreen(navController = navController, viewModel = HomeViewModel())
        }
        composable(
            route = "detail/{name}/{serial}",
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?: ""
            val serial = backStackEntry.arguments?.getString("serial") ?: ""

            DetailScreen(navController = navController, aquariumName = name, aquariumSerial = serial)
        }
        composable(route = "schedule/{serial}",){
            backStackEntry ->
            val serial = backStackEntry.arguments?.getString("serial") ?: ""
            ScheduleScreen(navController = navController, aquariumSerial = serial)
        }
        composable(Screen.setting.route){
            SettingScreen(navController = navController)
        }
        composable(Screen.schedule.route){
            ScheduleScreen(navController = navController,aquariumSerial = "")
        }
        composable(Screen.termsandcondition.route){
            TermsAndConditionsScreen(navController = navController)
        }
        composable(route = "detect_fish/{serial}"){
            backStackEntry ->
            val serial = backStackEntry.arguments?.getString("serial") ?: ""
            DetectScreen(navController = navController, aquariumSerial = serial)
        }
    }
}
