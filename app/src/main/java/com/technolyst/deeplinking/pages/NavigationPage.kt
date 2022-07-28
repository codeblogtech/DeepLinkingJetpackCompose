package com.technolyst.deeplinking.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import androidx.navigation.navOptions
import com.technolyst.deeplinking.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationPage() {

    var selectedItem = remember { mutableStateOf(0) }
    val items = listOf("Home", "Product", "Setting")
    var navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route


    Scaffold(topBar = {
        SmallTopAppBar(title = { Text(text = "DeepLinking Demo $currentRoute") })
    }, bottomBar = {

        NavigationBar() {
            items.forEachIndexed { index, item ->
                NavigationBarItem(

                    icon = {
                        if (item == "Home") {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_action_home),
                                contentDescription = null
                            )
                        } else if (item == "Product") {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_action_product),
                                contentDescription = null
                            )
                        } else {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_action_setting),
                                contentDescription = null
                            )
                        }

                    },
                    label = { Text(item) },
                    selected = index == selectedItem.value,
                    onClick = {
                        selectedItem.value = index
                        navController.navigate(item, navOptions {

                            // avoid building up a large stack of destinations
                            // on the back stack as users select items
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        })
                    }
                )
            }
        }

    }) {

        Box(modifier = Modifier.padding(it)) {

            NavHost(navController = navController, startDestination = "Home") {

                navigation(startDestination = "HomePage", route = "Home",) {
                    composable("HomePage" ) {
                        HomePage()
                    }

                }

                navigation(startDestination = "ProductPage", route = "Product") {

                    composable("ProductPage") {
                        ProductPage(navController)
                    }
                    composable("ProductDetailPage"){
                        ProductDetailPage(navController)
                    }

                }

                navigation(startDestination = "SettingPage", route = "Setting") {
                    composable("SettingPage") {
                        SettingPage()
                    }

                }


            }


        }

    }


}