package com.xinkong.ledger.data

import com.xinkong.ledger.model.AccountBook
import com.xinkong.ledger.model.Bill


class InMemoryLedgerRepository : LedgerRepository {

    private val accountBooks = mutableListOf<AccountBook>()
    private val bills = mutableListOf<Bill>()

    private var nextBookId = 1L
    private var nextBillId = 1L

    override fun getAllAccountBooks(): List<AccountBook> = accountBooks.toList()

    override fun addAccountBook(name: String) {
        accountBooks.add(AccountBook(id = nextBookId++, name = name))
    }

    override fun updateAccountBook(id: Long, name: String) {
        val index = accountBooks.indexOfFirst { it.id == id }
        if (index != -1) {
            accountBooks[index] = accountBooks[index].copy(name = name)
        }
    }

    override fun deleteAccountBook(id: Long) {
        accountBooks.removeAll { it.id == id }
        bills.removeAll { it.accountBookId == id }
    }

    override fun getBillsByBookId(accountId: Long): List<Bill> =
        bills.filter { it.accountBookId == accountId }
            .sortedByDescending { it.date }

    override fun addBill(accountId: Long, name: String, amount: Double, date: Long, note: String) {
        bills.add(Bill(
            id = nextBillId++,
            accountBookId = accountId,
            name = name,
            amount = amount,
            date = date,
            note = note
        ))
    }

    override fun updateBill(id: Long, name: String, amount: Double, date: Long, note: String) {
        val index = bills.indexOfFirst { it.id == id }
        if (index != -1) {
            bills[index] = bills[index].copy(
                name = name,
                amount = amount,
                date = date,
                note = note
            )
        }
    }

    override fun deleteBill(id: Long) {
        bills.removeAll { it.id == id }
    }
}
