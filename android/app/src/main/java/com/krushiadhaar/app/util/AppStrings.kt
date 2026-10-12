package com.krushiadhaar.app.util

import com.krushiadhaar.app.GlobalMockData

object AppStrings {
    fun isHindi(): Boolean =
        GlobalMockData.userLanguage.value.contains("Hindi", true) ||
        GlobalMockData.userLanguage.value.contains("हिंदी", true)

    fun isMarathi(): Boolean =
        GlobalMockData.userLanguage.value.contains("Marathi", true) ||
        GlobalMockData.userLanguage.value.contains("मराठी", true)

    val appName: String
        get() = when {
            isHindi() -> "किसान मित्र"
            isMarathi() -> "कृषी मित्र"
            else -> "Kisaan Mitra"
        }

    val hello: String
        get() = when {
            isHindi() -> "नमस्ते"
            isMarathi() -> "नमस्कार"
            else -> "Hi"
        }

    val farmerRole: String
        get() = when {
            isHindi() -> "किसान"
            isMarathi() -> "शेतकरी"
            else -> "Farmer"
        }

    val farmerGreeting: String
        get() = when {
            isHindi() -> "किसान भाई"
            isMarathi() -> "शेतकरी मित्र"
            else -> "Farmer"
        }

    val greeting: String
        get() = when {
            isHindi() -> "नमस्ते, किसान भाई 👋"
            isMarathi() -> "नमस्कार, शेतकरी मित्र 👋"
            else -> "Hi, Farmer 👋"
        }

    val topCropRecommendations: String
        get() = when {
            isHindi() -> "शीर्ष फसल सिफारिशें"
            isMarathi() -> "अव्वल पीक शिफारसी"
            else -> "Top Crop Recommendations"
        }

    val farmTools: String
        get() = when {
            isHindi() -> "कृषि उपकरण व सेवाएं"
            isMarathi() -> "शेती साधने व सेवा"
            else -> "Farm Tools & Services"
        }

    val voiceAssistant: String
        get() = when {
            isHindi() -> "कृषि आवाज़ सहायक"
            isMarathi() -> "कृषी आवाज सहाय्यक"
            else -> "AI Voice Assistant"
        }

    val mandiPrices: String
        get() = when {
            isHindi() -> "मंडी भाव व रुझान"
            isMarathi() -> "बाजार भाव (मंडी)"
            else -> "APMC Mandi Prices"
        }

    val soilHealth: String
        get() = when {
            isHindi() -> "मृदा स्वास्थ्य (NPK)"
            isMarathi() -> "माती आरोग्य (NPK)"
            else -> "Soil Health & NPK"
        }

    val smartIrrigation: String
        get() = when {
            isHindi() -> "स्मार्ट सिंचाई कैलेंडर"
            isMarathi() -> "स्मार्ट सिंचन कॅलेंडर"
            else -> "Smart Irrigation"
        }

    val pdfReport: String
        get() = when {
            isHindi() -> "फार्म हेल्थ रिपोर्ट PDF"
            isMarathi() -> "शेती अहवाल PDF"
            else -> "Farm PDF Report"
        }

    val weatherLabel: String
        get() = when {
            isHindi() -> "मौसम का पूर्वानुमान"
            isMarathi() -> "हवामान अंदाज"
            else -> "Live Weather Forecast"
        }

    val topRecommendations: String
        get() = when {
            isHindi() -> "शीर्ष अनुशंसित फसलें"
            isMarathi() -> "सर्वोत्तम पीक शिफारसी"
            else -> "Top Crop Recommendations"
        }

    val pestDetection: String
        get() = when {
            isHindi() -> "कीट व रोग पहचान"
            isMarathi() -> "कीड व रोग निदान"
            else -> "Pest & Disease Diagnosis"
        }

    val walletTitle: String
        get() = when {
            isHindi() -> "किसान वॉलेट"
            isMarathi() -> "शेतकरी वॉलेट"
            else -> "Farmer Wallet"
        }

    val schemesTitle: String
        get() = when {
            isHindi() -> "सरकारी योजनाएं"
            isMarathi() -> "सरकारी योजना"
            else -> "Government Schemes"
        }

    // Auth & Onboarding Strings
    val step1Subtitle: String
        get() = when {
            isHindi() -> "चरण 1 / 2"
            isMarathi() -> "टप्पा 1 / 2"
            else -> "Step 1 of 2"
        }

    val signInSubtitle: String
        get() = when {
            isHindi() -> "लॉगिन करें"
            isMarathi() -> "साइन इन करा"
            else -> "Sign In"
        }

    val accountDetailsTitle: String
        get() = when {
            isHindi() -> "खाता विवरण"
            isMarathi() -> "खाते तपशील"
            else -> "Account Details"
        }

    val welcomeBackTitle: String
        get() = when {
            isHindi() -> "वापसी पर स्वागत है"
            isMarathi() -> "पुन्हा स्वागत आहे"
            else -> "Welcome Back"
        }

    val nameLabel: String
        get() = when {
            isHindi() -> "पूरा नाम"
            isMarathi() -> "पूर्ण नाव"
            else -> "Full Name"
        }

    val phoneLabel: String
        get() = when {
            isHindi() -> "मोबाइल नंबर (10 अंक)"
            isMarathi() -> "मोबाईल नंबर (10 अंक)"
            else -> "Phone Number (10 digits)"
        }

    val passwordLabel: String
        get() = when {
            isHindi() -> "पासवर्ड"
            isMarathi() -> "पासवर्ड"
            else -> "Password"
        }

    val locationSelectLabel: String
        get() = when {
            isHindi() -> "कृषि क्षेत्र / स्थान चुनें"
            isMarathi() -> "शेतीचे ठिकाण / गाव निवडा"
            else -> "Select Agricultural Hub / Village"
        }

    val customVillageLabel: String
        get() = when {
            isHindi() -> "विशिष्ट गाँव का नाम (वैकल्पिक)"
            isMarathi() -> "गावाचे नाव (पर्यायी)"
            else -> "Specific Village Name (Optional)"
        }

    val preferredLanguageLabel: String
        get() = when {
            isHindi() -> "पसंदीदा भाषा"
            isMarathi() -> "पसंतीची भाषा"
            else -> "Preferred Language"
        }

    val nextAddFarmBtn: String
        get() = when {
            isHindi() -> "आगे बढ़ें: खेत जोड़ें ➔"
            isMarathi() -> "पुढे: शेत जोडा ➔"
            else -> "Next: Add Farm ➔"
        }

    val loginAsFarmerBtn: String
        get() = when {
            isHindi() -> "किसान के रूप में लॉगिन करें ➔"
            isMarathi() -> "शेतकरी म्हणून लॉगिन करा ➔"
            else -> "Login as Farmer ➔"
        }

    val loginAsBuyerBtn: String
        get() = when {
            isHindi() -> "व्यापारी/खरीदार लॉगिन ➔"
            isMarathi() -> "खरेदीदार म्हणून लॉगिन ➔"
            else -> "Login as Buyer ➔"
        }

    val alreadyHaveAccountPrompt: String
        get() = when {
            isHindi() -> "पहले से खाता है? लॉगिन करें"
            isMarathi() -> "आधीच खाते आहे का? साइन इन करा"
            else -> "Already have an account? Sign In"
        }

    val dontHaveAccountPrompt: String
        get() = when {
            isHindi() -> "खाता नहीं है? नया पंजीकरण करें"
            isMarathi() -> "खाते नाही का? नवीन नोंदणी करा"
            else -> "Don't have an account? Sign Up"
        }

    // Step 2 Strings
    val step2Subtitle: String
        get() = when {
            isHindi() -> "चरण 2 / 2"
            isMarathi() -> "टप्पा 2 / 2"
            else -> "Step 2 of 2"
        }

    val addYourFarmTitle: String
        get() = when {
            isHindi() -> "अपने खेत का विवरण दें"
            isMarathi() -> "आपल्या शेताचा तपशील"
            else -> "Add Your Farm"
        }

    val stateLabel: String
        get() = when {
            isHindi() -> "राज्य"
            isMarathi() -> "राज्य"
            else -> "State"
        }

    val districtLabel: String
        get() = when {
            isHindi() -> "ज़िला"
            isMarathi() -> "जिल्हा"
            else -> "District"
        }

    val cropTypeLabel: String
        get() = when {
            isHindi() -> "बोई गई फसल"
            isMarathi() -> "लागवड केलेले पीक"
            else -> "Sown Crop Type"
        }

    val farmAreaLabel: String
        get() = when {
            isHindi() -> "खेत का कुल क्षेत्रफल"
            isMarathi() -> "शेताचे एकूण क्षेत्र"
            else -> "Farm Area"
        }

    val acresUnit: String
        get() = when {
            isHindi() -> "एकड़"
            isMarathi() -> "एकर"
            else -> "Acres"
        }

    val hectaresUnit: String
        get() = when {
            isHindi() -> "हेक्टेयर"
            isMarathi() -> "हेक्टर"
            else -> "Hectares"
        }

    val analyzeMyFarmBtn: String
        get() = when {
            isHindi() -> "फार्म विश्लेषण शुरू करें 🚀"
            isMarathi() -> "शेती विश्लेषण सुरू करा 🚀"
            else -> "Analyze My Farm 🚀"
        }

    val searchLocationHint: String
        get() = when {
            isHindi() -> "गाँव, ज़िला या राज्य खोजें..."
            isMarathi() -> "गाव, जिल्हा किंवा राज्य शोधा..."
            else -> "Search village, district, state..."
        }

    val languageEnglish = "English"
    val languageHindi = "हिंदी"
    val languageMarathi = "मराठी"
}
