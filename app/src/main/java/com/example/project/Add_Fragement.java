package com.example.project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class Add_Fragement extends Fragment {
    EditText etTitle, etAmount, etCategory, etDate;
    Button btnSave;
    detabaseHelper dbHelper;

    public Add_Fragement() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add__fragement, container, false);

        etTitle = view.findViewById(R.id.etTittle);
        etAmount = view.findViewById(R.id.etAmount);
        etCategory = view.findViewById(R.id.etCategory);
        etDate = view.findViewById(R.id.etDate);
        btnSave = view.findViewById(R.id.save);

        dbHelper = new detabaseHelper(getContext());


        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMaterialDatePicker();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = etTitle.getText().toString();
                String amount = etAmount.getText().toString();
                String category = etCategory.getText().toString();
                String date = etDate.getText().toString();

                if (title.isEmpty() || amount.isEmpty() || category.isEmpty() || date.isEmpty()) {
                    Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    boolean inserted = dbHelper.insertExpense(title, amount, category, date);
                    if (inserted) {
                        Toast.makeText(getContext(), "Expense Added", Toast.LENGTH_SHORT).show();
                        etTitle.setText("");
                        etAmount.setText("");
                        etCategory.setText("");
                        etDate.setText("");
                    } else {
                        Toast.makeText(getContext(), "Error saving expense", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return view;
    }

    private void showMaterialDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                calendar.setTimeInMillis(selection);
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                String formattedDate = format.format(calendar.getTime());
                etDate.setText(formattedDate);
            }
        });

        datePicker.show(getChildFragmentManager(), "DATE_PICKER");
    }
}
