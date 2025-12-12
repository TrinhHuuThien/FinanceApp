package com.example.appqlchitieu.viewmodel

import androidx.lifecycle.*
import com.example.appqlchitieu.model.Budget
import com.example.appqlchitieu.repository.BudgetRepository
import com.example.appqlchitieu.utils.SessionManager
import com.example.appqlchitieu.utils.UserSession
import kotlinx.coroutines.launch

class BudgetViewModel(
    private val repo: BudgetRepository,
    sessionManager: SessionManager
) : ViewModel() {

    private val userSession = UserSession(sessionManager)

    private val _userId = MutableLiveData<Int?>().apply {
        value = userSession.userIdOrNull()
    }

    val allBudgets: LiveData<List<Budget>> =
        _userId.switchMap { uid ->
            if (uid != null && uid > 0) repo.allBudgets(uid).asLiveData()
            else MutableLiveData(emptyList())
        }

    fun byCategory(categoryId: Int): LiveData<List<Budget>> {
        val uid = _userId.value ?: -1
        return if (uid > 0) repo.byCategory(uid, categoryId).asLiveData()
        else MutableLiveData(emptyList())
    }

    fun activeNow(): LiveData<List<Budget>> {
        val uid = _userId.value ?: -1
        return if (uid > 0) repo.activeAt(uid, System.currentTimeMillis()).asLiveData()
        else MutableLiveData(emptyList())
    }
}

class BudgetViewModelFactory(
    private val repo: BudgetRepository,
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BudgetViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BudgetViewModel(repo, sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
