package com.xinkong.ledger.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

// Android 需要传入 Context，所以在 Android 端用这个函数
fun getDatabaseBuilder(ctx: Context): RoomDatabase.Builder<AppDatabase> {
    val appContext = ctx.applicationContext
    val dbFile = appContext.getDatabasePath("ledger.db")
    return Room.databaseBuilder<AppDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}
