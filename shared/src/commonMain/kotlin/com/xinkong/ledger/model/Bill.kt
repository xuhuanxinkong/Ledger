package com.xinkong.ledger.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bills")
data class Bill(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val accountBookId: Long,
    val name: String,
    val amount: Double,
    val date: Long,
    val note: String
)