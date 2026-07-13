package com.xinkong.ledger.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xinkong.ledger.model.AccountBook

@Composable
fun AccountBookListScreen(
    accountBooks: List<AccountBook>,
    onAddBook:(String)-> Unit,
    onUpdateBook: (Long, String) -> Unit,
    onDeleteBook: (Long) -> Unit,
    onSelectBook: (AccountBook) -> Unit,
    onBack: (() -> Unit)? = null
) {
    var showAdd by remember { mutableStateOf(false) }
    var editingBook by remember { mutableStateOf<AccountBook?>(null) }
    
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 64.dp, bottom = 24.dp, start = 24.dp, end = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onBack?.invoke() },
                    enabled = onBack != null,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowLeft,
                        contentDescription = "返回",
                        tint = MaterialTheme.colorScheme.onSurface.copy(
                            alpha = if (onBack != null) 1f else 0.35f
                        )
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "选择账本",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }


            LazyColumn(
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(accountBooks, key = { it.id }) { book ->
                    // UI defaults to white cards as we don't have currentBookId passed here.
                    val isSelected = false
                    val bgColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.White
                    val contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                    val subContentColor = if (isSelected) Color.White.copy(alpha = 0.7f) else Color.Gray

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectBook(book) },
                        colors = CardDefaults.cardColors(containerColor = bgColor),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) Color.White.copy(alpha = 0.2f) else MaterialTheme.colorScheme.background),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.List, contentDescription = "Book", tint = contentColor)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            

                            Column(modifier = Modifier.weight(1f)) {
                                Text(book.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = contentColor)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("点击进入账本", fontSize = 14.sp, color = subContentColor)
                            }
                            

                            var expanded by remember { mutableStateOf(false) }
                            Box {
                                IconButton(onClick = { expanded = true }) {
                                    Icon(Icons.Default.MoreVert, contentDescription = "More", tint = Color.LightGray)
                                }
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("编辑") },
                                        onClick = {
                                            expanded = false
                                            editingBook = book
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("删除", color = MaterialTheme.colorScheme.error) },
                                        onClick = {
                                            expanded = false
                                            onDeleteBook(book.id)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp))
                            .clickable { showAdd = true }
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("新建账本", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }

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
