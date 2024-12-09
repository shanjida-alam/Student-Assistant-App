package com.example.studentassistantapp.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.studentassistantapp.data.model.AddTaskModel;
import com.example.studentassistantapp.data.repository.TaskRepository;

import java.util.List;

public class AddTaskViewModel extends ViewModel {
    private final TaskRepository taskRepository;
    private final MutableLiveData<List<AddTaskModel>> tasksList;

    public AddTaskViewModel() {
        taskRepository = new TaskRepository();
        tasksList = new MutableLiveData<>();
        loadTasks();
    }

    private void loadTasks() {
        taskRepository.getTasks().observeForever(tasks -> {
            tasksList.setValue(tasks);
        });
    }

    public LiveData<List<AddTaskModel>> getTasksList() {
        return tasksList;
    }

    public LiveData<AddTaskModel> insertTask(AddTaskModel task) {
        return taskRepository.insertTask(task);
    }

    public void updateTaskCompletion(AddTaskModel task) {
        taskRepository.updateTaskCompletion(task);
    }
}