package dev.ilankal.taskmaster.ui.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import dev.ilankal.taskmaster.R;
import dev.ilankal.taskmaster.databinding.FragmentTasksBinding;

public class TasksFragment extends Fragment {
    private RecyclerView recyclerView;
    private FloatingActionButton mFab;

    private FragmentTasksBinding binding;

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

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewTask.newInstance().show(getChildFragmentManager(), AddNewTask.TAG);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
