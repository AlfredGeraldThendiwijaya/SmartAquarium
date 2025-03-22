package com.example.smartaquarium.navigation

sealed class Screen(val route: String){
    data object Splash : Screen("splash")
    data object Home : Screen("home")
    data object Detail : Screen("detail")
    data object setting : Screen("setting")
    data object schedule : Screen("schedule")
    data object login : Screen("login")
    data object termsandcondition : Screen("termsandcondition")

}