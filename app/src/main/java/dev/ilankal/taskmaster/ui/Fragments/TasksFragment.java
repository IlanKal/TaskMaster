package dev.ilankal.taskmaster.ui.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dev.ilankal.taskmaster.Adapter.TaskAdapter;
import dev.ilankal.taskmaster.R;
import dev.ilankal.taskmaster.databinding.FragmentTasksBinding;
import dev.ilankal.taskmaster.ui.Models.Task;

public class TasksFragment extends Fragment {
    private RecyclerView recyclerView;
    private FloatingActionButton mFab;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private FragmentTasksBinding binding;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentTasksBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        findViews(root);
        initViews();

        return root;
    }

    private void findViews(View root) {
        recyclerView = root.findViewById(R.id.RecyclerView);
        mFab = root.findViewById(R.id.floatingActionButton);
    }

    private void initViews() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mFab.setOnClickListener(v -> AddNewTask.newInstance().show(getChildFragmentManager(), AddNewTask.TAG));

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(this, taskList, currentUser);
        recyclerView.setAdapter(taskAdapter);

        loadTasks();
    }

    private void loadTasks() {
        if (currentUser == null) return;

        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid()).child("allTasks");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                taskList.clear();
                for (DataSnapshot taskSnapshot : snapshot.getChildren()) {
                    Task task = taskSnapshot.getValue(Task.class);
                    if (task != null) {
                        taskList.add(task);
                    }
                }
                taskAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
