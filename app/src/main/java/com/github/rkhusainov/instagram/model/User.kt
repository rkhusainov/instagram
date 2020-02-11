package com.github.rkhusainov.instagram.model

data class User(
    val name: String = "",
    val username: String = "",
    val website: String = "",
    val bio: String = "",
    val email: String = "",
    val phone: Long = 0L
)