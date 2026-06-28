package com.xinkong.ledger.viewmodel

import androidx.lifecycle.ViewModel
import com.xinkong.ledger.data.InMemoryLedgerRepository
import com.xinkong.ledger.data.LedgerRepository
import com.xinkong.ledger.model.AccountBook
import com.xinkong.ledger.model.Bill
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LedgerViewModel : ViewModel(){


    // ---------------- 数据 ----------------
    private val repository: LedgerRepository = InMemoryLedgerRepository()

    private val _accountBooks = MutableStateFlow<List<AccountBook>>(emptyList())
    val accountBooks: StateFlow<List<AccountBook>> = _accountBooks

    private val _currentBook = MutableStateFlow<AccountBook?>(null)
    val currentBook: StateFlow<AccountBook?> = _currentBook

    private val _bills = MutableStateFlow<List<Bill>>(emptyList())
    val bills: StateFlow<List<Bill>> = _bills

    private val _editingBill = MutableStateFlow<Bill?>(null)
    val editingBill: StateFlow<Bill?> = _editingBill

    init {
        refreshAccountBooks()
    }


    // ---------------- 操作 ----------------


    // ===== 账本操作 =====
    fun addAccountBook(name: String) {
        repository.addAccountBook(name)
        refreshAccountBooks()
    }

    fun updateAccountBook(id: Long, name: String) {
        repository.updateAccountBook(id, name)
        refreshAccountBooks()
        // 如果改的是当前账本，刷新一下
        if (_currentBook.value?.id == id) {
            _currentBook.value = repository.getAllAccountBooks().first { it.id == id }
        }
    }

    fun deleteAccountBook(id: Long) {
        repository.deleteAccountBook(id)
        refreshAccountBooks()
        // 如果删的是当前账本，回到账本列表
        if (_currentBook.value?.id == id) {
            _currentBook.value = null
            _bills.value = emptyList()
        }
    }

    // ===== 账本切换 =====

    fun selectAccountBook(book: AccountBook) {
        _currentBook.value = book
        refreshBills(book.id)
    }

    fun backToBookList() {
        _currentBook.value = null
        _bills.value = emptyList()
    }

    // ===== 账单操作 =====

    fun addBill(name: String, amount: Double, date: Long, note: String) {
        val bookId = _currentBook.value?.id ?: return
        repository.addBill(bookId, name, amount, date, note)
        refreshBills(bookId)
    }

    fun startEditBill(bill: Bill) {
        _editingBill.value = bill
    }

    fun updateBill(id: Long, name: String, amount: Double, date: Long, note: String) {
        repository.updateBill(id, name, amount, date, note)
        _currentBook.value?.id?.let { refreshBills(it) }
        _editingBill.value = null
    }

    fun deleteBill(id: Long) {
        repository.deleteBill(id)
        _currentBook.value?.id?.let { refreshBills(it) }
    }

    fun clearEditingBill() {
        _editingBill.value = null
    }

    // ===== 内部方法 =====

    private fun refreshAccountBooks() {
        _accountBooks.value = repository.getAllAccountBooks()
    }

    private fun refreshBills(bookId: Long) {
        _bills.value = repository.getBillsByBookId(bookId)
    }


}