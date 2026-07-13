package com.xinkong.ledger

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.xinkong.ledger.data.getDatabaseBuilder

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Ledger",
    ) {
        App(getDatabaseBuilder())
    }
}
