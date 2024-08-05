package dev.ilankal.taskmaster.ui.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import dev.ilankal.taskmaster.Data.DataManager;
import dev.ilankal.taskmaster.Interfaces.TaskCountsCallback;
import dev.ilankal.taskmaster.LoginActivity;
import dev.ilankal.taskmaster.R;
import dev.ilankal.taskmaster.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ImageView logoImage;
    private MaterialButton logoutButton;
    private TextView manageTasksText;
    private ImageView tasksImage;
    private TextView completedTasksText;
    private TextView pendingTasksText;
    private TextView openTasksInTypesText;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        findViews(root);
        initViews();

        return root;
    }

    private void findViews(View root) {
        logoImage = root.findViewById(R.id.logo_image);
        logoutButton = root.findViewById(R.id.logout_button);
        manageTasksText = root.findViewById(R.id.manage_tasks_text);
        tasksImage = root.findViewById(R.id.tasks_image);
        completedTasksText = root.findViewById(R.id.completed_tasks_text);
        pendingTasksText = root.findViewById(R.id.pending_tasks_text);
        openTasksInTypesText = root.findViewById(R.id.open_tasks_in_types_text);
    }

    private void initViews() {
        if (getContext() != null) {
            logoutButton.setOnClickListener(view -> {
                AuthUI.getInstance()
                        .signOut(getContext())
                        .addOnCompleteListener(task -> {
                            // Redirect to login screen after successful sign-out
                            Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                            startActivity(loginIntent);
                            getActivity().finish();
                        });
            });
        }

        // Initialize or update UI components here
        updateTaskCounts();
    }

    private void updateTaskCounts() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DataManager.getInstance().getTaskCountsByUser(user, new TaskCountsCallback() {
                @Override
                public void onTaskCountsUpdated(int completedTasks, int pendingTasks, int importantTasks, int urgentTasks, int optionalTasks) {
                    completedTasksText.setText(completedTasks + "\ncompleted Tasks");
                    pendingTasksText.setText(pendingTasks + "\nPending Tasks");
                    openTasksInTypesText.setText("Open Tasks in Types\nIMPORTANT: " + importantTasks + "\nURGENT: " + urgentTasks + "\nOPTIONAL: " + optionalTasks);
                }

                @Override
                public void onError(Exception e) {
                    // Handle error
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
