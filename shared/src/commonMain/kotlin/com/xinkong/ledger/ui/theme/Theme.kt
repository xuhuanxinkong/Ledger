package com.xinkong.ledger.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LedgerColorScheme = lightColorScheme(
    primary = Color(0xFF10B981) ,         // 翠绿色 - 主色
    onPrimary = Color.White,
    primaryContainer = Color(0xFF047857), // 深绿色
    secondary = Color(0xFF10B981),        // 绿色 - 用于收入
    onSecondary = Color.White,
    error = Color(0xFF111827),            // 深灰色 - 用于支出
    surface = Color.White,
    background = Color(0xFFF9FAFB),       // 浅灰背景
    onSurface = Color(0xFF111827),
    onBackground = Color(0xFF111827)
)

@Composable
fun LedgerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LedgerColorScheme,
        content = content
    )
}