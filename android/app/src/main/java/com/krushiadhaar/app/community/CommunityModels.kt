package com.krushiadhaar.app.community

import java.util.UUID

enum class CommunityRole(val label: String, val badge: String) {
    FARMER("Farmer", "🌾 Farmer"),
    BUYER("Buyer", "🤝 Buyer"),
    EXPERT("Agri Expert", "🔬 Agri Expert")
}

enum class PostType(val label: String, val badgeEmoji: String) {
    EXPERIENCE("Farmer Experience", "🌾"),
    GUIDANCE("Farmer Guidance / Tips", "💡"),
    BUYER_DEMAND("Buyer Requirement / Demand", "🤝"),
    QUESTION("Farmer Query", "❓"),
    MARKET_UPDATE("Mandi & Trade Pulse", "📈")
}

data class CommunityReply(
    val id: String = UUID.randomUUID().toString(),
    val postId: String,
    val authorName: String,
    val authorHandle: String,
    val authorRole: String = "FARMER",
    val authorLocation: String,
    val content: String,
    val timestampText: String = "Just now",
    var likesCount: Int = 0,
    var isLiked: Boolean = false
)

data class CommunityPost(
    val id: String = UUID.randomUUID().toString(),
    val authorName: String,
    val authorHandle: String,
    val authorRole: String = "FARMER", // "FARMER", "BUYER", "EXPERT"
    val authorLocation: String,
    val authorAvatarInitials: String = authorName.take(2).uppercase(),
    val isVerified: Boolean = true,
    val content: String,
    val postType: String = "EXPERIENCE", // "EXPERIENCE", "GUIDANCE", "BUYER_DEMAND", "QUESTION"
    val cropTag: String? = null,
    val tags: List<String> = emptyList(),
    val timestampText: String = "Just now",
    val timestampMillis: Long = System.currentTimeMillis(),
    var likesCount: Int = 0,
    var isLiked: Boolean = false,
    var repostsCount: Int = 0,
    var isReposted: Boolean = false,
    var repliesCount: Int = 0,
    val replies: MutableList<CommunityReply> = mutableListOf(),
    var isBookmarked: Boolean = false,
    val mediaUrl: String? = null,
    val buyerContactPhone: String? = null,
    val demandQuantity: String? = null,
    val offeredPrice: String? = null
)
