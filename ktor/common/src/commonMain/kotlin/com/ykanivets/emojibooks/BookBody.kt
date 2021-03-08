package com.ykanivets.emojibooks

import kotlinx.serialization.Serializable

@Serializable
data class BookBody(
    val emoji: String,
    val title: String,
    val author: String
)
