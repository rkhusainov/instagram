package com.github.rkhusainov.instagram.model

data class User(
    val name: String = "",
    val username: String = "",
    val phone: Long? = null,
    val website: String? = null,
    val bio: String? = null,
    val email: String = "",
    val photo: String? = null
)