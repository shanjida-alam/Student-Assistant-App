package com.example.studentassistantapp.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.studentassistantapp.data.model.ExpenseModel;
import com.example.studentassistantapp.data.repository.ExpenseRepository;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonalFinanceViewModel extends ViewModel {
    private final ExpenseRepository repository;
    private final MutableLiveData<List<ExpenseModel>> expensesLiveData;
    private final MutableLiveData<Map<String, Double>> categoryTotalsLiveData;
    private final MutableLiveData<String> errorLiveData;

    public PersonalFinanceViewModel() {
        repository = new ExpenseRepository();
        expensesLiveData = new MutableLiveData<>();
        categoryTotalsLiveData = new MutableLiveData<>();
        errorLiveData = new MutableLiveData<>();
    }

    public LiveData<List<ExpenseModel>> getExpenses() {
        return expensesLiveData;
    }

    public LiveData<Map<String, Double>> getCategoryTotals() {
        return categoryTotalsLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }

    public void addExpense(String category, double amount, String description, long timestamp) {
        ExpenseModel expense = new ExpenseModel(category, amount, description);
        expense.setTimestamp(timestamp);

        repository.addExpense(expense)
                .addOnSuccessListener(documentReference -> loadExpenses())
                .addOnFailureListener(e -> errorLiveData.setValue("Failed to add expense: " + e.getMessage()));
    }

    public void loadExpenses() {
        repository.getAllExpenses()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<ExpenseModel> expenses = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        ExpenseModel expense = document.toObject(ExpenseModel.class);
                        expense.setId(document.getId());
                        expenses.add(expense);
                    }
                    expensesLiveData.setValue(expenses);
                    calculateCategoryTotals(expenses);
                })
                .addOnFailureListener(e -> errorLiveData.setValue("Failed to load expenses: " + e.getMessage()));
    }

    private void calculateCategoryTotals(List<ExpenseModel> expenses) {
        Map<String, Double> categoryTotals = new HashMap<>();
        for (ExpenseModel expense : expenses) {
            String category = expense.getCategory();
            double currentTotal = categoryTotals.getOrDefault(category, 0.0);
            categoryTotals.put(category, currentTotal + expense.getAmount());
        }
        categoryTotalsLiveData.setValue(categoryTotals);
    }
}