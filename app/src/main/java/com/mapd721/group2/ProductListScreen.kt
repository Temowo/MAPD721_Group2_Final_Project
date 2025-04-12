package com.mapd721.group2

import android.content.Intent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    val offsetX = remember { Animatable(-300f) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        while (true) {
            offsetX.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 1500, easing = LinearEasing)
            )
            delay(1000)
            offsetX.snapTo(-300f)
        }
    }

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
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        // 🔥 Header Section with Emoji Icon
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Cart icon top right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { navController.navigate("cart") }) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Cart",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(26.dp)
                    )
                }

                IconButton(
                    onClick = {
                        // Intent to launch App 1
                        val intent = Intent().apply {
                            action = "com.plcoding.bluetoothchat.OPEN"
                        }
                        context.startActivity(intent)
                    }) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "View Cart",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }

            }


            // Title with Emoji icon (no drawable)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {

                Text(
                    text = "🛒",
                    fontSize = 36.sp,
                    modifier = Modifier.padding(end = 8.dp)
                )

                Text(
                    text = "Shop for Products",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Find the best tech gadgets and deals",
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .graphicsLayer { translationX = offsetX.value }
                    .fillMaxWidth()
            )
        }

        // 🔍 Search Bar
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(10.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search Products") },
                trailingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )
        }

        // 🛒 Product List
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
