package com.example.studentassistantapp.ui.view;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.studentassistantapp.R;
import com.example.studentassistantapp.data.model.AddTaskModel;
import com.example.studentassistantapp.ui.viewmodel.AddTaskViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

public class AddTaskFragment extends Fragment {

    private EditText editTextTaskTitle;
    private EditText editTextTaskDescription;
    private Spinner spinnerTaskCategory;
    private TextView textViewSelectedDate;
    private TextView textViewSelectedTime;
    private ImageButton btnSelectDate;
    private ImageButton btnSelectTime;
    private Button btnSaveTask;

    private AddTaskViewModel addTaskViewModel;
    private Calendar selectedCalendar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_task, container, false);

        // Initialize ViewModel
        addTaskViewModel = new ViewModelProvider(this).get(AddTaskViewModel.class);

        // Initialize Views
        initializeViews(view);

        // Setup Category Spinner
        setupCategorySpinner();

        // Setup Button Listeners
        setupButtonListeners();

        // Initialize Calendar
        selectedCalendar = Calendar.getInstance();

        return view;
    }

    private void initializeViews(View view) {
        editTextTaskTitle = view.findViewById(R.id.editTextTaskTitle);
        editTextTaskDescription = view.findViewById(R.id.editTextTaskDescription);
        spinnerTaskCategory = view.findViewById(R.id.spinnerTaskCategory);
        textViewSelectedDate = view.findViewById(R.id.textViewSelectedDate);
        textViewSelectedTime = view.findViewById(R.id.textViewSelectedTime);
        btnSelectDate = view.findViewById(R.id.btnSelectDate);
        btnSelectTime = view.findViewById(R.id.btnSelectTime);
        btnSaveTask = view.findViewById(R.id.btnSaveTask);
    }

    private void setupCategorySpinner() {
        String[] categories = {"Study", "Personal", "Work", "Other"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categories
        );
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTaskCategory.setAdapter(categoryAdapter);
    }

    private void setupButtonListeners() {
        // Date Selection
        btnSelectDate.setOnClickListener(v -> showDatePickerDialog());

        // Time Selection
        btnSelectTime.setOnClickListener(v -> showTimePickerDialog());

        // Save Task
        btnSaveTask.setOnClickListener(v -> saveTask(v));
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, monthOfYear, dayOfMonth) -> {
                    selectedCalendar.set(Calendar.YEAR, year);
                    selectedCalendar.set(Calendar.MONTH, monthOfYear);
                    selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    // Update TextView with selected date
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    textViewSelectedDate.setText(dateFormat.format(selectedCalendar.getTime()));
                },
                selectedCalendar.get(Calendar.YEAR),
                selectedCalendar.get(Calendar.MONTH),
                selectedCalendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                (view, hourOfDay, minute) -> {
                    selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedCalendar.set(Calendar.MINUTE, minute);

                    // Update TextView with selected time
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    textViewSelectedTime.setText(timeFormat.format(selectedCalendar.getTime()));
                },
                selectedCalendar.get(Calendar.HOUR_OF_DAY),
                selectedCalendar.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }

    private void saveTask(View view) {
        // Get input values
        String taskTitle = editTextTaskTitle.getText().toString().trim();
        String taskDescription = editTextTaskDescription.getText().toString().trim();
        String taskCategory = spinnerTaskCategory.getSelectedItem().toString();



        // Validate inputs
        if (taskTitle.isEmpty()) {
            editTextTaskTitle.setError("Task title is required");
            return;
        }

        // Validate date and time selections
        if (textViewSelectedDate.getText().toString().equals("Select Date")) {
            Toast.makeText(requireContext(), "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }

        if (textViewSelectedTime.getText().toString().equals("Select Time")) {
            Toast.makeText(requireContext(), "Please select a time", Toast.LENGTH_SHORT).show();
            return;
        }

        // Format date and time
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        // Create task model
        AddTaskModel newTask = new AddTaskModel(
                UUID.randomUUID().toString(), // Generate unique task ID
                taskTitle,
                taskDescription,
                dateFormat.format(selectedCalendar.getTime()),
                timeFormat.format(selectedCalendar.getTime()),
                taskCategory,
                false // Task is not completed by default
        );

        // Use ViewModel to save task
        addTaskViewModel.insertTask(newTask).observe(getViewLifecycleOwner(), addedTask -> {
            if (addedTask != null) {
                // Task successfully added
                // Optionally show a success message
                Toast.makeText(requireContext(), "Task Added Successfully", Toast.LENGTH_SHORT).show();

                // Navigate back to the previous fragment
                Navigation.findNavController(requireView()).navigateUp();
            } else {
                // Handle task addition failure
                Toast.makeText(requireContext(), "Failed to add task", Toast.LENGTH_SHORT).show();
            }
        });
    }
}