package com.technolyst.deeplinking.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController

@Composable
fun ProductPage(navController: NavHostController) {

    Box(modifier = Modifier
        .background(Color.Gray)
        .fillMaxSize()) {

        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {


            Text(text = "Product Page ")

            Button(onClick = {
                navController.navigate("ProductDetailPage")
            }) {

                Text(text = "Product Detail Page.")
            }
        }

    }

}
