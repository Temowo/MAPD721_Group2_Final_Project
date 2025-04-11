package com.mapd721.group2

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


@Composable
fun ProductListScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }


    // Filter your existing dummyProducts based on the search query
    val filteredProducts = remember(searchQuery) {
        if (searchQuery.isNotBlank()) {
            dummyProducts.filter {
                it.name.contains(searchQuery, ignoreCase = true)
            }
        } else {
            dummyProducts
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Search Bar (Live Filter)
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search Products") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { navController.navigate("cart") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("View Cart")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredProducts) { product ->
                ProductCard(
                    product = product,
                    onClick = { navController.navigate("productDetail/${product.id}") }
                )
            }
        }
    }
}