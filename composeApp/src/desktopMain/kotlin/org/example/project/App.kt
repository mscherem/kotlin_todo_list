package org.example.project

import TaskViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
@Preview
fun App() {
    MaterialTheme {
        val viewModel: TaskViewModel = viewModel()
        val tasks by viewModel.tasks.collectAsState(initial = emptyList())
        var newTask by remember { mutableStateOf("") }
        var searchQuery by remember { mutableStateOf("") }
        var taskBeingEdited by remember { mutableStateOf<String?>(null) }

        Column(modifier = Modifier.padding(16.dp)) {
            Text("Liste", style = MaterialTheme.typography.h4)
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    viewModel.setQuery(it)
                },
                placeholder = { Text("Aufgaben durchsuchen") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row {
                TextField(
                    value = newTask,
                    onValueChange = { newTask = it },
                    placeholder = { Text("Aufgabe eingeben")},
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    if (newTask.isNotBlank()) {
                        if (taskBeingEdited != null) {
                            viewModel.editTask(taskBeingEdited!!, newTask)
                            taskBeingEdited = null
                        } else {
                            viewModel.addTask(newTask)
                        }
                        newTask = ""
                    }
                }) {
                    Text(if (taskBeingEdited != null) "Speichern" else "Hinzufügen")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(tasks) { task ->
                    TaskItem(
                        task = task,
                        onEdit = { taskBeingEdited = it; newTask = it },
                        onDelete = {
                            viewModel.deleteTask(task)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TaskItem(task: String, onEdit: (String) -> Unit, onDelete: () -> Unit) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(task, style = MaterialTheme.typography.body1, modifier = Modifier.padding(8.dp))
        Row {
            Button(onClick = { onEdit(task) }) {
                Text("Bearbeiten")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { onDelete() }) {
                Text("Löschen")
            }
        }
    }
}