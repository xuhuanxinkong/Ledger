package com.xinkong.ledger.model

data class Bill(
    val id: Long,
    val accountBookId: Long,
    val name: String,
    val amount: Double,
    val date: Long,
    val note: String
)