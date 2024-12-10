package com.example.studentassistantapp.ui.view.adapter;

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

    private final boolean showCompletedTasks; // Add this flag
    // Modify constructor to include showCompletedTasks flag
    public TaskAdapter(List<AddTaskModel> taskList, TaskCompletionListener listener, boolean showCompletedTasks) {
        this.taskList = taskList;
        this.completionListener = listener;
        this.showCompletedTasks = showCompletedTasks;
    }


    // Listener interface to handle task completion
    public interface TaskCompletionListener {
        void onTaskCompletionChanged(AddTaskModel task, boolean isCompleted);
    }

    public TaskAdapter(TaskCompletionListener listener, boolean showCompletedTasks) {
        this.showCompletedTasks = showCompletedTasks;
        this.taskList = new ArrayList<>();
        this.completionListener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateTasks(List<AddTaskModel> newTasks) {
        taskList.clear();
        if (newTasks != null) {
            // Only add tasks that match our completion status
            for (AddTaskModel task : newTasks) {
                if (task.isCompleted() == showCompletedTasks) {
                    taskList.add(task);
                }
            }
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
        // Use different layouts for active and completed tasks
        int layoutId = showCompletedTasks ? R.layout.task_completed_item : R.layout.item_task;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new TaskViewHolder(view, showCompletedTasks);
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

        public TaskViewHolder(@NonNull View itemView, boolean isCompletedTask) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.taskTitle);
            taskDescription = itemView.findViewById(R.id.taskDescription);
            taskDueDate = itemView.findViewById(R.id.taskDueDate);
            // Only initialize checkbox for active tasks
            checkBoxTask = isCompletedTask ? null : itemView.findViewById(R.id.checkBoxTask);
        }

        public void bind(AddTaskModel task, TaskCompletionListener listener) {
            taskTitle.setText(task.getTaskTitle());
            taskDescription.setText(task.getTaskDescription());
            taskDueDate.setText("Due: " + task.getTaskDate());

            // Only setup checkbox for active tasks
            if (checkBoxTask != null) {
                checkBoxTask.setChecked(task.isCompleted());
                checkBoxTask.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (listener != null) {
                        listener.onTaskCompletionChanged(task, isChecked);
                    }
                });
            }
        }
    }

}