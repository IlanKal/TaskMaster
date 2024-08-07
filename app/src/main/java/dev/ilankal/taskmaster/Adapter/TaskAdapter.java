package dev.ilankal.taskmaster.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import dev.ilankal.taskmaster.Data.DataManager;
import dev.ilankal.taskmaster.Interfaces.RequestDb;
import dev.ilankal.taskmaster.R;
import dev.ilankal.taskmaster.ui.Fragments.TasksFragment;
import dev.ilankal.taskmaster.ui.Models.Task;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyViewHolder> {

    private List<Task> taskList;
    private TasksFragment tasksFragment;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    public TaskAdapter(TasksFragment tasksFragment, List<Task> taskList, FirebaseUser currentUser){
        this.taskList = taskList;
        this.tasksFragment = tasksFragment;
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_box, parent, false);
        databaseReference = FirebaseDatabase.getInstance().getReference("tasks");
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Task task = taskList.get(position);

        Log.d("Adapter", "Binding data at position " + position + ": " + task.toString());

        holder.mcheckbox.setText(task.getDescription());
        holder.date_tv.setText(task.getDate());
        holder.category_tv.setText("Category: " + task.getCategoryString());
        holder.type_tv.setText("Type: " + task.getTypeString());
        holder.mcheckbox.setChecked(task.isCompleted());

        holder.mcheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setCompleted(isChecked);

            DataManager.getInstance().updateTaskCompletionStatus(currentUser, task.getId(), isChecked, new RequestDb() {
                @Override
                public void onSuccess() {
                    Log.d("Adapter", "Task completion status updated successfully.");
                }

                @Override
                public void onFailure(Exception e) {
                    Log.d("Adapter", "Failed to update task completion status.", e);
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private MaterialCheckBox mcheckbox;
        private TextView date_tv;
        private TextView category_tv;
        private TextView type_tv;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            mcheckbox = itemView.findViewById(R.id.mcheckbox);
            date_tv = itemView.findViewById(R.id.date_tv);
            category_tv = itemView.findViewById(R.id.category_tv);
            type_tv = itemView.findViewById(R.id.type_tv);
        }
    }
}
