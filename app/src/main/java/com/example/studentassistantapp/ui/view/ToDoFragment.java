package com.example.studentassistantapp.ui.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentassistantapp.R;
import com.example.studentassistantapp.data.model.AddTaskModel;
import com.example.studentassistantapp.databinding.FragmentToDoBinding;
import com.example.studentassistantapp.ui.view.TaskAdapter;
import com.example.studentassistantapp.ui.viewmodel.AddTaskViewModel;

import java.util.ArrayList;
import java.util.List;

public class ToDoFragment extends Fragment implements TaskAdapter.TaskCompletionListener {
    private FragmentToDoBinding binding;
    private AddTaskViewModel taskViewModel;
    private TaskAdapter taskAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentToDoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
        taskViewModel = new ViewModelProvider(this).get(AddTaskViewModel.class);

        // Setup RecyclerView for tasks
        setupTaskRecyclerView();

        // Observe tasks from ViewModel
        observeTasks();

        // Setup Add Task Button
        binding.btnAddTask.setOnClickListener(v -> {
            // Implement navigation to add task screen
            Navigation.findNavController(v).navigate(R.id.action_toDoFragment_to_addTaskFragment);
        });

        // Handle View All text click to toggle visibility of tasks
        binding.viewAllTasksText.setOnClickListener(v -> {
            toggleTasksVisibility();
        });

        // Handle View All text click to toggle visibility of completed tasks
        binding.viewAllCompletedTasksText.setOnClickListener(v -> {
            toggleCompletedTasksVisibility();  // Toggle completed tasks visibility
        });
    }

    private void toggleTasksVisibility() {
        RecyclerView taskRecyclerView = binding.taskRecyclerView;
        TextView viewAllText = binding.viewAllTasksText;

        // Toggle the visibility of the tasks RecyclerView
        if (taskRecyclerView.getVisibility() == View.GONE) {
            taskRecyclerView.setVisibility(View.VISIBLE);  // Show the tasks
            viewAllText.setText("Collapse");  // Change text to "Collapse" when expanded
        } else {
            taskRecyclerView.setVisibility(View.GONE);  // Hide the tasks
            viewAllText.setText("View All");  // Change text to "View All" when collapsed
        }
    }

    private void toggleCompletedTasksVisibility() {
        LinearLayout completedTasksContainer = binding.completedTasksContainer;
        TextView viewAllCompletedText = binding.viewAllCompletedTasksText;

        // Toggle the visibility of the completed tasks container
        if (completedTasksContainer.getVisibility() == View.GONE) {
            completedTasksContainer.setVisibility(View.VISIBLE);  // Show the completed tasks
            viewAllCompletedText.setText("Collapse");  // Change text to "Collapse" when expanded
        } else {
            completedTasksContainer.setVisibility(View.GONE);  // Hide the completed tasks
            viewAllCompletedText.setText("View All");  // Change text to "View All" when collapsed
        }
    }

    private void setupTaskRecyclerView() {
        taskAdapter = new TaskAdapter(new ArrayList<>(), this); // 'this' refers to ToDoFragment, which implements TaskCompletionListener
        binding.taskRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.taskRecyclerView.setAdapter(taskAdapter);
    }

    private void observeTasks() {
        taskViewModel.getTasksList().observe(getViewLifecycleOwner(), tasks -> {
            // Log the number of tasks retrieved
            Log.d("ToDoFragment", "Tasks retrieved: " + (tasks != null ? tasks.size() : "null"));

            if (tasks != null && !tasks.isEmpty()) {
                // Update the adapter with the new list of tasks
                taskAdapter.updateTasks(tasks);
            }
        });
    }

    @Override
    public void onTaskCompletionChanged(AddTaskModel task, boolean isCompleted) {
        task.setCompleted(isCompleted);
        taskViewModel.updateTaskCompletion(task);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Prevent memory leaks by nullifying the binding object
        binding = null;
    }
}
