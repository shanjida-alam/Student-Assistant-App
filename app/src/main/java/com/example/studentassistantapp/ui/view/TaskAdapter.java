package com.example.studentassistantapp.ui.view;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentassistantapp.R;
import com.example.studentassistantapp.data.model.AddTaskModel;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private final List<AddTaskModel> taskList;
    private final TaskCompletionListener completionListener;

    public TaskAdapter(List<AddTaskModel> taskList, TaskCompletionListener listener) {
        this.taskList = taskList;
        this.completionListener = listener;
    }


    // Listener interface to handle task completion
    public interface TaskCompletionListener {
        void onTaskCompletionChanged(AddTaskModel task, boolean isCompleted);
    }

    public TaskAdapter(TaskCompletionListener listener) {
        this.taskList = new ArrayList<>();
        this.completionListener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateTasks(List<AddTaskModel> newTasks) {
        // Clear the existing task list and add new tasks
        this.taskList.clear();
        if (newTasks != null) {
            this.taskList.addAll(newTasks);
        }
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateCompletedTasks(List<AddTaskModel> completedTasks) {
        // Update the list of completed tasks
        List<AddTaskModel> completedList = new ArrayList<>();
        for (AddTaskModel task : taskList) {
            if (task.isCompleted()) {
                completedList.add(task);
            }
        }
        completedTasks.clear();
        completedTasks.addAll(completedList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false); // Reference your task layout here
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        if (!taskList.isEmpty()) {
            AddTaskModel task = taskList.get(position);
            holder.bind(task, completionListener);
        }
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView taskTitle, taskDescription, taskDueDate;
        private final CheckBox checkBoxTask;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize views from `task_item.xml`
            taskTitle = itemView.findViewById(R.id.taskTitle);
            taskDescription = itemView.findViewById(R.id.taskDescription);
            taskDueDate = itemView.findViewById(R.id.taskDueDate);
            checkBoxTask = itemView.findViewById(R.id.checkBoxTask);
        }

        public void bind(AddTaskModel task, TaskCompletionListener listener) {
            // Bind task data to views
            taskTitle.setText(task.getTaskTitle());
            taskDescription.setText(task.getTaskDescription());
            taskDueDate.setText("Due: " + task.getTaskDate());
            checkBoxTask.setChecked(task.isCompleted());

            // Handle task completion
            checkBoxTask.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null) {
                    task.setCompleted(isChecked);
                    listener.onTaskCompletionChanged(task, isChecked);
                }
            });
        }
    }
}
