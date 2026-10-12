package com.krushiadhaar.app.util

data class IndianLocation(
    val id: String,
    val villageOrTaluka: String,
    val district: String,
    val state: String,
    val latitude: Double,
    val longitude: Double,
    val nearestMandi: String,
    val primaryCrops: List<String>
) {
    val displayName: String
        get() = "$villageOrTaluka, $district • $state"

    val shortName: String
        get() = "$villageOrTaluka, $district"
}

object IndianLocationsCatalog {
    val locations = listOf(
        // Maharashtra
        IndianLocation(
            id = "mh_pune_baramati",
            villageOrTaluka = "Baramati",
            district = "Pune",
            state = "Maharashtra",
            latitude = 18.1517,
            longitude = 74.5774,
            nearestMandi = "Baramati APMC Mandi",
            primaryCrops = listOf("Sugarcane", "Wheat", "Soyabean", "Grapes")
        ),
        IndianLocation(
            id = "mh_nashik_lasalgaon",
            villageOrTaluka = "Lasalgaon",
            district = "Nashik",
            state = "Maharashtra",
            latitude = 20.1476,
            longitude = 74.2255,
            nearestMandi = "Lasalgaon Onion Mandi",
            primaryCrops = listOf("Onion", "Tomato", "Grapes", "Soyabean")
        ),
        IndianLocation(
            id = "mh_nagpur_kalamna",
            villageOrTaluka = "Kalamna",
            district = "Nagpur",
            state = "Maharashtra",
            latitude = 21.1764,
            longitude = 79.1368,
            nearestMandi = "Kalamna APMC Mandi",
            primaryCrops = listOf("Cotton", "Soyabean", "Orange", "Tur Dal")
        ),
        IndianLocation(
            id = "mh_kolhapur_karveer",
            villageOrTaluka = "Karveer",
            district = "Kolhapur",
            state = "Maharashtra",
            latitude = 16.7050,
            longitude = 74.2433,
            nearestMandi = "Kolhapur Jaggery Mandi",
            primaryCrops = listOf("Sugarcane", "Rice", "Soyabean", "Groundnut")
        ),
        IndianLocation(
            id = "mh_amravati",
            villageOrTaluka = "Morshi",
            district = "Amravati",
            state = "Maharashtra",
            latitude = 21.3183,
            longitude = 78.0125,
            nearestMandi = "Amravati APMC",
            primaryCrops = listOf("Cotton", "Soyabean", "Orange", "Wheat")
        ),
        IndianLocation(
            id = "mh_solapur_pandharpur",
            villageOrTaluka = "Pandharpur",
            district = "Solapur",
            state = "Maharashtra",
            latitude = 17.6778,
            longitude = 75.3267,
            nearestMandi = "Pandharpur Mandi",
            primaryCrops = listOf("Pomegranate", "Sugarcane", "Jowar", "Onion")
        ),
        IndianLocation(
            id = "mh_sambhajinagar",
            villageOrTaluka = "Paithan",
            district = "Chh. Sambhajinagar",
            state = "Maharashtra",
            latitude = 19.4797,
            longitude = 75.3854,
            nearestMandi = "Jadhavwadi APMC",
            primaryCrops = listOf("Cotton", "Maize", "Bajra", "Soyabean")
        ),
        IndianLocation(
            id = "mh_latur",
            villageOrTaluka = "Ausa",
            district = "Latur",
            state = "Maharashtra",
            latitude = 18.2500,
            longitude = 76.5000,
            nearestMandi = "Latur Dal & Oilseed Mandi",
            primaryCrops = listOf("Soyabean", "Tur Dal", "Gram", "Sunflower")
        ),
        IndianLocation(
            id = "mh_jalgaon",
            villageOrTaluka = "Raver",
            district = "Jalgaon",
            state = "Maharashtra",
            latitude = 21.2500,
            longitude = 76.0300,
            nearestMandi = "Jalgaon Banana Mandi",
            primaryCrops = listOf("Banana", "Cotton", "Maize", "Groundnut")
        ),

        // Punjab
        IndianLocation(
            id = "pb_ludhiana_jagraon",
            villageOrTaluka = "Jagraon",
            district = "Ludhiana",
            state = "Punjab",
            latitude = 30.7853,
            longitude = 75.4788,
            nearestMandi = "Khanna / Ludhiana Grain Mandi",
            primaryCrops = listOf("Wheat", "Rice", "Mustard", "Potato")
        ),
        IndianLocation(
            id = "pb_bathinda",
            villageOrTaluka = "Rampura Phul",
            district = "Bathinda",
            state = "Punjab",
            latitude = 30.2700,
            longitude = 75.2400,
            nearestMandi = "Bathinda Cotton & Wheat Mandi",
            primaryCrops = listOf("Wheat", "Cotton", "Rice", "Mustard")
        ),

        // Haryana
        IndianLocation(
            id = "hr_karnal_gharaunda",
            villageOrTaluka = "Gharaunda",
            district = "Karnal",
            state = "Haryana",
            latitude = 29.5372,
            longitude = 76.9715,
            nearestMandi = "Karnal Basmati Mandi",
            primaryCrops = listOf("Basmati Rice", "Wheat", "Sugarcane", "Potato")
        ),
        IndianLocation(
            id = "hr_sirsa",
            villageOrTaluka = "Ellenabad",
            district = "Sirsa",
            state = "Haryana",
            latitude = 29.4500,
            longitude = 74.6500,
            nearestMandi = "Sirsa Cotton Mandi",
            primaryCrops = listOf("Cotton", "Wheat", "Mustard", "Guar")
        ),

        // Madhya Pradesh
        IndianLocation(
            id = "mp_indore_sanwer",
            villageOrTaluka = "Sanwer",
            district = "Indore",
            state = "Madhya Pradesh",
            latitude = 22.9754,
            longitude = 75.8286,
            nearestMandi = "Choithram / Indore Mandi",
            primaryCrops = listOf("Soyabean", "Wheat", "Garlic", "Potato")
        ),
        IndianLocation(
            id = "mp_ujjain",
            villageOrTaluka = "Nagda",
            district = "Ujjain",
            state = "Madhya Pradesh",
            latitude = 23.4500,
            longitude = 75.4100,
            nearestMandi = "Ujjain Grain Mandi",
            primaryCrops = listOf("Soyabean", "Wheat", "Gram", "Mustard")
        ),

        // Gujarat
        IndianLocation(
            id = "gj_surat_bardoli",
            villageOrTaluka = "Bardoli",
            district = "Surat",
            state = "Gujarat",
            latitude = 21.1189,
            longitude = 73.1118,
            nearestMandi = "Surat APMC",
            primaryCrops = listOf("Sugarcane", "Rice", "Banana", "Cotton")
        ),
        IndianLocation(
            id = "gj_anand",
            villageOrTaluka = "Petlad",
            district = "Anand",
            state = "Gujarat",
            latitude = 22.4700,
            longitude = 72.8000,
            nearestMandi = "Anand Agricultural Mandi",
            primaryCrops = listOf("Tobacco", "Banana", "Groundnut", "Rice")
        ),
        IndianLocation(
            id = "gj_rajkot",
            villageOrTaluka = "Gondal",
            district = "Rajkot",
            state = "Gujarat",
            latitude = 21.9600,
            longitude = 70.8000,
            nearestMandi = "Gondal Groundnut & Chilli Mandi",
            primaryCrops = listOf("Groundnut", "Cotton", "Chilli", "Sesame")
        ),

        // Rajasthan
        IndianLocation(
            id = "rj_kota",
            villageOrTaluka = "Ramganj Mandi",
            district = "Kota",
            state = "Rajasthan",
            latitude = 24.6500,
            longitude = 75.9400,
            nearestMandi = "Bhamashah Mandi Kota",
            primaryCrops = listOf("Soyabean", "Mustard", "Coriander", "Wheat")
        ),
        IndianLocation(
            id = "rj_ganganagar",
            villageOrTaluka = "Suratgarh",
            district = "Sri Ganganagar",
            state = "Rajasthan",
            latitude = 29.3200,
            longitude = 73.9000,
            nearestMandi = "Ganganagar Grain Mandi",
            primaryCrops = listOf("Wheat", "Mustard", "Cotton", "Gram")
        ),

        // Uttar Pradesh
        IndianLocation(
            id = "up_varanasi",
            villageOrTaluka = "Pindra",
            district = "Varanasi",
            state = "Uttar Pradesh",
            latitude = 25.4800,
            longitude = 82.8400,
            nearestMandi = "Varanasi APMC",
            primaryCrops = listOf("Wheat", "Rice", "Tomato", "Potato")
        ),
        IndianLocation(
            id = "up_muzaffarnagar",
            villageOrTaluka = "Khatauli",
            district = "Muzaffarnagar",
            state = "Uttar Pradesh",
            latitude = 29.2800,
            longitude = 77.7200,
            nearestMandi = "Muzaffarnagar Jaggery & Cane Mandi",
            primaryCrops = listOf("Sugarcane", "Wheat", "Mustard", "Rice")
        ),

        // Karnataka
        IndianLocation(
            id = "ka_bengaluru_rural",
            villageOrTaluka = "Doddaballapur",
            district = "Bengaluru Rural",
            state = "Karnataka",
            latitude = 13.2926,
            longitude = 77.5413,
            nearestMandi = "Yeshwanthpur APMC",
            primaryCrops = listOf("Ragi", "Maize", "Tomato", "Grapes")
        ),
        IndianLocation(
            id = "ka_dharwad",
            villageOrTaluka = "Hubballi",
            district = "Dharwad",
            state = "Karnataka",
            latitude = 15.3647,
            longitude = 75.1240,
            nearestMandi = "Hubballi Cotton & Chilli Mandi",
            primaryCrops = listOf("Cotton", "Byadgi Chilli", "Maize", "Soyabean")
        )
    )

    fun getDefault(): IndianLocation = locations.first()

    fun search(query: String): List<IndianLocation> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return locations
        return locations.filter {
            it.villageOrTaluka.lowercase().contains(q) ||
            it.district.lowercase().contains(q) ||
            it.state.lowercase().contains(q) ||
            it.nearestMandi.lowercase().contains(q) ||
            it.primaryCrops.any { crop -> crop.lowercase().contains(q) }
        }
    }
}
