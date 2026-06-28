package com.xinkong.ledger

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.xinkong.ledger.viewmodel.LedgerViewModel
import com.xinkong.ledger.ui.screen.AccountBookListScreen
import com.xinkong.ledger.ui.screen.AddEditBillScreen
import com.xinkong.ledger.ui.screen.BillListScreen
import com.xinkong.ledger.ui.theme.LedgerTheme


@Composable
@Preview
fun App() {
    LedgerTheme {
        val vm = remember { LedgerViewModel() }

        // 观察 ViewModel 的状态
        val accountBooks by vm.accountBooks.collectAsState()
        val currentBook by vm.currentBook.collectAsState()
        val bills by vm.bills.collectAsState()
        val editingBill by vm.editingBill.collectAsState()

        // 导航逻辑：根据状态决定显示哪个页面
        if (editingBill != null) {
            // 编辑账单页面
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
        } else if (currentBook != null) {
            // 账单列表页面
            BillListScreen(
                bookName = currentBook!!.name,
                bills = bills,
                onBack = { vm.backToBookList() },
                onAddBill = { vm.startEditBill(com.xinkong.ledger.model.Bill(0, 0, "", 0.0, 0, "")) },
                onEditBill = { bill -> vm.startEditBill(bill) },
                onDeleteBill = { id -> vm.deleteBill(id) }
            )
        } else {
            // 账本列表页面（首页）
            AccountBookListScreen(
                accountBooks = accountBooks,
                onAddBook = { name -> vm.addAccountBook(name) },
                onUpdateBook = { id, name -> vm.updateAccountBook(id, name) },
                onDeleteBook = { id -> vm.deleteAccountBook(id) },
                onSelectBook = { book -> vm.selectAccountBook(book) }
            )
        }
    }
}