package com.xinkong.ledger.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.xinkong.ledger.model.AccountBook

@Dao
interface AccountBookDao {

    @Query("SELECT * FROM account_books")
    suspend fun getAll(): List<AccountBook>

    @Insert
    suspend fun insert(accountBook: AccountBook)

    @Update
    suspend fun update(accountBook: AccountBook)

    @Delete
    suspend fun delete(accountBook: AccountBook)

    @Query("DELETE FROM bills WHERE accountBookId = :bookId")
    suspend fun deleteBillsByBookId(bookId: Long)
}
