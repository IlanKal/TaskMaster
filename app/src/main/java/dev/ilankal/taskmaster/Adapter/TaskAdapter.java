package dev.ilankal.taskmaster.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

import dev.ilankal.taskmaster.R;
import dev.ilankal.taskmaster.ui.Fragments.TasksFragment;
import dev.ilankal.taskmaster.ui.Models.Task;

public class TaskAdapter extends RecyclerView.Adapter <TaskAdapter.MyVIewHolder> {

    private List<Task> taskList;


    public TaskAdapter(TasksFragment tasksFragment, List<Task> taskList){
        this.taskList = taskList;

    }
    @NonNull
    @Override
    public MyVIewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_box, parent, false);
        return new MyVIewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyVIewHolder holder, int position) {
        Task task = getItem(position);

        Log.d("Adapter", "Binding data at position " + position + ": " + task.toString());

        // Bind the businessActivity data to the views
        holder.date_tv.setText(task.getDate());
        holder.category_tv.setText("Category: " + task.getCategoryString());
        holder.type_tv.setText("Type: " + task.getTypeString());
        if(task.isCompleted()){
            holder.mcheckbox.setChecked(true);
        }
        else {

        }

    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    private Task getItem(int position) {
        return taskList.get(position);
    }

    public class MyVIewHolder extends RecyclerView.ViewHolder{
        private MaterialCheckBox mcheckbox;
        private TextView date_tv;
        private TextView category_tv;
        private TextView type_tv;
        public MyVIewHolder(@NonNull View itemView){
            super(itemView);
            mcheckbox = itemView.findViewById(R.id.mcheckbox);
            date_tv = itemView.findViewById(R.id.date_tv);
            category_tv = itemView.findViewById(R.id.category_tv);
            type_tv = itemView.findViewById(R.id.type_tv);
        }
    }
}
