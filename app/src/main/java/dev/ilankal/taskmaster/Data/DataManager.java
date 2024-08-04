package dev.ilankal.taskmaster.Data;


import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import dev.ilankal.taskmaster.Interfaces.RequestDb;
import dev.ilankal.taskmaster.ui.Models.Task;
import dev.ilankal.taskmaster.ui.Models.TasksHashMap;

public class DataManager {
    private static volatile DataManager instance = null;
    private Context context;

    private DataManager(Context context) {
        this.context = context;
    }

    public static DataManager init(Context context){
        if (instance == null) {
            synchronized (DataManager.class){
                if (instance == null){
                    instance = new DataManager(context);
                }
            }
        }
        return getInstance();
    }

    public static DataManager getInstance() {
        return instance;
    }

    public void addUserToDB(FirebaseUser user, RequestDb callback){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");
        usersRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Log.d("DB", "User ID already exists");
                    callback.onSuccess();
                } else {

                    TasksHashMap newBusinessActivityHashMap = new TasksHashMap(user.getUid());
                    usersRef.child(user.getUid()).setValue(newBusinessActivityHashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("DB", "onSuccess");
                            callback.onSuccess();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("DB", "onFailure", e);
                            callback.onFailure(e);
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("DB", "Database error: " + error.getMessage());
                callback.onFailure(error.toException());
            }
        });
    }

    public void addNewTaskByUser(FirebaseUser user, RequestDb callback, Task task){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");

        // Create a new unique key for the new task
        DatabaseReference newTaskRef = usersRef
                .child(user.getUid())
                .child("allTasks")
                .push();

        // Get the generated unique key
        String taskId = newTaskRef.getKey();

        // Set the ID in the BusinessActivity object
        task.setId(taskId);

        // Set the value of the task
        newTaskRef.setValue(task)
                .addOnSuccessListener(aVoid -> {
                    // Handle success, e.g., notify the callback
                    if (callback != null) {
                        callback.onSuccess();
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure, e.g., notify the callback with an error
                    if (callback != null) {
                        callback.onFailure(e);
                    }
                });
    }
}
