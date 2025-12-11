package com.example.appqlchitieu.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "category_table",
    indices = [Index("userId")]
)
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    //  chủ sở hữu danh mục
    val userId: Int,

    val name: String,
    val type: String
)
