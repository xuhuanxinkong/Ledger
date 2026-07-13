package com.xinkong.ledger.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "account_books")
data class AccountBook(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String
)