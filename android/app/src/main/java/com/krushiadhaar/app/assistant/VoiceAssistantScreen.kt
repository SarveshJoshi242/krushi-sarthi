package com.krushiadhaar.app.assistant

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.krushiadhaar.app.GlobalMockData
import com.krushiadhaar.app.ui.theme.*
import com.krushiadhaar.app.util.AppStrings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.*

data class VoiceMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: String = "Just now"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceAssistantScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var currentLanguage by remember { mutableStateOf(GlobalMockData.userLanguage.value) }

    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var isTtsReady by remember { mutableStateOf(false) }

    DisposableEffect(context) {
        val textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isTtsReady = true
            }
        }
        tts = textToSpeech
        onDispose {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
    }

    val initialGreeting = when {
        currentLanguage.contains("Hindi", true) || currentLanguage.contains("हिंदी", true) ->
            "नमस्ते किसान भाई! मैं कृषि आधार सहायक हूँ। आप बोलकर या लिखकर अपनी फसल, खाद, कीटनाशक या मंडी भाव के बारे में पूछ सकते हैं।"
        currentLanguage.contains("Marathi", true) || currentLanguage.contains("मराठी", true) ->
            "नमस्कार शेतकरी मित्र! मी शेतकरी आधार सहायक आहे. आपण बोलून किंवा लिहून आपल्या पिकाची माहिती, खते, कीटकनाशके किंवा बाजार भाव विचारू शकता."
        else ->
            "Hello Farmer! I am Krishi-Aadhar Assistant. Ask me anything about crop diseases, fertilizers, irrigation, or market prices in English, Hindi, or Marathi."
    }

    val messages = remember {
        mutableStateListOf(
            VoiceMessage(text = initialGreeting, isUser = false)
        )
    }

    var inputText by remember { mutableStateOf("") }
    var isListening by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    // Pulsing animation for mic
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isListening) 1.25f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    fun speakText(text: String) {
        if (!isTtsReady) return
        val loc = when {
            currentLanguage.contains("Hindi", true) || currentLanguage.contains("हिंदी", true) -> Locale("hi", "IN")
            currentLanguage.contains("Marathi", true) || currentLanguage.contains("मराठी", true) -> Locale("mr", "IN")
            else -> Locale.ENGLISH
        }
        tts?.language = loc
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "UTTERANCE_ID")
    }

    fun sendQuery(query: String) {
        if (query.isBlank() || isLoading) return
        messages.add(VoiceMessage(text = query, isUser = true))
        inputText = ""
        isLoading = true

        coroutineScope.launch {
            listState.animateScrollToItem(messages.size - 1)
            val reply = withContext(Dispatchers.IO) {
                try {
                    val encodedQuery = URLEncoder.encode(query, "UTF-8")
                    val encodedLang = URLEncoder.encode(currentLanguage, "UTF-8")
                    val url = URL("http://10.0.2.2:8000/voice-assistant")
                    val conn = url.openConnection() as HttpURLConnection
                    conn.requestMethod = "POST"
                    conn.doOutput = true
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                    conn.connectTimeout = 5000
                    conn.readTimeout = 7000

                    val encodedCrop = URLEncoder.encode(com.krushiadhaar.app.GlobalMockData.farmerCrop.value, "UTF-8")
                    val encodedLoc = URLEncoder.encode(com.krushiadhaar.app.GlobalMockData.userLocation.value, "UTF-8")
                    val postData = "query=$encodedQuery&language=$encodedLang&crop=$encodedCrop&location=$encodedLoc"
                    conn.outputStream.use { os ->
                        os.write(postData.toByteArray())
                        os.flush()
                    }

                    if (conn.responseCode == 200) {
                        val responseStr = conn.inputStream.bufferedReader().readText()
                        val json = JSONObject(responseStr)
                        json.optString("response", "Thank you for asking. Ensure proper irrigation and NPK application.")
                    } else {
                        getFallbackAnswer(query, currentLanguage)
                    }
                } catch (e: Exception) {
                    getFallbackAnswer(query, currentLanguage)
                }
            }

            isLoading = false
            messages.add(VoiceMessage(text = reply, isUser = false))
            listState.animateScrollToItem(messages.size - 1)
            speakText(reply)
        }
    }

    val quickChips = when {
        currentLanguage.contains("Hindi", true) || currentLanguage.contains("हिंदी", true) -> listOf(
            "गेहूं में 30 दिन बाद कौन सी खाद डालें?",
            "सोयाबीन और कपास का आज का मंडी भाव क्या है?",
            "पत्तियों पर पीले धब्बे और कीड़े का क्या इलाज है?",
            "ड्रिप सिंचाई में पानी देने का सही समय क्या है?",
            "ड्रिप और सोलर पंप पर सरकारी सब्सिडी कैसे मिलेगी?",
            "फसल सीधे व्यापारियों को बिना दलाल के कैसे बेचें?"
        )
        currentLanguage.contains("Marathi", true) || currentLanguage.contains("मराठी", true) -> listOf(
            "गव्हाला ३० दिवसांनंतर कोणते खत द्यावे?",
            "सोयाबीन आणि कापसाचा आजचा बाजार भाव काय आहे?",
            "पानांवरील करपा आणि कीड नियंत्रणासाठी काय उपाय?",
            "ठिबक सिंचनाने पाणी देण्याची योग्य वेळ कोणती?",
            "ठिबक आणि सौर पंपावर सरकारी अनुदान कसे मिळेल?",
            "माझा शेतमाल थेट खरेदीदारांना कसा विकावा?"
        )
        else -> listOf(
            "What fertilizer for wheat at 30 days?",
            "What is today's soybean and cotton mandi rate?",
            "How to treat yellow leaf spots and blight?",
            "What is the best timing for drip irrigation?",
            "How to get government subsidy for drip & solar pump?",
            "How to pitch crop to direct buyers without middlemen?"
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = AppStrings.voiceAssistant,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "English • हिंदी • मराठी (Gemini AI)",
                            fontSize = 11.sp,
                            color = GreenPrimary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                actions = {
                    // Language Switcher Dropdown / Action
                    Row(
                        modifier = Modifier.padding(end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val langs = listOf("English", "हिंदी", "मराठी")
                        langs.forEach { lang ->
                            val isSelected = currentLanguage.equals(lang, true) ||
                                (lang == "हिंदी" && currentLanguage.contains("Hindi", true)) ||
                                (lang == "मराठी" && currentLanguage.contains("Marathi", true))
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) GreenPrimary else Color(0xFFF0F0F0),
                                modifier = Modifier
                                    .padding(horizontal = 2.dp)
                                    .clickable {
                                        currentLanguage = lang
                                        GlobalMockData.updateLanguage(lang)
                                    }
                            ) {
                                Text(
                                    text = lang,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else TextSecondary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Quick suggested question chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    quickChips.forEach { chip ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = GreenSurface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, GreenPrimary.copy(alpha = 0.5f)),
                            modifier = Modifier.clickable { sendQuery(chip) }
                        ) {
                            Text(
                                text = chip,
                                fontSize = 12.sp,
                                color = GreenDark,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Input Bar with Mic button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = {
                            Text(
                                if (currentLanguage.contains("Hindi", true) || currentLanguage.contains("हिंदी", true))
                                    "पूछें या माइक दबाकर बोलें..."
                                else if (currentLanguage.contains("Marathi", true) || currentLanguage.contains("मराठी", true))
                                    "विचारा किंवा माइक दाबून बोला..."
                                else
                                    "Type question or tap mic to speak...",
                                fontSize = 13.sp
                            )
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenPrimary,
                            unfocusedBorderColor = Color.LightGray
                        ),
                        trailingIcon = {
                            if (inputText.isNotBlank()) {
                                IconButton(onClick = { sendQuery(inputText) }) {
                                    Icon(Icons.Default.Send, contentDescription = "Send", tint = GreenPrimary)
                                }
                            }
                        }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Floating Voice Mic
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(if (isListening) Color(0xFFE53935) else GreenPrimary)
                            .clickable {
                                isListening = !isListening
                                if (isListening) {
                                    // Pick one of the sample prompt to simulate voice recognition
                                    coroutineScope.launch {
                                        kotlinx.coroutines.delay(1200)
                                        isListening = false
                                        val sampleVoice = quickChips.random()
                                        sendQuery(sampleVoice)
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Speak",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
        },
        containerColor = AppBackground
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages) { msg ->
                ChatBubble(message = msg, onSpeak = { speakText(msg.text) })
            }

            if (isLoading) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = GreenPrimary, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (currentLanguage.contains("Hindi", true) || currentLanguage.contains("हिंदी", true))
                                "कृषी आधार सहायक सोच रहा है..."
                            else if (currentLanguage.contains("Marathi", true) || currentLanguage.contains("मराठी", true))
                                "शेतकरी आधार सहायक विचार करत आहे..."
                            else
                                "Krishi-Aadhar Assistant is analyzing...",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(message: VoiceMessage, onSpeak: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!message.isUser) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8F5E9)),
                contentAlignment = Alignment.Center
            ) {
                Text("🌾", fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Card(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.isUser) 16.dp else 4.dp,
                bottomEnd = if (message.isUser) 4.dp else 16.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (message.isUser) GreenPrimary else Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = message.text,
                    color = if (message.isUser) Color.White else TextPrimary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )

                if (!message.isUser) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onSpeak, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.VolumeUp, contentDescription = "Listen", tint = GreenPrimary, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

private fun getFallbackAnswer(query: String, language: String): String {
    val isHi = language.contains("Hindi", true) || language.contains("हिंदी", true)
    val isMr = language.contains("Marathi", true) || language.contains("मराठी", true)
    val q = query.lowercase()

    return when {
        // 1. Disease / Pest / Medicine
        q.contains("disease") || q.contains("pest") || q.contains("leaf") || q.contains("spray") || 
        q.contains("cure") || q.contains("medicine") || q.contains("yellow") || q.contains("blight") || 
        q.contains("कीट") || q.contains("रोग") || q.contains("दवाई") || q.contains("स्प्रे") || 
        q.contains("धब्बे") || q.contains("पीले") || q.contains("कीड") || q.contains("बुरशी") || 
        q.contains("करपा") || q.contains("फवारणी") -> {
            if (isHi) "फसल में कीट व फफूंद (धब्बे या झुलसा) के नियंत्रण के लिए प्रति एकड़ 500 ग्राम मैंकोज़ेब या 400 मिली क्लोरपायरीफॉस 200 लीटर पानी में मिलाकर छिड़कें। जैविक रोकथाम हेतु 5 मिली नीम तेल प्रति लीटर पानी में मिलाकर स्प्रे करें।"
            else if (isMr) "पिकावरील कीड व करपा रोगाच्या नियंत्रणासाठी २०० लिटर पाण्यात ५०० ग्रॅम मॅन्कोझेब किंवा ४०० मिली क्लोरपायरीफॉस मिसळून फवारणी करा. सेंद्रिय नियंत्रणासाठी ५ मिली निंबोळी अर्क प्रति लिटर पाण्यात फवारावा."
            else "For leaf spots or pest blight, spray Mancozeb 75% WP @ 2.5g/L or Chlorpyrifos 20% EC @ 2ml/L water. For organic treatment, spray cold-pressed Neem oil @ 5ml/L."
        }

        // 2. Soil / Fertilizer / NPK / Bags
        q.contains("fertilizer") || q.contains("urea") || q.contains("dap") || q.contains("mop") || 
        q.contains("npk") || q.contains("soil") || q.contains("bag") || q.contains("खाद") || 
        q.contains("खत") || q.contains("यूरिया") || q.contains("डीएपी") || q.contains("माती") -> {
            if (isHi) "संतुलित पैदावार के लिए बुवाई के समय प्रति एकड़ 1 बोरी DAP और आधा बोरी MOP दें। 30 दिन बाद पहली सिंचाई के समय 1 बोरी यूरिया का टॉप-ड्रेसिंग करें।"
            else if (isMr) "चांगल्या उत्पादनासाठी पेरणीवेळी एकरी १ गोणी DAP आणि अर्धी गोणी MOP द्यावे. ३० दिवसांनी पहिल्या सिंचनासोबत १ गोणी युरिया टाकावा."
            else "Apply 1 bag (50kg) DAP + 0.5 bag (25kg) MOP as basal application at sowing. Follow with 1 bag Urea top-dressing at 30 days along with first irrigation."
        }

        // 3. Smart Irrigation / Water
        q.contains("irrigation") || q.contains("water") || q.contains("drip") || q.contains("timing") || 
        q.contains("सिंचाई") || q.contains("पानी") || q.contains("ड्रिप") || q.contains("सिंचन") || q.contains("पाणी") -> {
            if (isHi) "सिंचाई हमेशा सुबह 6 से 9 बजे के बीच या शाम को करें। ड्रिप से 1.5 से 2 घंटे पानी देना सर्वोत्तम है। दोपहर की तेज धूप में पानी देने से 40% वाष्पीकरण का नुकसान होता है।"
            else if (isMr) "पाणी नेहमी सकाळी ६ ते ९ दरम्यान किंवा संध्याकाळी द्यावे. ठिबक सिंचनाने १.५ ते २ तास पाणी दिल्यास पाण्याची बचत होते. दुपारच्या उन्हात पाणी देणे टाळावे."
            else "Irrigate via drip for 1.5 to 2 hours between 6:00 AM - 9:00 AM or after 5:00 PM. Avoid afternoon watering to prevent up to 40% evaporation losses."
        }

        // 4. Mandi Prices / Market / MSP
        q.contains("mandi") || q.contains("market") || q.contains("price") || q.contains("rate") || 
        q.contains("msp") || q.contains("मंडी") || q.contains("भाव") || q.contains("बाजार") || 
        q.contains("दर") || q.contains("किंमत") -> {
            if (isHi) "आज प्रमुख मंडियों में सोयाबीन का औसत भाव ₹4,850/क्विंटल (MSP ₹4,600), कपास ₹7,250/क्विंटल (MSP ₹6,620) और गेहूं ₹2,350/क्विंटल चल रहा है।"
            else if (isMr) "आजच्या बाजार समितीत सोयाबीन सरासरी ₹४,८५०/क्विंटल (हमीभाव ₹४,६००), कापूस ₹७,२५०/क्विंटल आणि गहू ₹२,३५०/क्विंटल दराने विकला जात आहे."
            else "Today's APMC modal mandi rates: Soybean ₹4,850/Qtl (above MSP ₹4,600), Cotton ₹7,250/Qtl (MSP ₹6,620), and Wheat ₹2,350/Qtl."
        }

        // 5. Government Schemes & Subsidies
        q.contains("scheme") || q.contains("subsidy") || q.contains("pmkisan") || q.contains("pm-kisan") || 
        q.contains("insurance") || q.contains("fasal bima") || q.contains("योजना") || q.contains("सब्सिडी") || 
        q.contains("अनुदान") || q.contains("विमा") || q.contains("बीमा") -> {
            if (isHi) "प्रधानमंत्री किसान सम्मान निधि (PM-KISAN) से प्रति वर्ष ₹6,000 की सहायता और फसल बीमा योजना (PMFBY) से सूखा व बाढ़ से सुरक्षा मिलती है। ड्रिप सिंचाई पर 55% से 80% सरकारी सब्सिडी उपलब्ध है।"
            else if (isMr) "पीएम-किसान योजनेतून दरवर्षी ₹६,००० चा लाभ मिळतो. ठिबक सिंचनासाठी महाडीबीटीवर ५५% ते ८०% सरकारी अनुदान आणि १ रुपयात पीक विमा उपलब्ध आहे."
            else "PM-KISAN provides ₹6,000 yearly financial support. PMFBY offers crop insurance against natural calamities, and PMKSY offers 55% to 80% government subsidy on drip irrigation sets."
        }

        // 6. Selling Crop / Pitch Crop / Buyers
        q.contains("pitch") || q.contains("sell") || q.contains("buyer") || q.contains("marketplace") || 
        q.contains("व्यापारी") || q.contains("बेचें") || q.contains("विक्री") || q.contains("खरेदीदार") -> {
            if (isHi) "कृषी आधार के 'फसल बेचें' (Pitch Crop) विकल्प से आप अपनी फसल का ग्रेड और मात्रा डालकर बिना किसी दलाल के सीधे सत्यापित व्यापारियों को 15% अधिक मूल्य पर बेच सकते हैं।"
            else if (isMr) "शेतकरी आधार च्या 'पीक विक्री' (Pitch Crop) पर्यायावर जाऊन पिकाची प्रत व प्रमाण नोंदवा. दलालांशिवाय थेट प्रमाणित खरेदीदारांकडून चांगला दर मिळवा."
            else "List your harvest grade and tonnage in the Krishi-Aadhar 'Pitch Crop' tab to connect directly with verified institutional buyers without middlemen commission."
        }

        else -> {
            if (isHi) "कृषी आधार सहायक: अपनी फसल के बेहतर उत्पादन के लिए मौसम आधारित सिंचाई, संतुलित NPK खाद और समय पर जैविक कीटनाशक का प्रयोग करें।"
            else if (isMr) "शेतकरी आधार सहायक: भरघोस उत्पादनासाठी वेळेवर पाणी, संतुलित खते आणि रोग कीड संरक्षणासाठी शिफारशीत फवारणी करा."
            else "Krishi-Aadhar Assistant: Ensure balanced NPK fertilizer application, morning drip irrigation, and timely plant protection for maximum yield."
        }
    }
}
