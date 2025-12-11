package com.example.appqlchitieu.dao

import androidx.room.*
import com.example.appqlchitieu.model.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category)

    @Update
    suspend fun updateCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)

    // Lấy tất cả danh mục theo user
    @Query("SELECT * FROM category_table WHERE userId = :userId")
    fun getAllCategories(userId: Int): Flow<List<Category>>

    @Query("SELECT * FROM category_table WHERE userId = :userId")
    suspend fun getAllCategoriesOnce(userId: Int): List<Category>

    //  Lấy danh mục theo type + user
    @Query("SELECT * FROM category_table WHERE userId = :userId AND type = :type")
    fun getCategoriesByType(userId: Int, type: String): Flow<List<Category>>

    // Đếm danh mục theo user
    @Query("SELECT COUNT(*) FROM category_table WHERE userId = :userId")
    suspend fun countAll(userId: Int): Int

    @Insert
    suspend fun insertMany(categories: List<Category>)

}
