package com.krushiadhaar.app.util

import com.krushiadhaar.app.GlobalMockData

object AppStrings {
    private fun isHindi(): Boolean =
        GlobalMockData.userLanguage.value.contains("Hindi", true) ||
        GlobalMockData.userLanguage.value.contains("हिंदी", true)

    private fun isMarathi(): Boolean =
        GlobalMockData.userLanguage.value.contains("Marathi", true) ||
        GlobalMockData.userLanguage.value.contains("मराठी", true)

    val appName: String
        get() = when {
            isHindi() -> "किसान मित्र"
            isMarathi() -> "कृषी मित्र"
            else -> "Kisaan Mitra"
        }

    val greeting: String
        get() = when {
            isHindi() -> "नमस्ते, किसान भाई 👋"
            isMarathi() -> "नमस्कार, शेतकरी मित्र 👋"
            else -> "Hi, Farmer 👋"
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

    val languageEnglish = "English"
    val languageHindi = "हिंदी"
    val languageMarathi = "मराठी"
}
