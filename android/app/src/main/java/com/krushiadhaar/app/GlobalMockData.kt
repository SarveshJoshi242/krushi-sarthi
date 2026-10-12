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
    private const val KEY_PERSONAL_EXPENSES = "personal_expenses_json"
    private const val KEY_INVENTORY_ITEMS = "inventory_items_json"
    private const val KEY_ESTIMATED_CROP_COST = "estimated_crop_cost"
    private const val KEY_ESTIMATED_CROP_NAME = "estimated_crop_name"
    private const val KEY_ESTIMATED_CROP_AREA = "estimated_crop_area"

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
    
    data class PersonalExpense(
        val id: String = java.util.UUID.randomUUID().toString(),
        val title: String,
        val category: String,
        val amount: Int,
        val date: String,
        val isExpense: Boolean = true
    )

    data class AgriInventoryItem(
        val id: String = java.util.UUID.randomUUID().toString(),
        val name: String,
        val category: String,
        val quantity: Double,
        val unit: String,
        val pricePerUnit: Int = 0,
        val storageLocation: String = "Farm Godown",
        val status: String = "In Stock",
        val brandOrVariety: String = "",
        val expiryOrServiceDate: String = "",
        val notes: String = "",
        val emoji: String = "📦"
    )

    val walletBalance = mutableStateOf(15000)
    val transactions = androidx.compose.runtime.mutableStateListOf<Transaction>()
    val cropYieldRevenue = androidx.compose.runtime.mutableStateOf(0)
    data class Notification(val message: String, val time: String)
    val farmerNotifications = androidx.compose.runtime.mutableStateListOf<Notification>()

    // Farm Inventory State
    val inventoryItems = androidx.compose.runtime.mutableStateListOf<AgriInventoryItem>()

    // Personal Accounting & Farm Tally State
    val personalExpenses = androidx.compose.runtime.mutableStateListOf<PersonalExpense>()
    val estimatedCropCost = androidx.compose.runtime.mutableIntStateOf(40000)
    val estimatedCropName = androidx.compose.runtime.mutableStateOf("Wheat")
    val estimatedCropArea = androidx.compose.runtime.mutableStateOf("5")

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

        // Restore personal expenses
        val peJson = sp.getString(KEY_PERSONAL_EXPENSES, null)
        personalExpenses.clear()
        if (!peJson.isNullOrEmpty()) {
            try {
                val listType = object : TypeToken<List<PersonalExpense>>() {}.type
                val savedPe: List<PersonalExpense> = gson.fromJson(peJson, listType)
                personalExpenses.addAll(savedPe)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (personalExpenses.isEmpty()) {
            personalExpenses.addAll(
                listOf(
                    PersonalExpense(title = "Tractor Diesel & Servicing", category = "Farm Operations", amount = 4500, date = "12 Oct 2026", isExpense = true),
                    PersonalExpense(title = "Household Groceries & Provisions", category = "Household", amount = 5800, date = "11 Oct 2026", isExpense = true),
                    PersonalExpense(title = "KCC Kisan Credit Card EMI", category = "Loan & EMI", amount = 3200, date = "08 Oct 2026", isExpense = true),
                    PersonalExpense(title = "Cattle Fodder & Mineral Mix", category = "Livestock & Dairy", amount = 2400, date = "06 Oct 2026", isExpense = true),
                    PersonalExpense(title = "Borewell Electricity Bill", category = "Utilities", amount = 1100, date = "04 Oct 2026", isExpense = true),
                    PersonalExpense(title = "Children School Books & Fees", category = "Health & Education", amount = 2500, date = "02 Oct 2026", isExpense = true),
                    PersonalExpense(title = "Daily Milk Dairy Collection", category = "Farm Income", amount = 8500, date = "10 Oct 2026", isExpense = false),
                    PersonalExpense(title = "PM-KISAN DBT Installment", category = "Farm Income", amount = 2000, date = "01 Oct 2026", isExpense = false)
                )
            )
        }

        // Restore inventory items
        val invJson = sp.getString(KEY_INVENTORY_ITEMS, null)
        inventoryItems.clear()
        if (!invJson.isNullOrEmpty()) {
            try {
                val listType = object : TypeToken<List<AgriInventoryItem>>() {}.type
                val savedInv: List<AgriInventoryItem> = gson.fromJson(invJson, listType)
                inventoryItems.addAll(savedInv)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (inventoryItems.isEmpty()) {
            inventoryItems.addAll(
                listOf(
                    AgriInventoryItem(
                        name = "Soyabean (JS-335)",
                        category = "Harvested Produce",
                        quantity = 35.0,
                        unit = "Quintals",
                        pricePerUnit = 4600,
                        storageLocation = "Farm Godown",
                        status = "In Stock",
                        brandOrVariety = "JS-335 Certified",
                        expiryOrServiceDate = "Harvest 2026",
                        notes = "Cleaned and ready for APMC Mandi sale",
                        emoji = "🌾"
                    ),
                    AgriInventoryItem(
                        name = "Wheat (Lokwan)",
                        category = "Harvested Produce",
                        quantity = 20.0,
                        unit = "Quintals",
                        pricePerUnit = 2600,
                        storageLocation = "Home Storeroom",
                        status = "In Stock",
                        brandOrVariety = "Lokwan Grade A",
                        expiryOrServiceDate = "Harvest 2026",
                        notes = "Stored with neem leaves protection",
                        emoji = "🌾"
                    ),
                    AgriInventoryItem(
                        name = "IFFCO DAP (18:46:0)",
                        category = "Fertilizers",
                        quantity = 8.0,
                        unit = "Bags",
                        pricePerUnit = 1350,
                        storageLocation = "Farm Godown",
                        status = "In Stock",
                        brandOrVariety = "IFFCO Cooperative",
                        expiryOrServiceDate = "Exp: Dec 2027",
                        notes = "Phosphatic fertilizer for basal dose",
                        emoji = "🧪"
                    ),
                    AgriInventoryItem(
                        name = "Neem Coated Urea",
                        category = "Fertilizers",
                        quantity = 2.0,
                        unit = "Bags",
                        pricePerUnit = 266,
                        storageLocation = "Farm Godown",
                        status = "Low Stock",
                        brandOrVariety = "RCF Trombay",
                        expiryOrServiceDate = "Exp: Aug 2027",
                        notes = "Reorder 4 bags for top dressing",
                        emoji = "⚠️"
                    ),
                    AgriInventoryItem(
                        name = "KDS-726 Phule Sangam Seed",
                        category = "Seeds",
                        quantity = 60.0,
                        unit = "kg",
                        pricePerUnit = 95,
                        storageLocation = "Home Storeroom",
                        status = "In Stock",
                        brandOrVariety = "Mahabeej Certified",
                        expiryOrServiceDate = "Sowing 2026",
                        notes = "Treated with Trichoderma & Rhizobium",
                        emoji = "🌱"
                    ),
                    AgriInventoryItem(
                        name = "Coragen Chlorantraniliprole",
                        category = "Pesticides",
                        quantity = 1.5,
                        unit = "Liters",
                        pricePerUnit = 1900,
                        storageLocation = "Field Shed",
                        status = "In Stock",
                        brandOrVariety = "FMC India",
                        expiryOrServiceDate = "Exp: Oct 2026",
                        notes = "Broad spectrum insect protection",
                        emoji = "🛡️"
                    ),
                    AgriInventoryItem(
                        name = "Mahindra 575 DI Tractor",
                        category = "Machinery & Tools",
                        quantity = 1.0,
                        unit = "Units",
                        pricePerUnit = 0,
                        storageLocation = "Tractor Shed",
                        status = "Operational",
                        brandOrVariety = "Mahindra 45 HP",
                        expiryOrServiceDate = "Service: Dec 2026",
                        notes = "Engine oil changed last month",
                        emoji = "🚜"
                    ),
                    AgriInventoryItem(
                        name = "Battery Knapsack Sprayer (16L)",
                        category = "Machinery & Tools",
                        quantity = 2.0,
                        unit = "Units",
                        pricePerUnit = 0,
                        storageLocation = "Field Shed",
                        status = "Needs Service",
                        brandOrVariety = "Aspee 12V",
                        expiryOrServiceDate = "Service Due",
                        notes = "One unit lance nozzle needs replacement",
                        emoji = "⚙️"
                    ),
                    AgriInventoryItem(
                        name = "Amul Cattle Feed Pellets (पेंडी)",
                        category = "Animal Feed",
                        quantity = 6.0,
                        unit = "Bags",
                        pricePerUnit = 1250,
                        storageLocation = "Cattle Shed",
                        status = "In Stock",
                        brandOrVariety = "Amul Dan Gold",
                        expiryOrServiceDate = "Exp: Jan 2027",
                        notes = "High protein for milking cows",
                        emoji = "🐄"
                    )
                )
            )
        }

        // Restore crop estimates
        estimatedCropCost.intValue = sp.getInt(KEY_ESTIMATED_CROP_COST, 40000)
        estimatedCropName.value = sp.getString(KEY_ESTIMATED_CROP_NAME, farmerCrop.value) ?: farmerCrop.value
        estimatedCropArea.value = sp.getString(KEY_ESTIMATED_CROP_AREA, farmerFarmArea.value) ?: farmerFarmArea.value

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
            putString(KEY_PERSONAL_EXPENSES, gson.toJson(personalExpenses.toList()))
            putString(KEY_INVENTORY_ITEMS, gson.toJson(inventoryItems.toList()))
            putInt(KEY_ESTIMATED_CROP_COST, estimatedCropCost.intValue)
            putString(KEY_ESTIMATED_CROP_NAME, estimatedCropName.value)
            putString(KEY_ESTIMATED_CROP_AREA, estimatedCropArea.value)

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

    fun addPersonalExpense(expense: PersonalExpense) {
        personalExpenses.add(0, expense)
        persist()
    }

    fun removePersonalExpense(expenseId: String) {
        personalExpenses.removeAll { it.id == expenseId }
        persist()
    }

    fun addInventoryItem(item: AgriInventoryItem) {
        inventoryItems.add(0, item)
        persist()
    }

    fun removeInventoryItem(id: String) {
        inventoryItems.removeAll { it.id == id }
        persist()
    }

    fun updateInventoryItem(item: AgriInventoryItem) {
        val index = inventoryItems.indexOfFirst { it.id == item.id }
        if (index != -1) {
            inventoryItems[index] = item
            persist()
        }
    }

    fun adjustInventoryQuantity(id: String, delta: Double) {
        val index = inventoryItems.indexOfFirst { it.id == id }
        if (index != -1) {
            val current = inventoryItems[index]
            val newQty = ((current.quantity + delta) * 10).toInt() / 10.0
            val validQty = newQty.coerceAtLeast(0.0)
            val newStatus = when {
                validQty <= 0 -> "Out of Stock"
                validQty <= 3 && current.unit.contains("Bag", ignoreCase = true) -> "Low Stock"
                validQty <= 5 && current.unit.contains("kg", ignoreCase = true) -> "Low Stock"
                else -> if (current.status == "Out of Stock" || current.status == "Low Stock") "In Stock" else current.status
            }
            inventoryItems[index] = current.copy(quantity = validQty, status = newStatus)
            persist()
        }
    }

    fun updateCropEstimate(cost: Int, crop: String, area: String) {
        estimatedCropCost.intValue = cost
        estimatedCropName.value = crop
        estimatedCropArea.value = area
        persist()
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
