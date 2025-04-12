package com.mapd721.group2

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(product: Product?, onBack: () -> Unit) {
    val db = Firebase.firestore
    val context = LocalContext.current
    var quantity by remember { mutableIntStateOf(1) }
    var imageVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        imageVisible = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(product?.name ?: "Product Details")
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            product?.let {
                AnimatedVisibility(
                    visible = imageVisible,
                    enter = scaleIn(
                        animationSpec = tween(durationMillis = 900),
                        initialScale = 0.8f
                    )
                ) {
                    Image(
                        painter = painterResource(id = getImageResource(it.imageName)),
                        contentDescription = it.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                Text(
                    text = "$${it.price}",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = it.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                )

                // Quantity selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { if (quantity > 1) quantity-- },
                        shape = RoundedCornerShape(50)
                    ) {
                        Text("-", style = MaterialTheme.typography.titleLarge)
                    }

                    Text(
                        text = quantity.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Button(
                        onClick = { quantity++ },
                        shape = RoundedCornerShape(50)
                    ) {
                        Text("+", style = MaterialTheme.typography.titleLarge)
                    }
                }

                // Add to Cart button
                Button(
                    onClick = {
                        val newProduct = hashMapOf(
                            "productName" to it.name,
                            "price" to it.price.toFloat(),
                            "quantity" to quantity,
                            "image" to it.imageName
                        )

                        db.collection("products")
                            .add(newProduct)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Failed to add product", Toast.LENGTH_SHORT).show()
                            }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Add to Cart", style = MaterialTheme.typography.titleMedium)
                }

            } ?: run {
                Text("Product not found", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
