package com.krushiadhaar.app.assistant

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
            "नमस्ते किसान भाई! मैं कृषि सहायक हूँ। आप बोलकर या लिखकर अपनी फसल, खाद, कीटनाशक या मंडी भाव के बारे में पूछ सकते हैं।"
        currentLanguage.contains("Marathi", true) || currentLanguage.contains("मराठी", true) ->
            "नमस्कार शेतकरी मित्र! मी कृषी सहाय्यक आहे. आपण बोलून किंवा लिहून आपल्या पिकाची माहिती, खते, कीटकनाशके किंवा बाजार भाव विचारू शकता."
        else ->
            "Hello Farmer! I am Krushi Assistant. Ask me anything about crop diseases, fertilizers, irrigation, or market prices in English, Hindi, or Marathi."
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
            "सोयाबीन का आज का मंडी भाव क्या है?",
            "पत्तियों पर पीले धब्बे का क्या इलाज है?"
        )
        currentLanguage.contains("Marathi", true) || currentLanguage.contains("मराठी", true) -> listOf(
            "गव्हाला ३० दिवसांनंतर कोणते खत द्यावे?",
            "आजचा सोयाबीन बाजार भाव काय आहे?",
            "पानांवर पिवळे डाग पडले आहेत, काय उपाय?"
        )
        else -> listOf(
            "What fertilizer for wheat at 30 days?",
            "What is today's soybean mandi rate?",
            "How to treat yellow leaf spots?"
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
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    quickChips.take(2).forEach { chip ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = GreenSurface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, GreenPrimary.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { sendQuery(chip) }
                        ) {
                            Text(
                                text = chip,
                                fontSize = 11.sp,
                                color = GreenDark,
                                maxLines = 1,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
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
                                "कृषि सहायक सोच रहा है..."
                            else if (currentLanguage.contains("Marathi", true) || currentLanguage.contains("मराठी", true))
                                "कृषी सहाय्यक विचार करत आहे..."
                            else
                                "Krushi Assistant is analyzing...",
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

    return when {
        query.contains("fertilizer", true) || query.contains("खाद", true) || query.contains("खत", true) -> {
            if (isHi) "गेहूं में बुवाई के 30 दिनों बाद पहली सिंचाई के साथ 45 किग्रा यूरिया प्रति एकड़ दें। इससे कल्ले अच्छे फूटते हैं।"
            else if (isMr) "गव्हामध्ये पेरणीनंतर ३० दिवसांनी पहिल्या पाण्यासोबत ४५ किलो युरिया प्रति एकरी द्यावा. यामुळे फुटवे जोमदार येतात."
            else "For wheat at 30 days, apply 45 kg Urea per acre with first irrigation to promote vigorous tillering."
        }
        query.contains("mandi", true) || query.contains("मंडी", true) || query.contains("बाजार", true) -> {
            if (isHi) "आज कोल्हापुर और नागपुर मंडी में सोयाबीन का औसत भाव ₹4,850/क्विंटल है, जो कि सरकारी MSP ₹4,600 से अधिक है।"
            else if (isMr) "आज कोल्हापूर व नागपूर बाजार समितीत सोयाबीनचा सरासरी दर ₹४,८५०/क्विंटल असून तो हमीभावापेक्षा जास्त आहे."
            else "Today's average Mandi price for Soybean in Maharashtra is ₹4,850/Qtl, which is above the government MSP of ₹4,600."
        }
        else -> {
            if (isHi) "आपकी फसल के अच्छे उत्पादन के लिए मौसम के अनुसार संतुलित NPK खाद और समय पर सिंचाई करें।"
            else if (isMr) "चांगल्या उत्पादनासाठी हवामानाचा अंदाज घेऊन संतुलित NPK खतांचा वापर आणि वेळेवर पाणी द्या."
            else "For optimal yield, ensure balanced NPK fertilizer application and schedule irrigation according to local weather forecasts."
        }
    }
}
