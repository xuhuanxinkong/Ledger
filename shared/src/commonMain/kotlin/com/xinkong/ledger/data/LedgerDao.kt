package com.xinkong.ledger.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.xinkong.ledger.model.AccountBook
import com.xinkong.ledger.model.Bill

@Dao
interface LedgerDao {

    @Query("SELECT * FROM account_books")
    suspend fun getAllAccountBooks(): List<AccountBook>
    @Insert
    suspend fun insertAccountBook(accountBook: AccountBook)
    @Update
    suspend fun updateAccountBook(accountBook: AccountBook)
    @Query("DELETE FROM account_books WHERE id = :id")
    suspend fun deleteAccountBookById(id: Long)
    // ---- 账单 (Bill) 操作 ----
    // 根据账本 ID 查账单，并按时间倒序排列（对应你的需求）
    @Query("SELECT * FROM bills WHERE accountBookId = :accountId ORDER BY date DESC")
    suspend fun getBillsByBookId(accountId: Long): List<Bill>
    @Insert
    suspend fun insertBill(bill: Bill)
    @Update
    suspend fun updateBill(bill: Bill)
    @Query("DELETE FROM bills WHERE id = :id")
    suspend fun deleteBillById(id: Long)
}