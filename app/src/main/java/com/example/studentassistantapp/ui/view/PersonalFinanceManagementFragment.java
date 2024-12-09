package com.example.studentassistantapp.ui.view;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentassistantapp.R;
import com.example.studentassistantapp.databinding.FragmentPersonalFinanceManagementBinding;
import com.example.studentassistantapp.ui.view.adapter.ExpenseAdapter;
import com.example.studentassistantapp.ui.viewmodel.PersonalFinanceViewModel;
import com.github.mikephil.charting.charts.PieChart;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PersonalFinanceManagementFragment extends Fragment {
    private PersonalFinanceViewModel viewModel;
    private RecyclerView expensesRecyclerView;
    private ExpenseAdapter adapter;
    private PieChart pieChart;
    private EditText etAmount, etDescription, etDate, etTime;
    private Spinner categorySpinner;
    private long selectedTimestamp;
    private FragmentPersonalFinanceManagementBinding binding;
    private Calendar calendar;
    private MaterialCardView addExpenseCard;
    private FloatingActionButton fabAddExpense;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPersonalFinanceManagementBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(PersonalFinanceViewModel.class);
        calendar = Calendar.getInstance();

        initializeViews();
        setupRecyclerView();
        setupObservers();
        setupSpinner();
        setupDateTimePickers();
        setupAddExpenseButton();
        setupFab();

        viewModel.loadExpenses();
    }

    private void initializeViews() {
        pieChart = binding.pieChart;
        expensesRecyclerView = binding.expensesRecyclerView;
        etAmount = binding.etAmount;
        etDescription = binding.etDescription;
        etDate = binding.etDate;
        etTime = binding.etTime;
        categorySpinner = binding.categorySpinner;
        addExpenseCard = binding.addExpenseCard;
        fabAddExpense = binding.fabAddExpense;
    }

    private void setupRecyclerView() {
        adapter = new ExpenseAdapter();
        expensesRecyclerView.setAdapter(adapter);
        expensesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.expense_categories,
                android.R.layout.simple_spinner_item
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(spinnerAdapter);
    }

    private void setupFab() {
        fabAddExpense.setOnClickListener(v -> {
            if (addExpenseCard.getVisibility() == View.VISIBLE) {
                // Hide the add expense card with animation
                addExpenseCard.animate()
                        .alpha(0f)
                        .setDuration(300)
                        .withEndAction(() -> {
                            addExpenseCard.setVisibility(View.GONE);
                            fabAddExpense.setImageResource(R.drawable.baseline_add_24);
                        });
            } else {
                // Show the add expense card with animation
                addExpenseCard.setVisibility(View.VISIBLE);
                addExpenseCard.setAlpha(0f);
                addExpenseCard.animate()
                        .alpha(1f)
                        .setDuration(300);
                fabAddExpense.setImageResource(R.drawable.baseline_close_24);
            }
        });
    }

    private void hideAddExpenseForm() {
        addExpenseCard.animate()
                .alpha(0f)
                .setDuration(300)
                .withEndAction(() -> {
                    addExpenseCard.setVisibility(View.GONE);
                    fabAddExpense.setImageResource(R.drawable.baseline_add_24);
                });

        // Clear inputs
        etAmount.setText("");
        etDescription.setText("");
        etDate.setText("");
        etTime.setText("");
        categorySpinner.setSelection(0);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupDateTimePickers() {
        // Handle both field click and icon click for date
        etDate.setOnClickListener(v -> showDatePicker());
        etDate.setOnTouchListener((v, event) -> {
            if (event.getRawX() >= (etDate.getRight() - etDate.getCompoundDrawables()[2].getBounds().width())) {
                showDatePicker();
                return true;
            }
            return false;
        });

        // Handle both field click and icon click for time
        etTime.setOnClickListener(v -> showTimePicker());
        etTime.setOnTouchListener((v, event) -> {
            if (event.getRawX() >= (etTime.getRight() - etTime.getCompoundDrawables()[2].getBounds().width())) {
                showTimePicker();
                return true;
            }
            return false;
        });
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    etDate.setText(dateFormat.format(calendar.getTime()));
                    updateSelectedTimestamp();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                (view, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);

                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    etTime.setText(timeFormat.format(calendar.getTime()));
                    updateSelectedTimestamp();
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true // 24-hour format
        );
        timePickerDialog.show();
    }

    private void updateSelectedTimestamp() {
        selectedTimestamp = calendar.getTimeInMillis();
    }

    private void setupObservers() {
        viewModel.getExpenses().observe(getViewLifecycleOwner(), expenses -> {
            adapter.submitList(expenses);
        });

        viewModel.getCategoryTotals().observe(getViewLifecycleOwner(), this::updatePieChart);

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
        });
    }

    private void setupAddExpenseButton() {
        binding.btnAddExpense.setOnClickListener(v -> {
            String amountStr = etAmount.getText().toString();
            String description = etDescription.getText().toString();
            String category = categorySpinner.getSelectedItem().toString();
            String dateStr = etDate.getText().toString();
            String timeStr = etTime.getText().toString();

            if (amountStr.isEmpty()) {
                etAmount.setError("Please enter amount");
                return;
            }

            if (description.isEmpty()) {
                etDescription.setError("Please enter description");
                return;
            }

            if (dateStr.isEmpty()) {
                etDate.setError("Please select date");
                return;
            }

            if (timeStr.isEmpty()) {
                etTime.setError("Please select time");
                return;
            }

            double amount = Double.parseDouble(amountStr);
            viewModel.addExpense(category, amount, description, selectedTimestamp);

            // Hide form and clear inputs after adding expense
            hideAddExpenseForm();
        });
    }

    private void updatePieChart(Map<String, Double> categoryTotals) {
        List<com.github.mikephil.charting.data.PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            entries.add(new com.github.mikephil.charting.data.PieEntry(entry.getValue().floatValue(), entry.getKey()));
        }

        com.github.mikephil.charting.data.PieDataSet dataSet = new com.github.mikephil.charting.data.PieDataSet(entries, "Expenses by Category");
        dataSet.setColors(com.github.mikephil.charting.utils.ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(12f);

        com.github.mikephil.charting.data.PieData data = new com.github.mikephil.charting.data.PieData(dataSet);
        data.setValueFormatter(new com.github.mikephil.charting.formatter.PercentFormatter(pieChart));

        pieChart.setData(data);
        pieChart.setUsePercentValues(true);
        pieChart.setDescription(null);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.setHoleRadius(50f);
        pieChart.setTransparentCircleRadius(55f);
        pieChart.animateY(1000);
        pieChart.invalidate();
    }
}