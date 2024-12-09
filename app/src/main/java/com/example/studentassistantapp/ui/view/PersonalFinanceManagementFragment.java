package com.example.studentassistantapp.ui.view;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Fragment class (PersonalFinanceManagementFragment.java)
public class PersonalFinanceManagementFragment extends Fragment {
    private PersonalFinanceViewModel viewModel;
    private RecyclerView expensesRecyclerView;
    private ExpenseAdapter adapter;
    private PieChart pieChart;
    private EditText etAmount, etDescription;
    private Spinner categorySpinner;
    private FragmentPersonalFinanceManagementBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPersonalFinanceManagementBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(PersonalFinanceViewModel.class);
        initializeViews();
        setupRecyclerView();
        setupObservers();
        setupSpinner();
        setupAddExpenseButton();

        viewModel.loadExpenses();
    }

    private void initializeViews() {
        pieChart = binding.pieChart;
        expensesRecyclerView = binding.expensesRecyclerView;
        etAmount = binding.etAmount;
        etDescription = binding.etDescription;
        categorySpinner = binding.categorySpinner;
    }

    private void setupRecyclerView() {
        adapter = new ExpenseAdapter();
        expensesRecyclerView.setAdapter(adapter);
        expensesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
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

    private void setupSpinner() {
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.expense_categories,
                android.R.layout.simple_spinner_item
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(spinnerAdapter);
    }

    private void setupAddExpenseButton() {
        binding.btnAddExpense.setOnClickListener(v -> {
            String amountStr = etAmount.getText().toString();
            String description = etDescription.getText().toString();
            String category = categorySpinner.getSelectedItem().toString();

            if (amountStr.isEmpty()) {
                etAmount.setError("Please enter amount");
                return;
            }

            double amount = Double.parseDouble(amountStr);
            viewModel.addExpense(category, amount, description);

            // Clear inputs
            etAmount.setText("");
            etDescription.setText("");
            categorySpinner.setSelection(0);
        });
    }

    private void updatePieChart(Map<String, Double> categoryTotals) {
        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            entries.add(new PieEntry(entry.getValue().floatValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Expenses by Category");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(12f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart));

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