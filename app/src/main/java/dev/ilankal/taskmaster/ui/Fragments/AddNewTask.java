package dev.ilankal.taskmaster.ui.Fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import dev.ilankal.taskmaster.Data.DataManager;
import dev.ilankal.taskmaster.Enum.Category;
import dev.ilankal.taskmaster.Enum.Type;
import dev.ilankal.taskmaster.Interfaces.RequestDb;
import dev.ilankal.taskmaster.R;
import dev.ilankal.taskmaster.ui.Models.Task;

public class AddNewTask extends BottomSheetDialogFragment {

    public static final String TAG = "AddNewTask";

    private EditText taskDescription;
    private Button pickDateButton;
    private Spinner categorySpinner;
    private Spinner typeSpinner;
    private Button saveButton;

    private DatabaseReference databaseReference;

    public static AddNewTask newInstance() {
        return new AddNewTask();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_add_new_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);

        databaseReference = FirebaseDatabase.getInstance().getReference("tasks");

        pickDateButton.setOnClickListener(v -> showDatePickerDialog());
        saveButton.setOnClickListener(v -> saveTask());
    }

    private void findViews(View view) {
        taskDescription = view.findViewById(R.id.taskDescription);
        pickDateButton = view.findViewById(R.id.pickDateButton);
        categorySpinner = view.findViewById(R.id.categorySpinner);
        typeSpinner = view.findViewById(R.id.typeSpinner);
        saveButton = view.findViewById(R.id.saveButton);

        // Set up the Category spinner
        ArrayAdapter<Category> categoryAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                Category.values()
        );
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        // Set up the Type spinner
        ArrayAdapter<Type> typeAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                Type.values()
        );
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeAdapter);
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, year1, month1, dayOfMonth) -> {
                    month1 = month1 + 1;
                    String date = dayOfMonth + "/" + month1 + "/" + year1;
                    pickDateButton.setText(date);
                },
                year, month, day);
        datePickerDialog.show();
    }

    private void saveTask() {
        String description = taskDescription.getText().toString().trim();
        String date = pickDateButton.getText().toString().trim();
        Category category = (Category) categorySpinner.getSelectedItem();
        Type type = (Type) typeSpinner.getSelectedItem();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (description.isEmpty() || date.equals("Select Date") || category == null || type == null) {
            Toast.makeText(getContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
        } else {

            // Create a Task object
            Task task = new Task(description, date, category, type, false);
            DataManager.getInstance().addNewTaskByUser(user, new RequestDb() {
                @Override
                public void onSuccess() {
                    Toast.makeText(getContext(), "Task saved", Toast.LENGTH_SHORT).show();
                    dismiss();
                }

                @Override
                public void onFailure(Exception e) {
                    // Handle failure case
                    Toast.makeText(getActivity(), "Error logging out: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }, task);
        }
    }
}
