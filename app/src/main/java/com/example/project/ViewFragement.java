package com.example.project;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ViewFragement extends Fragment {

    RecyclerView recyclerView;
    ArrayList<Expense> list;
    ExpenseAdapter adapter;
    detabaseHelper dbHelper;
    TextView tvTotal;
    Button btnDeleteAll;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragement_view, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        tvTotal = view.findViewById(R.id.total);
        btnDeleteAll = view.findViewById(R.id.btnDeleteAll);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        dbHelper = new detabaseHelper(getContext());
        loadExpenses();

        btnDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteAllDialog();
            }
        });

        return view;
    }

    private void loadExpenses() {
        list = new ArrayList<>();
        Cursor cursor = dbHelper.getAllExpense();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(detabaseHelper.COL_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(detabaseHelper.COL_TITLE));
                String amount = cursor.getString(cursor.getColumnIndexOrThrow(detabaseHelper.COL_AMOUNT));
                String category = cursor.getString(cursor.getColumnIndexOrThrow(detabaseHelper.COL_CATEGORY));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(detabaseHelper.COL_DATE));

                list.add(new Expense(id, title, amount, category, date));
            } while (cursor.moveToNext());
            cursor.close();
        }

        adapter = new ExpenseAdapter(getContext(), list, new ExpenseAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(Expense expense, int position) {
                showDeleteDialog(expense, position);
            }
        });
        recyclerView.setAdapter(adapter);

        updateTotal();
    }

    private void showDeleteDialog(Expense expense, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete Expense");
        builder.setMessage("Are you sure you want to delete this expense?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dbHelper.deleteExpense(expense.getId())) {
                    list.remove(position);
                    adapter.notifyItemRemoved(position);
                    updateTotal();
                    Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showDeleteAllDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete All");
        builder.setMessage("Are you sure you want to delete all expenses?");
        builder.setPositiveButton("Delete All", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dbHelper.deleteAllExpenses();
                list.clear();
                adapter.notifyDataSetChanged();
                updateTotal();
                Toast.makeText(getContext(), "All Expenses Deleted", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void updateTotal() {
        double total = dbHelper.getTotalAmount();
        tvTotal.setText(String.format(java.util.Locale.getDefault(), "₹ %.2f", total));
    }
}
