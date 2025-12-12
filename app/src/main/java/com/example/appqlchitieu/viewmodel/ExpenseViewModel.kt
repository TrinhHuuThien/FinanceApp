package com.example.appqlchitieu.viewmodel

import androidx.lifecycle.*
import com.example.appqlchitieu.model.Expense
import com.example.appqlchitieu.repository.ExpenseRepository
import kotlinx.coroutines.launch

/**
 * Quản lý giao dịch (Expense) theo USER hiện tại.
 */
class ExpenseViewModel(
    private val repo: ExpenseRepository,
    private val userId: Int
) : ViewModel() {

    /** Danh sách giao dịch của USER hiện tại */
    val allExpenses = repo.allExpenses(userId).asLiveData()

    fun insert(expense: Expense) = viewModelScope.launch {
        repo.insert(expense)
    }

    fun update(expense: Expense) = viewModelScope.launch {
        repo.update(expense)
    }

    fun delete(expense: Expense) = viewModelScope.launch {
        repo.delete(expense)
    }

    /** Lấy 1 giao dịch theo id + user */
    suspend fun getById(id: Int) =
        repo.getById(userId, id)

    /** Lọc theo danh mục + user */
    fun byCategory(categoryId: Int) =
        repo.byCategory(userId, categoryId).asLiveData()

    /** Lọc theo khoảng ngày + user */
    fun byDateRange(start: Long, end: Long) =
        repo.byDateRange(userId, start, end).asLiveData()

    /** Tổng chi/thu toàn thời gian theo user */
    val totalExpense = repo.totalExpense(userId).asLiveData()
    val totalIncome  = repo.totalIncome(userId).asLiveData()

    /** Tổng chi/thu trong khoảng ngày theo user */
    fun totalExpenseBetween(start: Long, end: Long) =
        repo.totalExpenseBetween(userId, start, end).asLiveData()

    fun totalIncomeBetween(start: Long, end: Long) =
        repo.totalIncomeBetween(userId, start, end).asLiveData()
}

class ExpenseViewModelFactory(
    private val repo: ExpenseRepository,
    private val userId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExpenseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExpenseViewModel(repo, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
