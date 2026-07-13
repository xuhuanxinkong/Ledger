package com.xinkong.ledger.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun getDatabaseBuilder(ctx: Context): RoomDatabase.Builder<LedgerDatabase> {
    val appContext = ctx.applicationContext
    val dbFile = appContext.getDatabasePath("ledger.db")
    return Room.databaseBuilder<LedgerDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}
