package com.github.rkhusainov.instagram.model

import com.google.firebase.database.ServerValue
import java.util.*

data class FeedPost(
    val uid: String = "",
    val username: String = "",
    val image: String = "",
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val caption: String = "",
    val comments: List<Comment> = emptyList(),
    val timeStamp: Any = ServerValue.TIMESTAMP,
    val photo: String? = null
) {
    fun timeStampDate(): Date = Date(timeStamp as Long)
}