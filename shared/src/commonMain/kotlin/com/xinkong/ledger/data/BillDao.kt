package com.xinkong.ledger.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.xinkong.ledger.model.Bill

@Dao
interface BillDao {

    @Query("SELECT * FROM bills WHERE accountBookId = :bookId ORDER BY date DESC")
    suspend fun getByBookId(bookId: Long): List<Bill>

    @Query("SELECT * FROM bills WHERE id = :id")
    suspend fun getById(id: Long): Bill?

    @Insert
    suspend fun insert(bill: Bill)

    @Update
    suspend fun update(bill: Bill)

    @Query("DELETE FROM bills WHERE id = :id")
    suspend fun deleteById(id: Long)
}
