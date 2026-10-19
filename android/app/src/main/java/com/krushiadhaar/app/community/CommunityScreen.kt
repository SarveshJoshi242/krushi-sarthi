package com.krushiadhaar.app.community

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.krushiadhaar.app.GlobalMockData
import com.krushiadhaar.app.ui.theme.*
import com.krushiadhaar.app.util.AppStrings
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    onNavigateBack: () -> Unit,
    onNavigateToChat: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Mark unread posts as read when entering
    LaunchedEffect(Unit) {
        GlobalMockData.markCommunityAsRead()
    }

    // In-app alert banner state
    var activeBannerPost by remember { mutableStateOf<CommunityPost?>(null) }
    LaunchedEffect(Unit) {
        CommunityNotificationHelper.inAppNotificationFlow.collect { post ->
            activeBannerPost = post
            delay(5000)
            if (activeBannerPost?.id == post.id) {
                activeBannerPost = null
            }
        }
    }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("🌟 All Feeds", "🌾 Farmer Guides", "🤝 Buyer Demands", "👤 My Posts")

    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    var showComposeDialog by remember { mutableStateOf(false) }
    var activeThreadPost by remember { mutableStateOf<CommunityPost?>(null) }

    val allPosts = GlobalMockData.communityPosts

    val filteredPosts = remember(allPosts.toList(), selectedTabIndex, searchQuery) {
        val baseList = when (selectedTabIndex) {
            1 -> allPosts.filter { it.postType == "GUIDANCE" || it.postType == "EXPERIENCE" || it.authorRole == "FARMER" || it.authorRole == "EXPERT" }
            2 -> allPosts.filter { it.postType == "BUYER_DEMAND" || it.authorRole == "BUYER" }
            3 -> allPosts.filter { it.authorName == GlobalMockData.farmerName.value || it.authorHandle.contains(GlobalMockData.farmerName.value.filter { c -> c.isLetter() }.take(6), ignoreCase = true) }
            else -> allPosts
        }

        if (searchQuery.isBlank()) {
            baseList
        } else {
            baseList.filter { post ->
                post.content.contains(searchQuery, ignoreCase = true) ||
                post.authorName.contains(searchQuery, ignoreCase = true) ||
                post.authorLocation.contains(searchQuery, ignoreCase = true) ||
                (post.cropTag?.contains(searchQuery, ignoreCase = true) == true) ||
                post.tags.any { it.contains(searchQuery, ignoreCase = true) }
            }
        }
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.fillMaxWidth().background(Color.White)) {
                TopAppBar(
                    title = {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = AppStrings.community,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 19.sp,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(GreenSurface)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (AppStrings.isHindi()) "किसान मंच" else if (AppStrings.isMarathi()) "शेतकरी मंच" else "Community",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GreenDark
                                    )
                                }
                            }
                            Text(
                                text = if (AppStrings.isHindi()) "किसान व व्यापारी मंच" else if (AppStrings.isMarathi()) "शेतकरी व खरेदीदार मंच" else "Farmer & Buyer Forum",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                        }
                    },
                    actions = {
                        // Search Toggle Button
                        IconButton(onClick = { isSearchActive = !isSearchActive }) {
                            Icon(
                                if (isSearchActive) Icons.Default.Close else Icons.Default.Search,
                                contentDescription = "Search",
                                tint = TextPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )

                // Search Bar Input
                AnimatedVisibility(visible = isSearchActive) {
                    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search #hashtags, crops, buyers, tips...", fontSize = 13.sp) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GreenPrimary) },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondary)
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GreenPrimary,
                                unfocusedBorderColor = Color(0xFFE0E0E0),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color(0xFFF9FBF9)
                            )
                        )
                    }
                }

                // Community Category Tab Row
                ScrollableTabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color.White,
                    contentColor = GreenPrimary,
                    edgePadding = 16.dp,
                    divider = { Divider(color = Color(0xFFEEEEEE), thickness = 1.dp) }
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 13.sp,
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selectedTabIndex == index) GreenPrimary else TextSecondary
                                )
                            }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showComposeDialog = true },
                containerColor = GreenPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                elevation = FloatingActionButtonDefaults.elevation(6.dp)
            ) {
                Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Add, contentDescription = "Post")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Post", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        },
        containerColor = AppBackground
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                // Top Quick Post Composer Card
                item {
                    QuickPostComposerCard(
                        authorName = GlobalMockData.farmerName.value,
                        userRole = GlobalMockData.userRole.value,
                        onClick = { showComposeDialog = true }
                    )
                }

                // Community Notification Banner Notice
                item {
                    CommunityHeaderNotice()
                }

                if (filteredPosts.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Forum, contentDescription = null, tint = GreenPrimary.copy(alpha = 0.5f), modifier = Modifier.size(56.dp))
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("No posts found in this feed", fontWeight = FontWeight.SemiBold, color = TextSecondary)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Be the first farmer or buyer to share!", fontSize = 13.sp, color = TextSecondary.copy(alpha = 0.8f))
                            }
                        }
                    }
                } else {
                    items(filteredPosts, key = { it.id }) { post ->
                        CommunityPostCard(
                            post = post,
                            onLikeClick = { GlobalMockData.toggleLikePost(post.id) },
                            onRepostClick = {
                                GlobalMockData.toggleRepostPost(post.id)
                                Toast.makeText(context, if (!post.isReposted) "Reposted to your feed!" else "Removed repost", Toast.LENGTH_SHORT).show()
                            },
                            onCommentClick = { activeThreadPost = post },
                            onBookmarkClick = {
                                GlobalMockData.toggleBookmarkPost(post.id)
                                Toast.makeText(context, if (!post.isBookmarked) "Saved to bookmarks" else "Removed bookmark", Toast.LENGTH_SHORT).show()
                            },
                            onShareClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Krishi Post", "${post.authorName} on Krishi Charcha:\n\n${post.content}")
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Copied post link & text to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            onChatClick = { onNavigateToChat(post.authorName) }
                        )
                        Divider(color = Color(0xFFEEEEEE), thickness = 1.dp)
                    }
                }
            }

            // In-App Heads-Up Notification Banner
            activeBannerPost?.let { post ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .align(Alignment.TopCenter)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                activeThreadPost = post
                                activeBannerPost = null
                            },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = GreenDark),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "🔔 New Post: ${post.authorName} (${if (post.authorRole == "BUYER") "Buyer" else "Farmer"})",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = post.content,
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.9f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            TextButton(
                                onClick = {
                                    activeThreadPost = post
                                    activeBannerPost = null
                                }
                            ) {
                                Text("VIEW", color = Color(0xFF81C784), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            IconButton(onClick = { activeBannerPost = null }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    // Compose Post Dialog / Modal
    if (showComposeDialog) {
        ComposeCommunityPostDialog(
            onDismiss = { showComposeDialog = false },
            onPostCreated = { post ->
                GlobalMockData.addCommunityPost(post, context)
                showComposeDialog = false
                Toast.makeText(context, "🌾 Shared to Krishi Charcha community!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Threaded Comments / Reply Sheet Dialog
    activeThreadPost?.let { post ->
        CommunityThreadDialog(
            post = post,
            onDismiss = { activeThreadPost = null },
            onAddReply = { replyText ->
                val newReply = CommunityReply(
                    postId = post.id,
                    authorName = GlobalMockData.farmerName.value,
                    authorHandle = "@" + GlobalMockData.farmerName.value.filter { it.isLetter() }.lowercase(),
                    authorRole = GlobalMockData.userRole.value,
                    authorLocation = GlobalMockData.userLocation.value,
                    content = replyText,
                    timestampText = "Just now"
                )
                GlobalMockData.addReplyToPost(post.id, newReply, context)
                Toast.makeText(context, "Reply added to discussion!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun QuickPostComposerCard(
    authorName: String,
    userRole: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (userRole == "BUYER") OrangePrimary else GreenPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = authorName.take(2).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFFF3F7F3))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = if (userRole == "BUYER") "Post buyer demand or procurement quote..." else "Share your experience or guide fellow farmers...",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickTagChip(label = "🌾 Share Experience", color = GreenPrimary)
                QuickTagChip(label = "💡 Practical Tip", color = Color(0xFF1976D2))
                QuickTagChip(label = "🤝 Buyer Demand", color = OrangePrimary)
                QuickTagChip(label = "❓ Ask Question", color = Color(0xFF7B1FA2))
            }
        }
    }
}

@Composable
fun QuickTagChip(label: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = color,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}

@Composable
fun CommunityHeaderNotice() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFE8F5E9),
        border = BorderStroke(1.dp, Color(0xFFC8E6C9))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Campaign, contentDescription = null, tint = GreenDark, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (AppStrings.isHindi()) "लाइव किसान-व्यापारी नेटवर्क" else if (AppStrings.isMarathi()) "थेट शेतकरी-खरेदीदार नेटवर्क" else "Live Farmer-Buyer Network",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = GreenDark
                )
                Text(
                    text = if (AppStrings.isHindi()) "नया संदेश या मांग पोस्ट होते ही आपको तुरंत सूचना मिलेगी।" else if (AppStrings.isMarathi()) "नवीन सल्ला किंवा मागणी पोस्ट होताच त्वरित सूचना मिळेल." else "Instant notifications sent whenever a new experience, tip or demand is posted.",
                    fontSize = 11.sp,
                    color = TextPrimary.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun CommunityPostCard(
    post: CommunityPost,
    onLikeClick: () -> Unit,
    onRepostClick: () -> Unit,
    onCommentClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    onShareClick: () -> Unit,
    onChatClick: () -> Unit
) {
    val isBuyer = post.authorRole.equals("BUYER", ignoreCase = true)
    val isExpert = post.authorRole.equals("EXPERT", ignoreCase = true)

    val avatarBg = when {
        isBuyer -> OrangePrimary
        isExpert -> Color(0xFF7B1FA2)
        else -> GreenPrimary
    }

    val roleBadgeText = when {
        isBuyer -> "🤝 Buyer"
        isExpert -> "🔬 Agri Expert"
        else -> "🌾 Farmer"
    }

    val roleBadgeBg = when {
        isBuyer -> Color(0xFFFFF3E0)
        isExpert -> Color(0xFFF3E5F5)
        else -> GreenSurface
    }

    val roleBadgeColor = when {
        isBuyer -> OrangePrimary
        isExpert -> Color(0xFF7B1FA2)
        else -> GreenDark
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Author Avatar Circle
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(avatarBg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = post.authorAvatarInitials,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Post Body Column
            Column(modifier = Modifier.weight(1f)) {
                // Author Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        Text(
                            text = post.authorName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (post.isVerified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                Icons.Default.Verified,
                                contentDescription = "Verified",
                                tint = if (isBuyer) OrangePrimary else GreenPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Role Chip
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(roleBadgeBg)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = roleBadgeText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = roleBadgeColor
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = post.timestampText,
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                // Handle & Location Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = post.authorHandle,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (post.authorLocation.isNotBlank()) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("•", fontSize = 11.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(11.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = post.authorLocation,
                            fontSize = 11.sp,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Crop & Category Badges
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    post.cropTag?.let { crop ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFE8F5E9),
                            border = BorderStroke(0.5.dp, GreenPrimary)
                        ) {
                            Text("🌱 $crop", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GreenDark, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }

                    val typeLabel = when (post.postType) {
                        "BUYER_DEMAND" -> "🤝 Buyer Demand"
                        "GUIDANCE" -> "💡 Farmer Guide"
                        "QUESTION" -> "❓ Question"
                        else -> "🌾 Experience"
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF5F5F5)
                    ) {
                        Text(typeLabel, fontSize = 11.sp, color = TextSecondary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }

                // Buyer Demand Special Container (if Buyer Demand)
                if (post.postType == "BUYER_DEMAND" && (!post.demandQuantity.isNullOrBlank() || !post.offeredPrice.isNullOrBlank())) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFFFF8E1),
                        border = BorderStroke(1.dp, Color(0xFFFFE082)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocalShipping, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Buyer Procurement Offer", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = OrangePrimary)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                post.demandQuantity?.let {
                                    Column {
                                        Text("Required Qty", fontSize = 10.sp, color = TextSecondary)
                                        Text(it, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    }
                                }
                                post.offeredPrice?.let {
                                    Column {
                                        Text("Offered Price", fontSize = 10.sp, color = TextSecondary)
                                        Text(it, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                    }
                                }
                                Button(
                                    onClick = onChatClick,
                                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Text("Chat / Deal", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Post Body Content
                Spacer(modifier = Modifier.height(8.dp))
                PostFormattedText(text = post.content)

                // Community Post Action Bar
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Comment / Replies
                    Row(
                        modifier = Modifier.clickable(onClick = onCommentClick),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Outlined.ChatBubbleOutline,
                            contentDescription = "Reply",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (post.repliesCount > 0) "${post.repliesCount}" else "Reply",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    // Repost
                    Row(
                        modifier = Modifier.clickable(onClick = onRepostClick),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Repeat,
                            contentDescription = "Repost",
                            tint = if (post.isReposted) GreenPrimary else TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${post.repostsCount}",
                            fontSize = 12.sp,
                            color = if (post.isReposted) GreenPrimary else TextSecondary,
                            fontWeight = if (post.isReposted) FontWeight.Bold else FontWeight.Normal
                        )
                    }

                    // Like / Heart
                    Row(
                        modifier = Modifier.clickable(onClick = onLikeClick),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            if (post.isLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (post.isLiked) Color(0xFFE53935) else TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${post.likesCount}",
                            fontSize = 12.sp,
                            color = if (post.isLiked) Color(0xFFE53935) else TextSecondary,
                            fontWeight = if (post.isLiked) FontWeight.Bold else FontWeight.Normal
                        )
                    }

                    // Bookmark
                    IconButton(
                        onClick = onBookmarkClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            if (post.isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (post.isBookmarked) GreenPrimary else TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Share
                    IconButton(
                        onClick = onShareClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Share,
                            contentDescription = "Share",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PostFormattedText(text: String) {
    val words = text.split(" ")
    val annotatedString = buildAnnotatedString {
        words.forEachIndexed { index, word ->
            if (word.startsWith("#") || word.startsWith("@")) {
                withStyle(
                    style = SpanStyle(
                        color = GreenDark,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append(word)
                }
            } else {
                append(word)
            }
            if (index < words.size - 1) {
                append(" ")
            }
        }
    }
    Text(
        text = annotatedString,
        fontSize = 14.sp,
        color = TextPrimary,
        lineHeight = 20.sp
    )
}

// Dialog for Composing New Community Post
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposeCommunityPostDialog(
    onDismiss: () -> Unit,
    onPostCreated: (CommunityPost) -> Unit
) {
    var contentText by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(GlobalMockData.userRole.value) }
    var selectedType by remember { mutableStateOf(if (selectedRole == "BUYER") "BUYER_DEMAND" else "EXPERIENCE") }
    var selectedCrop by remember { mutableStateOf(GlobalMockData.farmerCrop.value) }
    var demandQty by remember { mutableStateOf("") }
    var offeredPrice by remember { mutableStateOf("") }

    val cropsList = listOf("Wheat", "Soyabean", "Cotton", "Onion", "Sugarcane", "Tomato", "Rice", "Gram", "General")
    val maxChars = 280

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Dialog Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "New Community Post",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Role Selector Switch
                Text("Post As:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedRole == "FARMER",
                        onClick = {
                            selectedRole = "FARMER"
                            selectedType = "EXPERIENCE"
                        },
                        label = { Text("🌾 Farmer", fontSize = 12.sp) }
                    )
                    FilterChip(
                        selected = selectedRole == "BUYER",
                        onClick = {
                            selectedRole = "BUYER"
                            selectedType = "BUYER_DEMAND"
                        },
                        label = { Text("🤝 Buyer", fontSize = 12.sp) }
                    )
                    FilterChip(
                        selected = selectedRole == "EXPERT",
                        onClick = {
                            selectedRole = "EXPERT"
                            selectedType = "GUIDANCE"
                        },
                        label = { Text("🔬 Expert", fontSize = 12.sp) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Post Category Selection
                Text("Category:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        "EXPERIENCE" to "🌾 Experience",
                        "GUIDANCE" to "💡 Practical Guide",
                        "BUYER_DEMAND" to "🤝 Buyer Demand",
                        "QUESTION" to "❓ Ask Advice"
                    ).forEach { (typeKey, label) ->
                        FilterChip(
                            selected = selectedType == typeKey,
                            onClick = { selectedType = typeKey },
                            label = { Text(label, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Crop Tag Selector
                Text("Tag Crop:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    cropsList.forEach { crop ->
                        FilterChip(
                            selected = selectedCrop == crop,
                            onClick = { selectedCrop = crop },
                            label = { Text(crop, fontSize = 11.sp) }
                        )
                    }
                }

                // If Buyer Demand, show Quantity & Offered Price fields
                if (selectedType == "BUYER_DEMAND") {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = demandQty,
                            onValueChange = { demandQty = it },
                            label = { Text("Qty (e.g. 50 Tons)", fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = offeredPrice,
                            onValueChange = { offeredPrice = it },
                            label = { Text("Price (e.g. ₹4,800/Q)", fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Post Body Input
                OutlinedTextField(
                    value = contentText,
                    onValueChange = { if (it.length <= maxChars) contentText = it },
                    placeholder = {
                        Text(
                            text = if (selectedType == "BUYER_DEMAND")
                                "Describe your procurement specs, moisture limit, delivery or spot payment terms..."
                            else
                                "Share what worked on your farm, medicine dosage, fertilizer results, or yield secrets...",
                            fontSize = 13.sp,
                            color = TextSecondary.copy(alpha = 0.7f)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenPrimary,
                        unfocusedBorderColor = Color(0xFFDDDDDD)
                    )
                )

                // Character counter & Quick tags
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("#OrganicTips", "#PestRemedy", "#WaterSaving").forEach { tag ->
                            Text(
                                text = tag,
                                fontSize = 11.sp,
                                color = GreenDark,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clickable {
                                        if (!contentText.contains(tag)) {
                                            contentText = (contentText + " " + tag).trim()
                                        }
                                    }
                                    .padding(vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "${contentText.length} / $maxChars",
                        fontSize = 11.sp,
                        color = if (contentText.length > 260) Color.Red else TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Submit Button
                Button(
                    onClick = {
                        if (contentText.isNotBlank()) {
                            val newPost = CommunityPost(
                                authorName = GlobalMockData.farmerName.value,
                                authorHandle = "@" + GlobalMockData.farmerName.value.filter { it.isLetter() }.lowercase(),
                                authorRole = selectedRole,
                                authorLocation = GlobalMockData.userLocation.value,
                                authorAvatarInitials = GlobalMockData.farmerName.value.take(2).uppercase(),
                                isVerified = true,
                                content = contentText.trim(),
                                postType = selectedType,
                                cropTag = selectedCrop,
                                timestampText = "Just now",
                                demandQuantity = if (selectedType == "BUYER_DEMAND") demandQty.ifBlank { null } else null,
                                offeredPrice = if (selectedType == "BUYER_DEMAND") offeredPrice.ifBlank { null } else null
                            )
                            onPostCreated(newPost)
                        }
                    },
                    enabled = contentText.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (selectedRole == "BUYER") OrangePrimary else GreenPrimary)
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Post to Community (Alert All)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}

// Dialog for Viewing Discussion Thread and Replying
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityThreadDialog(
    post: CommunityPost,
    onDismiss: () -> Unit,
    onAddReply: (String) -> Unit
) {
    var replyInput by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Discussion & Guidance Thread", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFEEEEEE))

                // Scrollable Replies List
                LazyColumn(modifier = Modifier.weight(1f)) {
                    // Original Post Summary
                    item {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF9FBF9),
                            border = BorderStroke(1.dp, Color(0xFFE8E8E8)),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(post.authorName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(post.authorHandle, fontSize = 12.sp, color = TextSecondary)
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text(post.timestampText, fontSize = 11.sp, color = TextSecondary)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(post.content, fontSize = 13.sp, color = TextPrimary, lineHeight = 18.sp)
                            }
                        }
                    }

                    // Section Header
                    item {
                        Text(
                            text = "Replies & Peer Guidance (${post.replies.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    }

                    if (post.replies.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                                Text("No replies yet. Guide this farmer or respond to deal!", fontSize = 12.sp, color = TextSecondary)
                            }
                        }
                    } else {
                        items(post.replies) { reply ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, Color(0xFFF0F0F0)),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            ) {
                                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.Top) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(if (reply.authorRole == "BUYER") OrangePrimary else GreenPrimary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(reply.authorName.take(2).uppercase(), fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(reply.authorName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(reply.authorHandle, fontSize = 11.sp, color = TextSecondary)
                                            Spacer(modifier = Modifier.weight(1f))
                                            Text(reply.timestampText, fontSize = 10.sp, color = TextSecondary)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(reply.content, fontSize = 13.sp, color = TextPrimary)
                                    }
                                }
                            }
                        }
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFEEEEEE))

                // Reply Input Field
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = replyInput,
                        onValueChange = { replyInput = it },
                        placeholder = { Text("Write guidance or response...", fontSize = 12.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenPrimary,
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (replyInput.isNotBlank()) {
                                onAddReply(replyInput.trim())
                                replyInput = ""
                            }
                        },
                        enabled = replyInput.isNotBlank(),
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (replyInput.isNotBlank()) GreenPrimary else Color(0xFFE0E0E0))
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}
