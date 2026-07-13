package com.xinkong.ledger.data

import com.xinkong.ledger.model.AccountBook
import com.xinkong.ledger.model.Bill

interface LedgerRepository {

    suspend fun getAllAccountBooks(): List<AccountBook>
    suspend fun addAccountBook(name: String)
    suspend fun updateAccountBook(id: Long, name: String)
    suspend fun deleteAccountBook(id: Long)

    suspend fun getBillsByBookId(accountId: Long): List<Bill>
    suspend fun addBill(accountId: Long, name: String, amount: Double,
                date: Long, note: String)
    suspend fun updateBill(id: Long, name: String, amount: Double,
                   date: Long, note: String)
    suspend fun deleteBill(id: Long)
}
