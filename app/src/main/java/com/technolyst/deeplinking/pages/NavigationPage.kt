package com.technolyst.deeplinking.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavDeepLink
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import androidx.navigation.navOptions
import com.technolyst.deeplinking.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationPage() {

    //Create items for bottom bar.
    val tabItems = listOf("Home", "Product", "Setting")
    // Now we will handle the click event and save the last action.
    // we will save the index value of tab when user click on Bar item.
    // for that first we create mutableState for saving the index number.
    // init with zero index.
    // its mutable state value change on click event. lets code.
    var selectedItem = remember { mutableStateOf(0) }

    // first create navController object.
    var navController = rememberNavController()

    // get navBackStackEntry as State so we can refresh the ui onBackStack event.
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    // get selected route name and show on SmallTopBar.
    // parent route name means nested graph route name.
    val parentRouteName = navBackStackEntry.value?.destination?.parent?.route

    //Get current page name from backStackEntry
    val routeName = navBackStackEntry.value?.destination?.route


    Scaffold(topBar = {
        SmallTopAppBar(title = { Text(text = "$routeName") })
    }, bottomBar = {
        // for bottom bar we will used NavigationBar composable.

        NavigationBar() {
            // Now in this method block we will add NavigationBarItem.
            // we have to keep the limited number of item here it should be
            // between 3 to 5
            // let's iterate "tabItems" array add Bar item.
            // we have already added tab icon in project file in res folder.

            tabItems.forEachIndexed { index, item ->
                // that for each iterate the item one by one
                //create NavigationBarItem
                // right now we have set selected value false.

                // selected value code update and check with mutableState
                // we have to update selected on back stack event so we have
                // update the logic for selected. item name as nested graph route name.

                NavigationBarItem(
                    selected = parentRouteName == item,
                    onClick = {
                        // on click we have to update mutableState value to
                        //last action perform upon tab.
                        // let's run.
                        selectedItem.value = index
                        // navigation graph is complete and now on tab click
                        // select proper navigation graph.
                        // i have put navigation graph name as our tab name so
                        // we have just used item which also the name of navigation graph.

                        // it's create large stack of destination.
                        // let's limit this stack. using navOptions.
                        navController.navigate(item,navOptions{
                            // avoid building up a large stack of destinations
                            //on the back stack as users select items.

                            popUpTo(navController.graph.findStartDestination().id){
                                saveState = true
                            }

                            // Avoid multiple copies fo the same destination when
                            // reselecting the same item.
                            launchSingleTop = true
                            restoreState = true

                        })


                    },
                    icon = {
                        //in this lambda function set icon for bar item
                        // by check item name and set icon according to that.
                        when (item) {
                            "Home" -> Icon(
                                painter = painterResource(id = R.drawable.ic_action_home),
                                contentDescription = null
                            )
                            "Product" -> Icon(
                                painter = painterResource(id = R.drawable.ic_action_product),
                                contentDescription = null
                            )

                            "Setting" -> Icon(
                                painter = painterResource(id = R.drawable.ic_action_setting),
                                contentDescription = null
                            )
                        }

                    },
                    label = { Text(text = item) })
            }

        }

    }) {
        Box(modifier = Modifier.padding(it)) {

            // in content area of Scaffold create NavHost.
            // for demo we have already Created Home Page, Product Page , Setting page
            NavHost(navController = navController, startDestination = "Home") {
                //Create Nested Navigation .
                navigation(startDestination = "HomePage", route = "Home",deepLinks = listOf(NavDeepLink("deeplink://home"))) {
                    // in Nested Navigation Add Composable Pages.
                    composable("HomePage") {
                        HomePage(navController = navController)
                    }
                    // Add another route in Home nested navigation.
                    composable("HomeDetailPage" ,deepLinks = listOf(NavDeepLink("deeplink://homeDetail"))) {
                        HomeDetailPage(navController = navController)
                    }
                }

                // Now Add Product Nested Navigation Like Home Page.

                navigation(startDestination = "ProductPage", route = "Product") {
                    // Add pages.
                    composable("ProductPage" , deepLinks = listOf(NavDeepLink("deeplink://product"))) {
                        ProductPage(navController = navController)
                    }
                    composable("ProductDetailPage",deepLinks = listOf(NavDeepLink("deeplink://productDetail"))) {
                        ProductDetailPage(navController = navController)
                    }

                }

                //Now Add final Tab Setting Page.

                navigation(startDestination = "SettingPage", route = "Setting") {
                    composable("SettingPage") {
                        SettingPage()
                    }
                }


            }


        }
    }
}