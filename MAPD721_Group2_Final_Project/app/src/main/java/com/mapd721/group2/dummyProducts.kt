package com.mapd721.group2

data class Product(
    val id: Int,
    val name: String,
    val imageName: String, // Will reference drawable resources
    val price: Double,
    val description: String
)

val dummyProducts = listOf(
    Product(
        id = 1,
        name = "Wireless Headphones",
        imageName = "headphones",
        price = 99.99,
        description = "Noise-cancelling wireless headphones with 30hr battery"
    ),
    Product(
        id = 2,
        name = "Smart Watch",
        imageName = "smartwatch",
        price = 199.99,
        description = "Fitness tracking and notifications"
    ),
    Product(
        id = 3,
        name = "Bluetooth Speaker",
        imageName = "speaker",
        price = 59.99,
        description = "Portable waterproof speaker"
    ),
    Product(
        id = 4,
        name = "Gaming Mouse",
        imageName = "mouse",
        price = 45.99,
        description = "RGB gaming mouse with 6 buttons"
    ),
    Product(
        id = 5,
        name = "Mechanical Keyboard",
        imageName = "keyboard",
        price = 89.99,
        description = "Cherry MX switches with RGB backlight"
    ),
    Product(
        id = 6,
        name = "External SSD",
        imageName = "ssd",
        price = 129.99,
        description = "1TB portable solid state drive"
    ),
    Product(
        id = 7,
        name = "Wireless Charger",
        imageName = "charger",
        price = 29.99,
        description = "15W fast wireless charging pad"
    )
)

// Helper function to get drawable resource ID
fun getImageResource(imageName: String): Int {
    return when (imageName) {
        "headphones" -> R.drawable.headphones
        "smartwatch" -> R.drawable.smartwatch
        "speaker" -> R.drawable.speaker
        "mouse" -> R.drawable.mouse
        "keyboard" -> R.drawable.keyboard
        "ssd" -> R.drawable.ssd
        "charger" -> R.drawable.charger
        else -> R.drawable.ic_placeholder
    }
}