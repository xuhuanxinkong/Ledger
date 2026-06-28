package com.xinkong.ledger.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.xinkong.ledger.model.Bill
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlin.time.Clock

@Composable
fun AddEditBillScreen(
    editingBill: Bill?,  // null = 新增模式，非null = 编辑模式
    onBack: () -> Unit,
    onSave: (name: String, amount: Double, date: Long, note: String) -> Unit
) {
    val isEditing = editingBill != null && editingBill.id != 0L

    var name by remember { mutableStateOf(editingBill?.name ?: "") }
    var amountText by remember {
        mutableStateOf(editingBill?.let {
            // 编辑模式：把负数的负号去掉，用开关控制收支
            if (it.amount < 0) (-it.amount).toLong().toString()
            else it.amount.toLong().toString()
        } ?: "")
    }
    var isExpense by remember { mutableStateOf(editingBill?.let { it.amount < 0 } ?: true) }
    var dateText by remember {
        mutableStateOf(editingBill?.let { formatDate(it.date) } ?: formatDate(Clock.System.now().toEpochMilliseconds()))
    }
    var note by remember { mutableStateOf(editingBill?.note ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (isEditing) "编辑账单" else "新增账单",
                        fontWeight = FontWeight.Bold)
                },
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
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 账目名称
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("账目名称") },
                placeholder = { Text("例如：午饭、工资") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // 金额
            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("金额") },
                placeholder = { Text("例如：30") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // 收入/支出切换
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = !isExpense,
                    onClick = { isExpense = false },
                    label = { Text("收入") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = isExpense,
                    onClick = { isExpense = true },
                    label = { Text("支出") },
                    modifier = Modifier.weight(1f)
                )
            }

            // 日期
            OutlinedTextField(
                value = dateText,
                onValueChange = { dateText = it },
                label = { Text("日期") },
                placeholder = { Text("格式：2026-06-24") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // 备注
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("备注") },
                placeholder = { Text("可选") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 保存按钮
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: return@Button
                    val finalAmount = if (isExpense) -amount else amount
                    val timestamp = parseDate(dateText) ?: return@Button
                    onSave(name.trim(), finalAmount, timestamp, note.trim())
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                enabled = name.isNotBlank() && amountText.isNotBlank() && dateText.isNotBlank()
            ) {
                Text(if (isEditing) "保存修改" else "添加账单",
                    fontWeight = FontWeight.Bold)
            }
        }
    }
}

// 解析 yyyy-MM-dd 格式为时间戳
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