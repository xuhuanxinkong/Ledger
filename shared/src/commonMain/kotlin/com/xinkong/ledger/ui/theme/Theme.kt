package com.xinkong.ledger.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LedgerColorScheme = lightColorScheme(
    primary = Color(0xFF2E7D32) ,         // 深绿色 - 主色
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC8E6C9), // 浅绿色
    secondary = Color(0xFFFF6F00),        // 橙色 - 用于收入
    onSecondary = Color.White,
    error = Color(0xFFD32F2F),            // 红色 - 用于支出
    surface = Color.White,
    background = Color(0xFFF5F5F5),       // 浅灰背景
)

@Composable
fun LedgerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LedgerColorScheme,
        content = content
    )
}