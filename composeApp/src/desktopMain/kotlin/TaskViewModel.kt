import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class TaskViewModel : ViewModel() {
    private val _tasks = MutableStateFlow<List<String>>(emptyList())
    private val _query = MutableStateFlow("")

    val tasks: Flow<List<String>> = combine(_tasks, _query) { taskList, query ->
        if (query.isBlank()) taskList
        else taskList.filter { it.contains(query, ignoreCase = true) }
    }

    private val file = File("tasks.txt")

    init {
        CoroutineScope(Dispatchers.IO).launch {
            val loadedTasks = loadTasksFromFile()
            _tasks.update { loadedTasks }
        }
    }

    fun addTask(task: String) {
        _tasks.update { it + task }
        saveTasksToFile()
    }

    fun editTask(oldTask: String, newTask: String) {
        _tasks.update { it.map { if (it == oldTask) newTask else it } }
        saveTasksToFile()
    }

    fun deleteTask(task: String) {
        _tasks.update { it - task }
        saveTasksToFile()
    }

    fun setQuery(query: String) {
        _query.value = query
    }

    private fun saveTasksToFile() {
        CoroutineScope(Dispatchers.IO).launch {
            file.writeText(_tasks.value.joinToString("\n"))
        }
    }

    private fun loadTasksFromFile(): List<String> {
        if (!file.exists()) return emptyList()
        return file.readLines()
    }
}