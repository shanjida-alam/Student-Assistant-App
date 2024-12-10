package com.example.studentassistantapp.data.repository;

import com.example.studentassistantapp.data.model.ExpenseModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class ExpenseRepository {
    private static final String COLLECTION_EXPENSES = "expenses";
    private final FirebaseFirestore db;

    public ExpenseRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public Task<DocumentReference> addExpense(ExpenseModel expense) {
        return db.collection(COLLECTION_EXPENSES).add(expense);
    }

    public Task<QuerySnapshot> getAllExpenses() {
        return db.collection(COLLECTION_EXPENSES)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get();
    }

    public Task<QuerySnapshot> getExpensesByCategory(String category) {
        return db.collection(COLLECTION_EXPENSES)
                .whereEqualTo("category", category)
                .get();
    }
}