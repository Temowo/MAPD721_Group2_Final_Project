package com.mapd721.group2

import android.webkit.WebView
import android.webkit.WebViewClient
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
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
    val context = LocalContext.current
    var showWebView by remember { mutableStateOf(false) } // State to control WebView visibility

    // Currency formatter
    val currencyFormat = remember {
        NumberFormat.getCurrencyInstance(Locale.getDefault()).apply {
            maximumFractionDigits = 2
        }
    }

    // Listen for Firestore changes
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

    // Calculate grand total
    val grandTotal = remember(items) {
        items.sumOf { it.total.toDouble() }.toFloat()
    }

    // Delete all products
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
                title = { "Cart Details" },
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
            .padding(16.dp)
    ) {

        Spacer(modifier = Modifier.height(paddingValues.calculateTopPadding()))

        // Input Section
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Button(
                    onClick = { showCheckoutDialog = true },
                    modifier = Modifier.weight(1f),
                    enabled = items.isNotEmpty()
                ) {
                    Text("CheckOut")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Total Price
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Total: ${currencyFormat.format(grandTotal)}",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Products List
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
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
    }



        if (showWebView) {

            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        webViewClient = object : WebViewClient() {
                            override fun shouldOverrideUrlLoading(
                                view: WebView,
                                url: String
                            ): Boolean {
                                if (url.contains("rickie-austin-114.github.io/paypal/success.html")) {
                                    // Close the WebView and return to the app
                                    view.visibility =
                                        android.view.View.GONE // or finish the activity
                                    return true
                                }
                                view.loadUrl(url)
                                return false
                            }
                        }
                        settings.javaScriptEnabled = true // Enable JavaScript
                        loadUrl("http://rickie-austin-114.github.io/paypal/index.html?amount=$grandTotal") // Initial URL
                    }
                },
                modifier = Modifier.fillMaxSize()

            )
        }

    // Delete All Confirmation Dialog
    if (showCheckoutDialog) {
        AlertDialog(
            onDismissRequest = { showCheckoutDialog = false },
            title = { Text("Checkout All Products?") },
            text = { Text("Make sure you have order all the product") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showWebView = true
                        deleteAllProducts()
                        showCheckoutDialog = false

                    }
                ) {
                    Text("Checkout", color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showCheckoutDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }}
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductItemRow(
    item: Item,
    currencyFormat: NumberFormat,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(id = getImageResource(item.image)),
                contentDescription = "",
                modifier = Modifier
                    .width(40.dp)
                    .height(40.dp)
                    .clip(RoundedCornerShape(2.dp)),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = item.productName,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "${currencyFormat.format(item.price)} × ${item.quantity} = ${currencyFormat.format(item.total)}",
                    style = MaterialTheme.typography.bodyMedium,
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

// Rest of the code remains the same (Item data class and ProductItemRow composable)
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


