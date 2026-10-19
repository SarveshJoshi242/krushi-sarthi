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
import com.krushiadhaar.app.community.CommunityPost
import com.krushiadhaar.app.community.CommunityReply
import com.krushiadhaar.app.community.CommunityNotificationHelper

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
    private const val KEY_COMMUNITY_POSTS = "community_posts_json"
    private const val KEY_UNREAD_COMMUNITY_COUNT = "unread_community_count"

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
    private const val KEY_FARMER_SOIL = "farmer_soil"
    private const val KEY_PEST_SCANS = "pest_scans_json"
    private const val KEY_USER_ROLE = "user_role"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"
    private const val KEY_AUTH_TOKEN = "auth_token"
    private const val KEY_MARKETPLACE_LISTINGS = "marketplace_listings_json"

    data class CropListingDto(
        val name: String,
        val farmer: String,
        val location: String,
        val quantityTons: Int,
        val pricePerTon: Int,
        val emoji: String,
        val imageUri: String? = null,
        val originalQuantity: Double? = null,
        val originalUnit: String? = null,
        val originalPrice: Int? = null,
        val grade: String? = null
    )

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
    val pestSelectedCrop = mutableStateOf("Auto-Detect")
    val farmerFarmArea = mutableStateOf("5")
    val farmerAreaUnit = mutableStateOf("Acres")
    val farmerSoilType = mutableStateOf("Optimal Fertile Loam")
    val userRole = mutableStateOf("FARMER")
    val isLoggedIn = mutableStateOf(false)
    val authToken = mutableStateOf<String?>(null)

    // Community Module State (Twitter / X for Farmers & Buyers)
    val communityPosts = mutableStateListOf<CommunityPost>()
    val unreadCommunityCount = androidx.compose.runtime.mutableIntStateOf(0)
    val pendingNavigationRoute = mutableStateOf<String?>(null)

    data class PestScanHistory(
        val id: String = java.util.UUID.randomUUID().toString(),
        val crop: String,
        val diseaseName: String,
        val scientificName: String,
        val severity: String,
        val confidence: Double,
        val symptoms: String,
        val medicines: String,
        val estimatedCost: Double,
        val management: String,
        val dateText: String,
        val emoji: String = "🌱",
        val imageUri: String? = null
    )

    val pestScanHistoryList = mutableStateListOf<PestScanHistory>()

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
        farmerSoilType.value = sp.getString(KEY_FARMER_SOIL, "Optimal Fertile Loam") ?: "Optimal Fertile Loam"
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

        // Restore pest scan history
        val scansJson = sp.getString(KEY_PEST_SCANS, null)
        pestScanHistoryList.clear()
        if (!scansJson.isNullOrEmpty()) {
            try {
                val listType = object : TypeToken<List<PestScanHistory>>() {}.type
                val savedScans: List<PestScanHistory> = gson.fromJson(scansJson, listType)
                pestScanHistoryList.addAll(savedScans)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (pestScanHistoryList.isEmpty()) {
            pestScanHistoryList.addAll(
                listOf(
                    PestScanHistory(
                        crop = "Pumpkin",
                        diseaseName = "Downy Mildew",
                        scientificName = "Pseudoperonospora spp.",
                        severity = "Severe / Advanced Stage",
                        confidence = 0.986,
                        symptoms = "Yellow to pale angular leaf spots; gray to purplish downy growth on leaf undersides during humid conditions.",
                        medicines = "Ridomil Gold (Metalaxyl 4% + Mancozeb 64% WP) @ 2.5g/L water, or Saaf @ 2g/L",
                        estimatedCost = 750.0,
                        management = "Improve airflow; reduce prolonged leaf wetness; avoid overhead irrigation where practical; remove severely affected leaves and maintain field sanitation.",
                        dateText = "Just now",
                        emoji = "🎃"
                    ),
                    PestScanHistory(
                        crop = "Cotton",
                        diseaseName = "Leaf Spot",
                        scientificName = "Cercospora gossypina",
                        severity = "Moderate",
                        confidence = 0.942,
                        symptoms = "Circular brown spots with purple margins on leaves.",
                        medicines = "Copper Oxychloride 50 WP @ 3g/L or Mancozeb @ 2.5g/L",
                        estimatedCost = 550.0,
                        management = "Destroy infected crop residues and spray preventative fungicide.",
                        dateText = "2 days ago",
                        emoji = "🪴"
                    ),
                    PestScanHistory(
                        crop = "Wheat",
                        diseaseName = "Leaf Rust",
                        scientificName = "Puccinia triticina",
                        severity = "Severe",
                        confidence = 0.965,
                        symptoms = "Orange-brown pustules on leaf blades.",
                        medicines = "Propiconazole 25 EC (Tilt) @ 1ml/L water",
                        estimatedCost = 680.0,
                        management = "Spray at early emergence of rust pustules and use resistant varieties.",
                        dateText = "1 week ago",
                        emoji = "🌾"
                    )
                )
            )
        }

        // Restore marketplace listings
        val listingsJson = sp.getString(KEY_MARKETPLACE_LISTINGS, null)
        if (!listingsJson.isNullOrEmpty()) {
            try {
                val listType = object : TypeToken<List<CropListingDto>>() {}.type
                val savedListings: List<CropListingDto> = gson.fromJson(listingsJson, listType)
                marketplaceListings.clear()
                marketplaceListings.addAll(savedListings.map { dto ->
                    CropListing(
                        name = dto.name,
                        farmer = dto.farmer,
                        location = dto.location,
                        quantityTons = dto.quantityTons,
                        pricePerTon = dto.pricePerTon,
                        emoji = dto.emoji,
                        emojiBg = Color(0xFFE8F5E9),
                        imageUri = dto.imageUri,
                        originalQuantity = dto.originalQuantity,
                        originalUnit = dto.originalUnit,
                        originalPrice = dto.originalPrice,
                        grade = dto.grade
                    )
                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Restore Community Posts & Unread Count
        unreadCommunityCount.intValue = sp.getInt(KEY_UNREAD_COMMUNITY_COUNT, 0)
        val postsJson = sp.getString(KEY_COMMUNITY_POSTS, null)
        communityPosts.clear()
        if (!postsJson.isNullOrEmpty()) {
            try {
                val listType = object : TypeToken<List<CommunityPost>>() {}.type
                val savedPosts: List<CommunityPost> = gson.fromJson(postsJson, listType)
                communityPosts.addAll(savedPosts)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (communityPosts.isEmpty()) {
            seedInitialCommunityPosts()
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
            putString(KEY_PEST_SCANS, gson.toJson(pestScanHistoryList.toList()))
            val listingsDto = marketplaceListings.map { l ->
                CropListingDto(
                    name = l.name,
                    farmer = l.farmer,
                    location = l.location,
                    quantityTons = l.quantityTons,
                    pricePerTon = l.pricePerTon,
                    emoji = l.emoji,
                    imageUri = l.imageUri,
                    originalQuantity = l.originalQuantity,
                    originalUnit = l.originalUnit,
                    originalPrice = l.originalPrice,
                    grade = l.grade
                )
            }
            putString(KEY_MARKETPLACE_LISTINGS, gson.toJson(listingsDto))
            putInt(KEY_ESTIMATED_CROP_COST, estimatedCropCost.intValue)
            putString(KEY_ESTIMATED_CROP_NAME, estimatedCropName.value)
            putString(KEY_ESTIMATED_CROP_AREA, estimatedCropArea.value)

            putString(KEY_COMMUNITY_POSTS, gson.toJson(communityPosts.toList()))
            putInt(KEY_UNREAD_COMMUNITY_COUNT, unreadCommunityCount.intValue)

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
            putString(KEY_FARMER_SOIL, farmerSoilType.value)
            putString(KEY_USER_ROLE, userRole.value)
            putBoolean(KEY_IS_LOGGED_IN, isLoggedIn.value)
            putString(KEY_AUTH_TOKEN, authToken.value)
            apply()
        }
    }

    fun addMarketplaceListing(listing: CropListing) {
        marketplaceListings.add(0, listing)
        persist()
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

    fun setFarmerCrop(crop: String) {
        farmerCrop.value = crop
        estimatedCropName.value = crop
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
        soilType: String = "Optimal Fertile Loam",
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
        farmerSoilType.value = soilType
        userRole.value = role
        userLocation.value = "$village, $district • $state"

        persist()
    }

    fun addPestScan(history: PestScanHistory) {
        pestScanHistoryList.add(0, history)
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

    fun depositToWallet(amount: Int, description: String = "Deposit to Wallet") {
        if (amount > 0) {
            walletBalance.value += amount
            transactions.add(0, Transaction(amount, description, true))
            persist()
        }
    }

    fun withdrawFromWallet(amount: Int, description: String = "Bank Withdrawal"): Boolean {
        if (amount > 0 && walletBalance.value >= amount) {
            walletBalance.value -= amount
            transactions.add(0, Transaction(amount, description, false))
            persist()
            return true
        }
        return false
    }

    fun updateLanguage(lang: String) {
        userLanguage.value = lang
        prefs?.edit()?.putString(KEY_LANGUAGE, lang)?.apply()
    }

    fun updateLocation(loc: String) {
        userLocation.value = loc
        prefs?.edit()?.putString(KEY_LOCATION, loc)?.apply()
    }

    private fun seedInitialCommunityPosts() {
        val seeded = listOf(
            CommunityPost(
                authorName = "Ramesh Patil",
                authorHandle = "@ramesh_farmer",
                authorRole = "FARMER",
                authorLocation = "Baramati, Pune",
                authorAvatarInitials = "RP",
                isVerified = true,
                content = "Shifted 3 acres to drip fertigation for HD-2967 Wheat. Diesel cost dropped by ₹4,200/acre and zero water-logging during critical crown root initiation (CRI). Brother farmers, adopt fertigation early! It saves water and doubles grain weight.",
                postType = "EXPERIENCE",
                cropTag = "Wheat",
                tags = listOf("#FarmerExperience", "#WaterSaving", "#Wheat2026"),
                timestampText = "12m ago",
                likesCount = 28,
                isLiked = false,
                repostsCount = 6,
                isReposted = false,
                repliesCount = 2,
                replies = mutableListOf(
                    CommunityReply(
                        postId = "",
                        authorName = "Suresh Kumar",
                        authorHandle = "@suresh_k",
                        authorRole = "FARMER",
                        authorLocation = "Nagpur, MH",
                        content = "Ramesh bhai, what venturi injector size did you use for 3 acres?",
                        timestampText = "8m ago",
                        likesCount = 3
                    ),
                    CommunityReply(
                        postId = "",
                        authorName = "Ramesh Patil",
                        authorHandle = "@ramesh_farmer",
                        authorRole = "FARMER",
                        authorLocation = "Baramati, Pune",
                        content = "Used 1.5-inch venturi with flow control valve. Works very smoothly!",
                        timestampText = "5m ago",
                        likesCount = 2
                    )
                )
            ),
            CommunityPost(
                authorName = "Apex Agro Foods & Exports",
                authorHandle = "@apex_procure",
                authorRole = "BUYER",
                authorLocation = "Navi Mumbai / Pune Mandi",
                authorAvatarInitials = "AA",
                isVerified = true,
                content = "🚨 URGENT BUYER REQUIREMENT: Need 60 Metric Tons of Grade-A Soybean (Moisture < 10%, Foreign matter < 2%). Offering premium spot cash payment of ₹4,950/Quintal at farmgate. Immediate weighment and direct transfer to farmer bank account!",
                postType = "BUYER_DEMAND",
                cropTag = "Soyabean",
                tags = listOf("#BuyerDemand", "#DirectProcurement", "#SpotCash", "#SoybeanBuyer"),
                timestampText = "45m ago",
                likesCount = 42,
                isLiked = false,
                repostsCount = 18,
                isReposted = false,
                repliesCount = 1,
                demandQuantity = "60 Metric Tons",
                offeredPrice = "₹4,950 / Quintal",
                buyerContactPhone = "+91 98230 45678",
                replies = mutableListOf(
                    CommunityReply(
                        postId = "",
                        authorName = "Prakash Kadam",
                        authorHandle = "@prakash_k",
                        authorRole = "FARMER",
                        authorLocation = "Kolhapur, MH",
                        content = "I have 15 tons ready in godown. Will call your team.",
                        timestampText = "20m ago",
                        likesCount = 1
                    )
                )
            ),
            CommunityPost(
                authorName = "Dr. Arvind Shinde",
                authorHandle = "@dr_arvind_agri",
                authorRole = "EXPERT",
                authorLocation = "KVK Agriculture Extension",
                authorAvatarInitials = "AS",
                isVerified = true,
                content = "💡 CRITICAL ADVISORY FOR COTTON & CHILLI: Cloudy and humid weather is triggering rapid whitefly and thrips infestation. Spray Neem Oil 10,000 ppm @ 3ml/L mixed with Fipronil 5% SC @ 1.5ml/L during early evening. Avoid spraying in direct sunlight to protect beneficial pollinators.",
                postType = "GUIDANCE",
                cropTag = "Cotton",
                tags = listOf("#AgriGuidance", "#PestControl", "#CottonCare", "#FarmerTips"),
                timestampText = "2h ago",
                likesCount = 89,
                isLiked = false,
                repostsCount = 34,
                isReposted = false,
                repliesCount = 0
            ),
            CommunityPost(
                authorName = "Balasaheb Ghadge",
                authorHandle = "@balasaheb_organic",
                authorRole = "FARMER",
                authorLocation = "Nashik, Maharashtra",
                authorAvatarInitials = "BG",
                isVerified = true,
                content = "Made 200 Litres of Jeevamrut using native Desi Cow dung, urine, jaggery and gram flour for Sugarcane crop. Microorganism count was incredible! Earthworm activity has doubled in my rootzone and soil is cool and porous. Zero chemical nitrogen purchased this entire month! #NaturalFarming #ZeroBudget #OrganicTips",
                postType = "EXPERIENCE",
                cropTag = "Sugarcane",
                tags = listOf("#NaturalFarming", "#ZeroBudget", "#OrganicTips", "#SoilHealth"),
                timestampText = "4h ago",
                likesCount = 112,
                isLiked = false,
                repostsCount = 29,
                isReposted = false,
                repliesCount = 0
            ),
            CommunityPost(
                authorName = "Kisan Mandi Logistics",
                authorHandle = "@kisan_mandi_buyer",
                authorRole = "BUYER",
                authorLocation = "Indore / Nashik Corridor",
                authorAvatarInitials = "KM",
                isVerified = true,
                content = "Looking for contract procurement of 120 Quintals of Process-Grade Red Onion (Garva variety, medium size 45-55mm, properly cured). Minimum guaranteed floor price of ₹2,200/Qtl with transport arranged directly by our logistics fleet. FPOs & farmer clusters welcome.",
                postType = "BUYER_DEMAND",
                cropTag = "Onion",
                tags = listOf("#BuyerOffer", "#OnionTrade", "#DirectDeal", "#MandiContract"),
                timestampText = "6h ago",
                likesCount = 54,
                isLiked = false,
                repostsCount = 14,
                isReposted = false,
                repliesCount = 0,
                demandQuantity = "120 Quintals",
                offeredPrice = "₹2,200 / Quintal"
            )
        )
        communityPosts.addAll(seeded)
    }

    fun addCommunityPost(post: CommunityPost, context: Context? = appContext) {
        communityPosts.add(0, post)
        farmerNotifications.add(
            0,
            Notification(
                message = "🌾 Community: ${post.authorName} (${if (post.authorRole == "BUYER") "Buyer" else "Farmer"}) posted: ${post.content.take(50)}...",
                time = "Just now"
            )
        )
        unreadCommunityCount.intValue += 1
        persist()
        context?.let {
            CommunityNotificationHelper.dispatchNewPostNotification(it, post)
        }
    }

    fun toggleLikePost(postId: String) {
        val index = communityPosts.indexOfFirst { it.id == postId }
        if (index != -1) {
            val item = communityPosts[index]
            val newLiked = !item.isLiked
            val newCount = if (newLiked) item.likesCount + 1 else maxOf(0, item.likesCount - 1)
            communityPosts[index] = item.copy(isLiked = newLiked, likesCount = newCount)
            persist()
        }
    }

    fun toggleRepostPost(postId: String) {
        val index = communityPosts.indexOfFirst { it.id == postId }
        if (index != -1) {
            val item = communityPosts[index]
            val newReposted = !item.isReposted
            val newCount = if (newReposted) item.repostsCount + 1 else maxOf(0, item.repostsCount - 1)
            communityPosts[index] = item.copy(isReposted = newReposted, repostsCount = newCount)
            persist()
        }
    }

    fun toggleBookmarkPost(postId: String) {
        val index = communityPosts.indexOfFirst { it.id == postId }
        if (index != -1) {
            val item = communityPosts[index]
            communityPosts[index] = item.copy(isBookmarked = !item.isBookmarked)
            persist()
        }
    }

    fun addReplyToPost(postId: String, reply: CommunityReply, context: Context? = appContext) {
        val index = communityPosts.indexOfFirst { it.id == postId }
        if (index != -1) {
            val item = communityPosts[index]
            val updatedReplies = item.replies.toMutableList()
            updatedReplies.add(reply)
            val updatedPost = item.copy(
                repliesCount = updatedReplies.size,
                replies = updatedReplies
            )
            communityPosts[index] = updatedPost
            unreadCommunityCount.intValue += 1
            persist()

            // Trigger notification for the new reply
            val dummyReplyPost = CommunityPost(
                authorName = reply.authorName,
                authorHandle = reply.authorHandle,
                authorRole = reply.authorRole,
                authorLocation = reply.authorLocation,
                content = "💬 Replied: ${reply.content}",
                postType = "QUESTION",
                cropTag = item.cropTag
            )
            context?.let {
                CommunityNotificationHelper.dispatchNewPostNotification(it, dummyReplyPost)
            }
        }
    }

    fun markCommunityAsRead() {
        unreadCommunityCount.intValue = 0
        persist()
    }

    private var simulationIndex = 0

    fun simulateIncomingCommunityPost(context: Context? = appContext) {
        val simulatedIncomingList = listOf(
            CommunityPost(
                authorName = "Sukhwinder Singh",
                authorHandle = "@sukhwinder_pb",
                authorRole = "FARMER",
                authorLocation = "Ludhiana, Punjab",
                content = "🌾 Bumper harvest update! Reaped 23 Quintals/Acre on PBW-550 Wheat using Super Seeder with zero straw burning. Nitrogen efficiency increased by 30%. Happy to advise any farmer brothers!",
                postType = "EXPERIENCE",
                cropTag = "Wheat",
                tags = listOf("#ZeroTillage", "#WheatHarvest", "#FarmerPride"),
                likesCount = 14,
                repostsCount = 5
            ),
            CommunityPost(
                authorName = "Maharastra Agro Mart",
                authorHandle = "@maha_agro_buyer",
                authorRole = "BUYER",
                authorLocation = "Pune Market Yard",
                content = "🤝 SPOT CASH BUYING: Need 40 Tons of Green Moong (Mung bean) & 50 Tons of Chana (Gram). Offering ₹8,200/Qtl for Moong and ₹5,800/Qtl for Chana. Spot weighing, spot RTGS transfer!",
                postType = "BUYER_DEMAND",
                cropTag = "Chickpea / Gram",
                tags = listOf("#BuyerProcurement", "#CashDeal", "#SpotCash"),
                demandQuantity = "50 Tons",
                offeredPrice = "₹5,800 / Quintal",
                buyerContactPhone = "+91 97654 32109",
                likesCount = 31,
                repostsCount = 9
            ),
            CommunityPost(
                authorName = "Anita Deshmukh",
                authorHandle = "@anita_farmer",
                authorRole = "FARMER",
                authorLocation = "Satara, Maharashtra",
                content = "💡 TIP FOR ONION & VEGETABLE GROWERS: Install 15 yellow sticky cards & 10 blue sticky cards per acre. In just 3 days, over 70% of thrips and whiteflies were trapped without a drop of pesticide! Saved ₹1,800 per spray.",
                postType = "GUIDANCE",
                cropTag = "Onion",
                tags = listOf("#OrganicFarming", "#StickyTraps", "#FarmerHacks"),
                likesCount = 47,
                repostsCount = 12
            ),
            CommunityPost(
                authorName = "Global Fresh Agro Exports",
                authorHandle = "@global_fresh_procure",
                authorRole = "BUYER",
                authorLocation = "Nashik / Export Zone",
                content = "🚨 URGENT EXPORT REQUIREMENT: Purchasing 30 Metric Tons of Export-Quality Pomegranate (Bhagwa variety, 250g+ size, spot payment @ ₹135/Kg). Truck pickup directly at orchard. DM or contact immediately!",
                postType = "BUYER_DEMAND",
                cropTag = "Pomegranate",
                tags = listOf("#ExportDeal", "#PomegranateBuyer", "#SpotPayment"),
                demandQuantity = "30 Metric Tons",
                offeredPrice = "₹135 / Kg",
                buyerContactPhone = "+91 91234 56780",
                likesCount = 59,
                repostsCount = 21
            )
        )

        val nextPost = simulatedIncomingList[simulationIndex % simulatedIncomingList.size]
        simulationIndex++
        addCommunityPost(nextPost, context)
    }
}

