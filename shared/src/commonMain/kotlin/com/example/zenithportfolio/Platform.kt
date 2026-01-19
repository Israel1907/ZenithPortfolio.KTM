package com.example.zenithportfolio

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform