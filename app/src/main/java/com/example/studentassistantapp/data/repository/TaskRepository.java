package com.example.studentassistantapp.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.studentassistantapp.data.model.AddTaskModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TaskRepository {
    private final FirebaseFirestore firestore;

    public TaskRepository() {
        firestore = FirebaseFirestore.getInstance();
    }

    public LiveData<AddTaskModel> insertTask(AddTaskModel task) {
        MutableLiveData<AddTaskModel> resultLiveData = new MutableLiveData<>();


        firestore.collection("tasks")
                .document(task.getTaskId())
                .set(task)
                .addOnSuccessListener(unused -> {
                    // Return the task that was just added
                    resultLiveData.setValue(task);
                })
                .addOnFailureListener(e -> {
                    // Log the error
                    Log.e("TaskRepository", "Error inserting task", e);
                    resultLiveData.setValue(null);
                });

        return resultLiveData;
    }

    public LiveData<List<AddTaskModel>> getTasks() {
        MutableLiveData<List<AddTaskModel>> tasksLiveData = new MutableLiveData<>();

        firestore.collection("tasks")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<AddTaskModel> taskList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        AddTaskModel task = document.toObject(AddTaskModel.class);
                        task.setTaskId(document.getId());
                        taskList.add(task);
                    }
                    tasksLiveData.setValue(taskList);
                })
                .addOnFailureListener(e -> {
                    Log.e("TaskRepository", "Error fetching tasks", e);
                    tasksLiveData.setValue(new ArrayList<>());
                });

        return tasksLiveData;
    }

    public void updateTaskCompletion(AddTaskModel task) {
        firestore.collection("tasks")
                .document(task.getTaskId())
                .update("completed", task.isCompleted())
                .addOnSuccessListener(unused -> {
                    // Success handling
                })
                .addOnFailureListener(e -> {
                    // Error handling
                });
    }

}