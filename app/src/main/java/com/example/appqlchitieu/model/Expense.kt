package com.example.appqlchitieu.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "expense_table",
    indices = [
        Index("userId"),
        Index(value = ["userId", "categoryId"]),
        Index(value = ["userId", "walletId"])
    ]
)
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    // giao dịch thuộc user nào
    val userId: Int,

    val title: String,
    val amount: Double,
    val categoryId: Int,
    val walletId: Int,
    val date: Long,
    val type: String
)
