
package com.example.appqlchitieu.model

import androidx.room.Entity
import androidx.room.PrimaryKey



@Entity(tableName = "user_table")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val email: String,
    val phone: String?,
    val password: String,              // Sau có thể mã hóa
    val avatarUri: String? = null,
    val address: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val isVerified: Boolean = false,   // xác thực email
    val isLoggedIn: Boolean = false    // ghi nhớ đăng nhập
)
