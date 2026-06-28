package com.xinkong.ledger

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform