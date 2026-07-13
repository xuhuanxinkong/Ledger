package com.xinkong.ledger.viewmodel

import androidx.lifecycle.ViewModel
import com.xinkong.ledger.data.LedgerRepository
import com.xinkong.ledger.model.AccountBook
import com.xinkong.ledger.model.Bill
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LedgerViewModel(
    private val repository: LedgerRepository
) : ViewModel() {

    private val _accountBooks = MutableStateFlow<List<AccountBook>>(emptyList())
    val accountBooks: StateFlow<List<AccountBook>> = _accountBooks

    private val _currentBook = MutableStateFlow<AccountBook?>(null)
    val currentBook: StateFlow<AccountBook?> = _currentBook

    private val _bills = MutableStateFlow<List<Bill>>(emptyList())
    val bills: StateFlow<List<Bill>> = _bills

    private val _editingBill = MutableStateFlow<Bill?>(null)
    val editingBill: StateFlow<Bill?> = _editingBill

    // 用于在 ViewModel 外部启动协程
    private val scope = CoroutineScope(Dispatchers.Default)

    init {
        scope.launch { refreshAccountBooks() }
    }

    // ===== 账本操作 =====

    fun addAccountBook(name: String) {
        scope.launch {
            repository.addAccountBook(name)
            refreshAccountBooks()
        }
    }

    fun updateAccountBook(id: Long, name: String) {
        scope.launch {
            repository.updateAccountBook(id, name)
            refreshAccountBooks()
            if (_currentBook.value?.id == id) {
                _currentBook.value = repository.getAllAccountBooks().first { it.id == id }
            }
        }
    }

    fun deleteAccountBook(id: Long) {
        scope.launch {
            repository.deleteAccountBook(id)
            refreshAccountBooks()
            if (_currentBook.value?.id == id) {
                _currentBook.value = null
                _bills.value = emptyList()
            }
        }
    }

    // ===== 账本切换 =====

    fun selectAccountBook(book: AccountBook) {
        scope.launch {
            _currentBook.value = book
            refreshBills(book.id)
        }
    }

    fun backToBookList() {
        _currentBook.value = null
        _bills.value = emptyList()
    }

    // ===== 账单操作 =====

    fun addBill(name: String, amount: Double, date: Long, note: String) {
        val bookId = _currentBook.value?.id ?: return
        scope.launch {
            repository.addBill(bookId, name, amount, date, note)
            refreshBills(bookId)
        }
    }

    fun startEditBill(bill: Bill) {
        _editingBill.value = bill
    }

    fun updateBill(id: Long, name: String, amount: Double, date: Long, note: String) {
        scope.launch {
            repository.updateBill(id, name, amount, date, note)
            _currentBook.value?.id?.let { refreshBills(it) }
            _editingBill.value = null
        }
    }

    fun deleteBill(id: Long) {
        scope.launch {
            repository.deleteBill(id)
            _currentBook.value?.id?.let { refreshBills(it) }
        }
    }

    fun clearEditingBill() {
        _editingBill.value = null
    }

    // ===== 内部方法 =====

    private suspend fun refreshAccountBooks() {
        _accountBooks.value = repository.getAllAccountBooks()
    }

    private suspend fun refreshBills(bookId: Long) {
        _bills.value = repository.getBillsByBookId(bookId)
    }
}
