package com.xinkong.ledger.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
) {
    val balance = bills.sumOf { it.amount }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddBill,
                containerColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize()) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer)
                        )
                    )
                    .padding(top = 80.dp, bottom = 40.dp, start = 24.dp, end = 24.dp)
            ) {
                Column {

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                            .clickable { onBack() }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = bookName,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            Icons.Default.KeyboardArrowDown,
                            contentDescription = "Switch Book",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = (if (balance < 0) "-¥ " else "¥ ") + formatAmountAbs(balance),
                        color = Color.White,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }


            Column(modifier = Modifier.padding(top = 32.dp, start = 20.dp, end = 20.dp).fillMaxSize()) {
                Text(
                    text = "今天",
                    color = Color.Gray,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (bills.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize().padding(bottom = padding.calculateBottomPadding()), contentAlignment = Alignment.Center) {
                        Text("还没有账单，点击右下角 + 添加", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = padding.calculateBottomPadding() + 80.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
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
}

@Composable
private fun BillItem(
    bill: Bill,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val isIncome = bill.amount >= 0
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Placeholder
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Text(bill.name.take(1), fontWeight = FontWeight.Bold, color = Color.Gray)
            }
            Spacer(modifier = Modifier.width(16.dp))
            
            // Texts
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = bill.name,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (bill.note.isNotBlank()) {
                    Text(
                        text = bill.note,
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                }
            }
            

            Text(
                text = (if (isIncome) "+" else "") + formatAmount(bill.amount),
                color = if (isIncome) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.LightGray)
            }
        }
    }
}

//转为绝对值
fun formatAmountAbs(amount: Double): String {
    val abs = if (amount < 0) -amount else amount
    val intPart = abs.toLong()
    val decPart = ((abs - intPart) * 100).toLong()
    return "$intPart.${decPart.toString().padStart(2, '0')}"
}

//转为字符串
fun formatAmount(amount: Double): String {
    val isNegative = amount < 0
    val abs = if (isNegative) -amount else amount
    val intPart = abs.toLong()
    val decPart = ((abs - intPart) * 100).toLong()
    val sign = if (isNegative) "-" else ""
    return "$sign$intPart.${decPart.toString().padStart(2, '0')}"
}

//转换格式
fun formatDate(timestamp: Long): String {
    val date = kotlin.time.Instant.fromEpochMilliseconds(timestamp)
        .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
    return "${date.year}-${date.monthNumber.toString().padStart(2, '0')}-${date.dayOfMonth.toString().padStart(2, '0')}"
}