package com.krushiadhaar.app

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.krushiadhaar.app.marketplace.CropListing
import androidx.compose.ui.graphics.Color
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.krushiadhaar.app.core.storage.TokenStorage

object GlobalMockData {
    private const val PREFS_NAME = "krushi_adhaar_persistent_state"
    private const val KEY_WALLET = "wallet_balance"
    private const val KEY_REVENUE = "crop_yield_revenue"
    private const val KEY_LANGUAGE = "user_language"
    private const val KEY_LOCATION = "user_location"
    private const val KEY_TRANSACTIONS = "transactions_json"
    private const val KEY_NOTIFICATIONS = "notifications_json"

    // Farmer Profile Keys
    private const val KEY_FARMER_NAME = "farmer_name"
    private const val KEY_FARMER_PHONE = "farmer_phone"
    private const val KEY_FARMER_VILLAGE = "farmer_village"
    private const val KEY_FARMER_DISTRICT = "farmer_district"
    private const val KEY_FARMER_STATE = "farmer_state"
    private const val KEY_FARMER_LAT = "farmer_latitude"
    private const val KEY_FARMER_LON = "farmer_longitude"
    private const val KEY_FARMER_CROP = "farmer_crop"
    private const val KEY_FARMER_AREA = "farmer_area"
    private const val KEY_FARMER_UNIT = "farmer_unit"
    private const val KEY_USER_ROLE = "user_role"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"
    private const val KEY_AUTH_TOKEN = "auth_token"

    private var prefs: SharedPreferences? = null
    private var appContext: Context? = null
    private val gson = Gson()

    val userLanguage = mutableStateOf("English")
    val userLocation = mutableStateOf("Baramati, Pune • Maharashtra")

    // Farmer Profile State
    val farmerName = mutableStateOf("Ramesh Patil")
    val farmerPhone = mutableStateOf("9876543210")
    val farmerVillage = mutableStateOf("Baramati")
    val farmerDistrict = mutableStateOf("Pune")
    val farmerState = mutableStateOf("Maharashtra")
    val farmerLatitude = mutableStateOf(18.1517)
    val farmerLongitude = mutableStateOf(74.5774)
    val farmerCrop = mutableStateOf("Soyabean")
    val farmerFarmArea = mutableStateOf("5")
    val farmerAreaUnit = mutableStateOf("Acres")
    val userRole = mutableStateOf("FARMER")
    val isLoggedIn = mutableStateOf(false)
    val authToken = mutableStateOf<String?>(null)

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
        appContext = context
        val sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs = sp

        // Restore wallet & system state
        walletBalance.value = sp.getInt(KEY_WALLET, 15000)
        cropYieldRevenue.value = sp.getInt(KEY_REVENUE, 0)
        userLanguage.value = sp.getString(KEY_LANGUAGE, "English") ?: "English"
        userLocation.value = sp.getString(KEY_LOCATION, "Baramati, Pune • Maharashtra") ?: "Baramati, Pune • Maharashtra"

        // Restore Farmer Profile
        farmerName.value = sp.getString(KEY_FARMER_NAME, "Ramesh Patil") ?: "Ramesh Patil"
        farmerPhone.value = sp.getString(KEY_FARMER_PHONE, "9876543210") ?: "9876543210"
        farmerVillage.value = sp.getString(KEY_FARMER_VILLAGE, "Baramati") ?: "Baramati"
        farmerDistrict.value = sp.getString(KEY_FARMER_DISTRICT, "Pune") ?: "Pune"
        farmerState.value = sp.getString(KEY_FARMER_STATE, "Maharashtra") ?: "Maharashtra"
        farmerLatitude.value = sp.getFloat(KEY_FARMER_LAT, 18.1517f).toDouble()
        farmerLongitude.value = sp.getFloat(KEY_FARMER_LON, 74.5774f).toDouble()
        farmerCrop.value = sp.getString(KEY_FARMER_CROP, "Soyabean") ?: "Soyabean"
        farmerFarmArea.value = sp.getString(KEY_FARMER_AREA, "5") ?: "5"
        farmerAreaUnit.value = sp.getString(KEY_FARMER_UNIT, "Acres") ?: "Acres"
        userRole.value = sp.getString(KEY_USER_ROLE, "FARMER") ?: "FARMER"

        // Restore Auth Session
        val tokenStorage = TokenStorage(context)
        val storedToken = tokenStorage.getAccessToken() ?: sp.getString(KEY_AUTH_TOKEN, null)
        authToken.value = storedToken
        isLoggedIn.value = sp.getBoolean(KEY_IS_LOGGED_IN, storedToken != null)

        // Sync token back to TokenStorage if present in sp
        if (storedToken != null && tokenStorage.getAccessToken() == null) {
            tokenStorage.saveTokens(storedToken, storedToken)
        }

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

            putString(KEY_FARMER_NAME, farmerName.value)
            putString(KEY_FARMER_PHONE, farmerPhone.value)
            putString(KEY_FARMER_VILLAGE, farmerVillage.value)
            putString(KEY_FARMER_DISTRICT, farmerDistrict.value)
            putString(KEY_FARMER_STATE, farmerState.value)
            putFloat(KEY_FARMER_LAT, farmerLatitude.value.toFloat())
            putFloat(KEY_FARMER_LON, farmerLongitude.value.toFloat())
            putString(KEY_FARMER_CROP, farmerCrop.value)
            putString(KEY_FARMER_AREA, farmerFarmArea.value)
            putString(KEY_FARMER_UNIT, farmerAreaUnit.value)
            putString(KEY_USER_ROLE, userRole.value)
            putBoolean(KEY_IS_LOGGED_IN, isLoggedIn.value)
            putString(KEY_AUTH_TOKEN, authToken.value)
            apply()
        }
    }

    fun saveFarmerProfile(
        name: String,
        phone: String,
        village: String,
        district: String,
        state: String,
        latitude: Double,
        longitude: Double,
        crop: String,
        farmArea: String,
        areaUnit: String,
        role: String = "FARMER"
    ) {
        farmerName.value = name
        farmerPhone.value = phone
        farmerVillage.value = village
        farmerDistrict.value = district
        farmerState.value = state
        farmerLatitude.value = latitude
        farmerLongitude.value = longitude
        farmerCrop.value = crop
        farmerFarmArea.value = farmArea
        farmerAreaUnit.value = areaUnit
        userRole.value = role
        userLocation.value = "$village, $district • $state"

        persist()
    }

    fun setSession(loggedIn: Boolean, token: String? = null, role: String = "FARMER") {
        isLoggedIn.value = loggedIn
        authToken.value = token
        userRole.value = role

        appContext?.let {
            val tokenStorage = TokenStorage(it)
            if (loggedIn && token != null) {
                tokenStorage.saveTokens(token, token)
            } else if (!loggedIn) {
                tokenStorage.clearTokens()
            }
        }

        prefs?.edit()?.apply {
            putBoolean(KEY_IS_LOGGED_IN, loggedIn)
            putString(KEY_AUTH_TOKEN, token)
            putString(KEY_USER_ROLE, role)
            apply()
        }
    }

    fun logout() {
        setSession(false, null)
    }

    fun placeOrder(listing: CropListing) {
        marketplaceListings.remove(listing)
        val totalAmount = listing.quantityTons * listing.pricePerTon
        walletBalance.value += totalAmount
        transactions.add(0, Transaction(totalAmount, "Sold ${listing.quantityTons} Tons of ${listing.name} to Buyer", true))
        cropYieldRevenue.value += totalAmount
        farmerNotifications.add(0, Notification("Order Placed: A buyer purchased ${listing.quantityTons} Tons of ${listing.name} for ₹$totalAmount.", "Just now"))
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
