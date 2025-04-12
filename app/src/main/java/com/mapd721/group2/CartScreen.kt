package com.mapd721.group2

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.NumberFormat
import java.util.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(onBack: () -> Unit) {
    val db = Firebase.firestore
    var items by remember { mutableStateOf<List<Item>>(emptyList()) }
    val scope = rememberCoroutineScope()
    var showCheckoutDialog by remember { mutableStateOf(false) }

    val currencyFormat = remember {
        NumberFormat.getCurrencyInstance(Locale.getDefault()).apply {
            maximumFractionDigits = 2
        }
    }

    LaunchedEffect(Unit) {
        db.collection("products")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                items = snapshot?.documents?.mapNotNull { doc ->
                    Item(
                        id = doc.id,
                        productName = doc.getString("productName") ?: "",
                        price = (doc.getDouble("price") ?: 0.0).toFloat(),
                        quantity = (doc.getLong("quantity") ?: 0).toInt(),
                        image = doc.getString("image") ?: ""
                    )
                } ?: emptyList()
            }
    }

    val grandTotal = remember(items) {
        items.sumOf { it.total.toDouble() }.toFloat()
    }

    fun deleteAllProducts() {
        scope.launch {
            try {
                val batch = db.batch()
                items.forEach { item ->
                    batch.delete(db.collection("products").document(item.id))
                }
                batch.commit().await()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Cart Details",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
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
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { showCheckoutDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(24.dp),
                enabled = items.isNotEmpty()
            ) {
                Text("Check Out")
            }

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Text(
                    text = "Total: ${currencyFormat.format(grandTotal)}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(items) { item ->
                    ProductItemRow(
                        item = item,
                        currencyFormat = currencyFormat,
                        onDelete = {
                            db.collection("products")
                                .document(item.id)
                                .delete()
                        }
                    )
                }
            }

            if (showCheckoutDialog) {
                AlertDialog(
                    onDismissRequest = { showCheckoutDialog = false },
                    title = { Text("Checkout All Products?") },
                    text = { Text("Make sure you have ordered all the products") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                deleteAllProducts()
                                showCheckoutDialog = false
                            }
                        ) {
                            Text("Checkout")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showCheckoutDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductItemRow(
    item: Item,
    currencyFormat: NumberFormat,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Image(
                painter = painterResource(id = getImageResource(item.image)),
                contentDescription = item.productName,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.productName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${currencyFormat.format(item.price)} × ${item.quantity} = ${currencyFormat.format(item.total)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

data class Item(
    val id: String = "",
    val productName: String = "",
    val price: Float = 0f,
    val quantity: Int = 0,
    val image: String = ""
) {
    val total: Float
        get() = price * quantity
}
