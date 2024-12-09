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

    private final MutableLiveData<List<AddTaskModel>> tasksLiveData;

    public TaskRepository() {
        firestore = FirebaseFirestore.getInstance();
        tasksLiveData = new MutableLiveData<>();
        // Setup real-time listener immediately
        setupTasksListener();
    }
    private void setupTasksListener() {
        firestore.collection("tasks")
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Log.e("TaskRepository", "Listen failed.", error);
                        return;
                    }

                    if (snapshots != null) {
                        List<AddTaskModel> taskList = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : snapshots) {
                            AddTaskModel task = doc.toObject(AddTaskModel.class);
                            task.setTaskId(doc.getId());
                            taskList.add(task);
                        }
                        tasksLiveData.setValue(taskList);
                    }
                });
    }

    public LiveData<AddTaskModel> insertTask(AddTaskModel task) {
        MutableLiveData<AddTaskModel> resultLiveData = new MutableLiveData<>();

        firestore.collection("tasks")
                .document(task.getTaskId())
                .set(task)
                .addOnSuccessListener(unused -> {
                    resultLiveData.setValue(task);
                })
                .addOnFailureListener(e -> {
                    Log.e("TaskRepository", "Error inserting task", e);
                    resultLiveData.setValue(null);
                });

        return resultLiveData;
    }

    public LiveData<List<AddTaskModel>> getTasks() {
        return tasksLiveData;
    }


    public void updateTaskCompletion(AddTaskModel task) {
        firestore.collection("tasks")
                .document(task.getTaskId())
                .update("completed", task.isCompleted())
                .addOnSuccessListener(unused -> {
                    Log.d("TaskRepository", "Task completion status updated successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e("TaskRepository", "Error updating task completion status", e);
                });
    }

}