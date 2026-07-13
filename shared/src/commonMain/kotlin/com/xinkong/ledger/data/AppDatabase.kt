package com.xinkong.ledger.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.xinkong.ledger.model.AccountBook
import com.xinkong.ledger.model.Bill

@Database(
    entities = [AccountBook::class, Bill::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accountBookDao(): AccountBookDao
    abstract fun billDao(): BillDao
}
