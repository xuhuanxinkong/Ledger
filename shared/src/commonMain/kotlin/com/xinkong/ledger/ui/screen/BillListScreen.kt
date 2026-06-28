package com.xinkong.ledger.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xinkong.ledger.model.Bill
import kotlinx.datetime.toLocalDateTime

@Composable
fun BillListScreen(
    bookName: String,
    bills: List<Bill>,
    onBack: () -> Unit,
    onAddBill: () -> Unit,
    onEditBill: (Bill) -> Unit,
    onDeleteBill: (Long) -> Unit
){
    val balance = bills.sumOf { it.amount }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(bookName, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("< 返回", color = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddBill,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Text("+", style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            // 余额卡片
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("余额", style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formatAmount(balance),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (balance >= 0) MaterialTheme.colorScheme.secondary
                        else MaterialTheme.colorScheme.error
                    )
                }
            }

            // 账单列表
            if (bills.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("还没有账单，点击右下角 + 添加",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(bills, key = { it.id }) { bill ->
                        BillItem(
                            bill = bill,
                            onEdit = { onEditBill(bill) },
                            onDelete = { onDeleteBill(bill.id) }
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun BillItem(
    bill: Bill,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左侧：名称和日期
            Column(modifier = Modifier.weight(1f)) {
                Text(bill.name, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = formatDate(bill.date),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                if (bill.note.isNotBlank()) {
                    Text(
                        text = bill.note,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
            // 右侧：金额和操作
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatAmount(bill.amount),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (bill.amount >= 0) MaterialTheme.colorScheme.secondary
                    else MaterialTheme.colorScheme.error
                )
                Row {
                    TextButton(onClick = onEdit, contentPadding = PaddingValues(0.dp)) {
                        Text("编辑", style = MaterialTheme.typography.bodySmall)
                    }
                    TextButton(onClick = onDelete, contentPadding = PaddingValues(0.dp)) {
                        Text("删除", style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}


// 格式化金额：正数显示 +，负数显示 -
fun formatAmount(amount: Double): String {
    val isNegative = amount < 0
    val abs = if (isNegative) -amount else amount
    val intPart = abs.toLong()
    val decPart = ((abs - intPart) * 100).toLong()
    val sign = if (isNegative) "" else "+"
    return "$sign$intPart.${decPart.toString().padStart(2, '0')}"
}

// 格式化时间戳为 yyyy-MM-dd
fun formatDate(timestamp: Long): String {
    val date = kotlin.time.Instant.fromEpochMilliseconds(timestamp)
        .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
    return "${date.year}-${date.monthNumber.toString().padStart(2, '0')}-${date.dayOfMonth.toString().padStart(2, '0')}"
}