package com.krushiadhaar.app

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.krushiadhaar.app.marketplace.CropListing
import androidx.compose.ui.graphics.Color
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object GlobalMockData {
    private const val PREFS_NAME = "krushi_adhaar_persistent_state"
    private const val KEY_WALLET = "wallet_balance"
    private const val KEY_REVENUE = "crop_yield_revenue"
    private const val KEY_LANGUAGE = "user_language"
    private const val KEY_LOCATION = "user_location"
    private const val KEY_TRANSACTIONS = "transactions_json"
    private const val KEY_NOTIFICATIONS = "notifications_json"

    private var prefs: SharedPreferences? = null
    private val gson = Gson()

    val userLanguage = mutableStateOf("English")
    val userLocation = mutableStateOf("Maharashtra, India")

    val marketplaceListings = mutableStateListOf(
        CropListing("Sugarcane", "Ramesh Patil", "Pune, Maharashtra", 10, 2500, "\uD83C\uDF3F", Color(0xFFE8F5E9)),
        CropListing("Cotton", "Suresh Kumar", "Nagpur, Maharashtra", 5, 8000, "\uD83C\uDF31", Color(0xFFE3F2FD)),
        CropListing("Wheat", "Anil Deshmukh", "Nashik, Maharashtra", 20, 2600, "\uD83C\uDF3E", Color(0xFFFFF3E0)),
        CropListing("Rice", "Prakash Kadam", "Kolhapur, Maharashtra", 15, 3000, "\uD83C\uDF3E", Color(0xFFE0F7FA))
    )

    data class Transaction(val amount: Int, val description: String, val isCredit: Boolean)
    
    val walletBalance = mutableStateOf(15000)
    val transactions = androidx.compose.runtime.mutableStateListOf<Transaction>()
    val cropYieldRevenue = androidx.compose.runtime.mutableStateOf(0)
    data class Notification(val message: String, val time: String)
    val farmerNotifications = androidx.compose.runtime.mutableStateListOf<Notification>()

    var selectedImageUri: String? = null
    var capturedBitmap: android.graphics.Bitmap? = null

    fun init(context: Context) {
        val sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs = sp

        // Restore wallet balance
        walletBalance.value = sp.getInt(KEY_WALLET, 15000)
        cropYieldRevenue.value = sp.getInt(KEY_REVENUE, 0)
        userLanguage.value = sp.getString(KEY_LANGUAGE, "English") ?: "English"
        userLocation.value = sp.getString(KEY_LOCATION, "Maharashtra, India") ?: "Maharashtra, India"

        // Restore transactions
        val txJson = sp.getString(KEY_TRANSACTIONS, null)
        if (!txJson.isNullOrEmpty()) {
            try {
                val listType = object : TypeToken<List<Transaction>>() {}.type
                val savedTx: List<Transaction> = gson.fromJson(txJson, listType)
                transactions.clear()
                transactions.addAll(savedTx)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Restore notifications
        val notifJson = sp.getString(KEY_NOTIFICATIONS, null)
        if (!notifJson.isNullOrEmpty()) {
            try {
                val listType = object : TypeToken<List<Notification>>() {}.type
                val savedNotifs: List<Notification> = gson.fromJson(notifJson, listType)
                farmerNotifications.clear()
                farmerNotifications.addAll(savedNotifs)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun persist() {
        prefs?.edit()?.apply {
            putInt(KEY_WALLET, walletBalance.value)
            putInt(KEY_REVENUE, cropYieldRevenue.value)
            putString(KEY_LANGUAGE, userLanguage.value)
            putString(KEY_LOCATION, userLocation.value)
            putString(KEY_TRANSACTIONS, gson.toJson(transactions.toList()))
            putString(KEY_NOTIFICATIONS, gson.toJson(farmerNotifications.toList()))
            apply()
        }
    }

    fun placeOrder(listing: CropListing) {
        // Remove from inventory/marketplace
        marketplaceListings.remove(listing)
        
        // Add to wallet
        val totalAmount = listing.quantityTons * listing.pricePerTon
        walletBalance.value += totalAmount
        transactions.add(0, Transaction(totalAmount, "Sold ${listing.quantityTons} Tons of ${listing.name} to Buyer", true))
        
        // Add to crop yield revenue
        cropYieldRevenue.value += totalAmount
        
        // Notify farmer
        farmerNotifications.add(0, Notification("Order Placed: A buyer purchased ${listing.quantityTons} Tons of ${listing.name} for ₹$totalAmount.", "Just now"))

        // Persist to storage
        persist()
    }
    
    fun sendNegotiationMessage(farmerName: String, buyerName: String) {
        farmerNotifications.add(0, Notification("New Message: $buyerName wants to negotiate for your crop.", "Just now"))
        persist()
    }

    fun updateLanguage(lang: String) {
        userLanguage.value = lang
        prefs?.edit()?.putString(KEY_LANGUAGE, lang)?.apply()
    }

    fun updateLocation(loc: String) {
        userLocation.value = loc
        prefs?.edit()?.putString(KEY_LOCATION, loc)?.apply()
    }
}
