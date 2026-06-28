package com.xinkong.ledger.data

import com.xinkong.ledger.model.AccountBook
import com.xinkong.ledger.model.Bill

interface LedgerRepository {

    fun getAllAccountBooks(): List<AccountBook>
    fun addAccountBook(name: String)
    fun updateAccountBook(id: Long,name: String)
    fun deleteAccountBook(id: Long)

    fun getBillsByBookId(accountId: Long): List<Bill>
    fun addBill(accountId: Long,name: String,amount: Double,
                date: Long,note: String)
    fun updateBill(id: Long,name: String,amount: Double,
                   date: Long,note: String)
    fun deleteBill(id: Long)
}