package com.xinkong.ledger.data

import com.xinkong.ledger.model.AccountBook
import com.xinkong.ledger.model.Bill

class RoomLedgerRepository(
    private val db: AppDatabase
) : LedgerRepository {

    private val bookDao = db.accountBookDao()
    private val billDao = db.billDao()

    override suspend fun getAllAccountBooks(): List<AccountBook> = bookDao.getAll()

    override suspend fun addAccountBook(name: String) {
        bookDao.insert(AccountBook(id = 0, name = name))
    }

    override suspend fun updateAccountBook(id: Long, name: String) {
        val book = bookDao.getAll().firstOrNull { it.id == id } ?: return
        bookDao.update(book.copy(name = name))
    }

    override suspend fun deleteAccountBook(id: Long) {
        val book = bookDao.getAll().firstOrNull { it.id == id } ?: return
        bookDao.deleteBillsByBookId(id)
        bookDao.delete(book)
    }

    override suspend fun getBillsByBookId(accountId: Long): List<Bill> =
        billDao.getByBookId(accountId)

    override suspend fun addBill(accountId: Long, name: String, amount: Double, date: Long, note: String) {
        billDao.insert(Bill(
            id = 0,
            accountBookId = accountId,
            name = name,
            amount = amount,
            date = date,
            note = note
        ))
    }

    override suspend fun updateBill(id: Long, name: String, amount: Double, date: Long, note: String) {
        val existing = billDao.getById(id) ?: return
        billDao.update(existing.copy(
            name = name,
            amount = amount,
            date = date,
            note = note
        ))
    }

    override suspend fun deleteBill(id: Long) {
        billDao.deleteById(id)
    }
}
