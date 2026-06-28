package com.xinkong.ledger.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xinkong.ledger.model.AccountBook


@Composable
fun AccountBookListScreen(
    accountBooks: List<AccountBook>,
    onAddBook:(String)-> Unit,
    onUpdateBook: (Long, String) -> Unit,
    onDeleteBook: (Long) -> Unit,
    onSelectBook: (AccountBook) -> Unit
){
    var showAdd by remember { mutableStateOf(false) }
    var editingBook by remember { mutableStateOf<AccountBook?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("我的账本", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAdd = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Text("+", style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    ){padding ->
        if (accountBooks.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("还没有账本，点击右下角 + 创建",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(accountBooks, key = { it.id }) { book ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                            .clickable { onSelectBook(book) },
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(book.name, style = MaterialTheme.typography.titleMedium)
                            Row {
                                TextButton(onClick = { editingBook = book }) {
                                    Text("编辑")
                                }
                                TextButton(onClick = { onDeleteBook(book.id) }) {
                                    Text("删除", color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    // 新增账本对话框
    if (showAdd) {
        NameInputDialog(
            title = "新增账本",
            initialValue = "",
            onConfirm = { name ->
                onAddBook(name)
                showAdd = false
            },
            onDismiss = { showAdd = false }
        )
    }

    // 编辑账本对话框
    editingBook?.let { book ->
        NameInputDialog(
            title = "编辑账本",
            initialValue = book.name,
            onConfirm = { name ->
                onUpdateBook(book.id, name)
                editingBook = null
            },
            onDismiss = { editingBook = null }
        )
    }
}

// 通用的名称输入对话框
@Composable
fun NameInputDialog(
    title: String,
    initialValue: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf(initialValue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("名称") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = { if (text.isNotBlank()) onConfirm(text.trim()) }
            ) { Text("确定") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    )
}