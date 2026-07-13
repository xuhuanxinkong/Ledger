package com.xinkong.ledger.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xinkong.ledger.model.Bill
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlin.time.Clock

private val billCategories = listOf("餐饮", "交通", "购物", "娱乐", "服饰", "日用", "居住", "其他")

@Composable
fun AddEditBillScreen(
    editingBill: Bill?,
    onBack: () -> Unit,
    onSave: (name: String, amount: Double, date: Long, note: String) -> Unit
) {
    val isEditing = editingBill != null && editingBill.id != 0L

    var name by remember { mutableStateOf(editingBill?.name ?: "") }
    var amountText by remember {
        mutableStateOf(editingBill?.let {
            if (it.amount < 0) (-it.amount).toLong().toString()
            else it.amount.toLong().toString()
        } ?: "")
    }
    var isExpense by remember { mutableStateOf(editingBill?.let { it.amount < 0 } ?: true) }
    var selectedCategory by remember(editingBill?.id) {
        mutableStateOf<String?>(
            editingBill?.name?.takeIf { it in billCategories }
                ?: if (!isEditing) billCategories.first() else null
        )
    }
    var dateText by remember {
        mutableStateOf(
            if (isEditing) formatDate(editingBill!!.date)
            else formatDate(Clock.System.now().toEpochMilliseconds())
        )
    }
    var note by remember { mutableStateOf(editingBill?.note ?: "") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 64.dp, bottom = 24.dp, start = 24.dp, end = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "取消",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.clickable { onBack() }.padding(8.dp)
                )
                Text(
                    text = if (isEditing) "编辑账单" else "记一笔",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "保存",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable {
                        errorMsg = null
                        val amount = amountText.toDoubleOrNull()
                        if (amount == null || amount <= 0) {
                            errorMsg = "请输入有效金额"
                            return@clickable
                        }
                        val finalAmount = if (isExpense) -amount else amount
                        val timestamp = parseDate(dateText)
                        if (timestamp == null) {
                            errorMsg = "日期格式不正确，请使用 yyyy-MM-dd"
                            return@clickable
                        }
                        val finalName = name.trim().ifBlank {
                            selectedCategory ?: if (isExpense) "支出" else "收入"
                        }
                        onSave(finalName, finalAmount, timestamp, note.trim())
                    }.padding(8.dp)
                )
            }

            // Tabs
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isExpense) MaterialTheme.colorScheme.onSurface else Color.LightGray.copy(alpha = 0.3f))
                        .clickable { isExpense = true }
                        .padding(horizontal = 24.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "支出",
                        color = if (isExpense) Color.White else Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (!isExpense) MaterialTheme.colorScheme.onSurface else Color.LightGray.copy(alpha = 0.3f))
                        .clickable { isExpense = false }
                        .padding(horizontal = 24.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "收入",
                        color = if (!isExpense) Color.White else Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Amount Area
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "¥",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.alignByBaseline()
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    BasicTextField(
                        value = amountText,
                        onValueChange = { amountText = it.filter { c -> c.isDigit() || c == '.' } },
                        textStyle = TextStyle(
                            fontSize = 56.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        singleLine = true,
                        modifier = Modifier
                            .alignByBaseline()
                            .width(IntrinsicSize.Min)
                            .defaultMinSize(minWidth = 100.dp)
                    )
                }
            }

            // 错误提示
            errorMsg?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                val catsRow1 = billCategories.take(4)
                val catsRow2 = billCategories.drop(4)
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    catsRow1.forEach { cat ->
                        CategoryItem(
                            name = cat,
                            isSelected = selectedCategory == cat,
                            onClick = {
                                selectedCategory = cat
                                name = cat
                            }
                        )
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    catsRow2.forEach { cat ->
                        CategoryItem(
                            name = cat,
                            isSelected = selectedCategory == cat,
                            onClick = {
                                selectedCategory = cat
                                name = cat
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.List, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    BasicTextField(
                        value = dateText,
                        onValueChange = { dateText = it },
                        textStyle = TextStyle(fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface),
                        modifier = Modifier.width(80.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Create, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    BasicTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            selectedCategory = it.takeIf { value -> value in billCategories }
                        },
                        textStyle = TextStyle(fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface),
                        modifier = Modifier.fillMaxWidth(),
                        decorationBox = { innerTextField ->
                            if (name.isEmpty()) {
                                Text("添加备注或名称...", color = Color.Gray, fontSize = 14.sp)
                            }
                            innerTextField()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryItem(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(name.take(1), fontWeight = FontWeight.Bold, color = if(isSelected) Color.White else MaterialTheme.colorScheme.onSurface)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = name,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
        )
    }
}

//修改年份格式
fun parseDate(dateStr: String): Long? {
    return try {
        val parts = dateStr.split("-")
        if (parts.size != 3) return null
        val year = parts[0].toInt()
        val month = parts[1].toInt()
        val day = parts[2].toInt()
        LocalDate(year, month, day)
            .atStartOfDayIn(TimeZone.currentSystemDefault())
            .toEpochMilliseconds()
    } catch (e: Exception) {
        null
    }
}
