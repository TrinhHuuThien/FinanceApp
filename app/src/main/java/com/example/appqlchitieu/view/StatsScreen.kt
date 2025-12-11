@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.appqlchitieu.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.appqlchitieu.database.DatabaseProvider
import com.example.appqlchitieu.model.Category
import com.example.appqlchitieu.model.Expense
import com.example.appqlchitieu.utils.SessionManager
import com.example.appqlchitieu.utils.UserSession
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

enum class StatMode { EXPENSE, INCOME }

@Composable
fun StatsScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val db = remember { DatabaseProvider.getDatabase(context) }

    val sessionManager = remember { SessionManager(context) }
    val userSession = remember { UserSession(sessionManager) }
    val userId = userSession.userIdOrNull()

    if (userId == null) {
        Box(
            modifier = modifier.fillMaxWidth().height(120.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Bạn chưa đăng nhập", color = Color.Gray)
        }
        return
    }

    val nf = remember { NumberFormat.getInstance(Locale("vi", "VN")) }

    val now = remember { Calendar.getInstance() }
    var year by rememberSaveable { mutableStateOf(now.get(Calendar.YEAR)) }
    var month by rememberSaveable { mutableStateOf(now.get(Calendar.MONTH)) }

    val (start, end) = remember(year, month) { monthBounds(year, month) }
    val monthLabel = remember(start) {
        SimpleDateFormat("MM/yyyy", Locale.getDefault()).format(Date(start))
    }

    var mode by rememberSaveable { mutableStateOf(StatMode.EXPENSE) }

    val categories by db.categoryDao()
        .getAllCategories(userId)
        .collectAsState(initial = emptyList())

    val expenses by db.expenseDao()
        .getExpensesByDateRange(userId, start, end)
        .collectAsState(initial = emptyList())

    val filtered = remember(expenses, mode) {
        val t = if (mode == StatMode.EXPENSE) "expense" else "income"
        expenses.filter { it.type == t }
    }

    val byCategory: List<Pair<Category, Double>> = remember(filtered, categories) {
        val map = filtered.groupBy { it.categoryId }
            .mapValues { (_, list) -> list.sumOf { it.amount } }

        map.mapNotNull { (cid, total) ->
            categories.find { it.id == cid }?.let { it to total }
        }.sortedByDescending { it.second }
    }

    val total = remember(byCategory) { byCategory.sumOf { it.second } }

    val palette = listOf(
        Color(0xFF42A5F5), Color(0xFF66BB6A), Color(0xFFFFA726),
        Color(0xFFEF5350), Color(0xFFAB47BC), Color(0xFF26C6DA),
        Color(0xFF7E57C2), Color(0xFFFF7043), Color(0xFF9CCC65),
        Color(0xFF29B6F6)
    )
    val trackColor = Color(0xFFE8EEF3)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(Color(0xFFE3F2FD), Color(0xFFBBDEFB))))
            .padding(16.dp)
    ) {
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(6.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = {
                        if (month == 0) { month = 11; year -= 1 } else month -= 1
                    }) { Icon(Icons.Filled.ChevronLeft, contentDescription = "Tháng trước") }

                    Text(monthLabel, style = MaterialTheme.typography.titleMedium)

                    IconButton(onClick = {
                        if (month == 11) { month = 0; year += 1 } else month += 1
                    }) { Icon(Icons.Filled.ChevronRight, contentDescription = "Tháng sau") }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = mode == StatMode.EXPENSE,
                    onClick = { mode = StatMode.EXPENSE },
                    label = { Text("Chi") }
                )
                FilterChip(
                    selected = mode == StatMode.INCOME,
                    onClick = { mode = StatMode.INCOME },
                    label = { Text("Thu") }
                )
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.fillMaxWidth().padding(16.dp)) {
                    val title = if (mode == StatMode.EXPENSE) "Tổng chi" else "Tổng thu"
                    val color = if (mode == StatMode.EXPENSE) Color(0xFFE53935) else Color(0xFF1E88E5)

                    Text(title, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                    Text("${nf.format(total.toLong())}₫", color = color, style = MaterialTheme.typography.titleLarge)

                    Spacer(Modifier.height(12.dp))

                    if (total <= 0.0 || byCategory.isEmpty()) {
                        Box(Modifier.fillMaxWidth().height(180.dp), contentAlignment = Alignment.Center) {
                            Text("Không có dữ liệu trong tháng", color = Color.Gray)
                        }
                    } else {
                        val maxVal = byCategory.maxOf { it.second }
                        val bars = byCategory.take(12)

                        val barWidth = 28.dp
                        val chartHeight = 180.dp

                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            items(bars) { (cat, value) ->
                                val frac = (value / maxVal).toFloat().coerceIn(0f, 1f)

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Bottom,
                                    modifier = Modifier.height(chartHeight)
                                ) {
                                    Text(
                                        text = nf.format(value.toLong()),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.Gray,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(Modifier.height(6.dp))

                                    Box(
                                        modifier = Modifier
                                            .width(barWidth)
                                            .fillMaxHeight(0.78f)
                                            .background(trackColor, RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.BottomCenter
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .fillMaxHeight(frac)
                                                .background(
                                                    palette[(cat.id + 7) % palette.size],
                                                    RoundedCornerShape(8.dp)
                                                )
                                        )
                                    }

                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        text = cat.name,
                                        style = MaterialTheme.typography.labelSmall,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.widthIn(max = 64.dp),
                                    )
                                }
                            }
                        }

                        if (byCategory.size > bars.size) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "+${byCategory.size - bars.size} danh mục khác…",
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun monthBounds(year: Int, month0: Int): Pair<Long, Long> {
    val c1 = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month0)
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }
    val start = c1.timeInMillis

    val c2 = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month0)
        set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
        set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59); set(Calendar.SECOND, 59); set(Calendar.MILLISECOND, 999)
    }
    return start to c2.timeInMillis
}
