package com.xinkong.ledger

import androidx.compose.runtime.*
import androidx.room.RoomDatabase
import com.xinkong.ledger.data.AppDatabase
import com.xinkong.ledger.data.RoomLedgerRepository
import com.xinkong.ledger.viewmodel.LedgerViewModel
import com.xinkong.ledger.ui.screen.AccountBookListScreen
import com.xinkong.ledger.ui.screen.AddEditBillScreen
import com.xinkong.ledger.ui.screen.BillListScreen
import com.xinkong.ledger.ui.theme.LedgerTheme

@Composable
fun App(dbBuilder: RoomDatabase.Builder<AppDatabase>) {
    LedgerTheme {
        // 创建数据库和 Repository
        val db = remember {
            dbBuilder.setDriver(androidx.sqlite.driver.bundled.BundledSQLiteDriver())
                .build()
        }
        val repository = remember { RoomLedgerRepository(db) }
        val vm = remember { LedgerViewModel(repository) }

        // 观察 ViewModel 的状态
        val accountBooks by vm.accountBooks.collectAsState()
        val currentBook by vm.currentBook.collectAsState()
        val bills by vm.bills.collectAsState()
        val editingBill by vm.editingBill.collectAsState()
        var showBookSelector by remember { mutableStateOf(false) }

        // 导航逻辑：根据状态决定显示哪个页面
        if (editingBill != null) {
            AddEditBillScreen(
                editingBill = editingBill,
                onBack = { vm.clearEditingBill() },
                onSave = { name, amount, date, note ->
                    if (editingBill != null && editingBill!!.id != 0L) {
                        vm.updateBill(editingBill!!.id, name, amount, date, note)
                    } else {
                        vm.addBill(name, amount, date, note)
                    }
                    vm.clearEditingBill()
                }
            )
        } else if (currentBook != null && !showBookSelector) {
            BillListScreen(
                bookName = currentBook!!.name,
                bills = bills,
                onBack = { showBookSelector = true },
                onAddBill = { vm.startEditBill(com.xinkong.ledger.model.Bill(0, 0, "", 0.0, 0, "")) },
                onEditBill = { bill -> vm.startEditBill(bill) },
                onDeleteBill = { id -> vm.deleteBill(id) }
            )
        } else {
            AccountBookListScreen(
                accountBooks = accountBooks,
                onAddBook = { name -> vm.addAccountBook(name) },
                onUpdateBook = { id, name -> vm.updateAccountBook(id, name) },
                onDeleteBook = { id -> vm.deleteAccountBook(id) },
                onSelectBook = { book ->
                    vm.selectAccountBook(book)
                    showBookSelector = false
                },
                onBack = if (currentBook != null) {
                    { showBookSelector = false }
                } else {
                    null
                }
            )
        }
    }
}
