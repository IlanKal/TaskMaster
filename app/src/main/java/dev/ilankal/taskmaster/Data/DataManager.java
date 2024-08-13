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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dev.ilankal.taskmaster.Interfaces.RequestDb;
import dev.ilankal.taskmaster.Interfaces.TaskCountsByCategoryCallback;
import dev.ilankal.taskmaster.Interfaces.TaskCountsByTypeCallback;
import dev.ilankal.taskmaster.Interfaces.TaskCountsByUserCallback;
import dev.ilankal.taskmaster.Interfaces.TasksByDateCallback;
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
                    TasksHashMap newTasksHashMap = new TasksHashMap(user.getUid());
                    usersRef.child(user.getUid()).setValue(newTasksHashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
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
    public void getTasksByDate(FirebaseUser user, String date, TasksByDateCallback callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference tasksRef = database.getReference("users").child(user.getUid()).child("allTasks");

        tasksRef.orderByChild("date").equalTo(date).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Task> tasks = new ArrayList<>();
                for (DataSnapshot taskSnapshot : snapshot.getChildren()) {
                    Task task = taskSnapshot.getValue(Task.class);
                    if (task != null) {
                        tasks.add(task);
                    }
                }
                callback.onTasksRetrieved(tasks);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.toException());
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

        // Set the ID in the Task object
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

    public void getCompletedAndPendingTasks(FirebaseUser user, TaskCountsByUserCallback callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users").child(user.getUid()).child("allTasks");

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int completedTasks = 0;
                int pendingTasks = 0;

                for (DataSnapshot taskSnapshot : snapshot.getChildren()) {
                    Task task = taskSnapshot.getValue(Task.class);
                    if (task != null) {
                        if (task.isCompleted()) {
                            completedTasks++;
                        } else {
                            pendingTasks++;
                        }
                    }
                }

                callback.onTaskCountsByUserUpdated(completedTasks, pendingTasks);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.toException());
            }
        });
    }

    public void getTaskCountsByType(FirebaseUser user, TaskCountsByTypeCallback callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users").child(user.getUid()).child("allTasks");

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int importantTasks = 0;
                int urgentTasks = 0;
                int optionalTasks = 0;

                for (DataSnapshot taskSnapshot : snapshot.getChildren()) {
                    Task task = taskSnapshot.getValue(Task.class);
                    if (task != null) {
                        switch (task.getType()) {
                            case IMPORTANT:
                                importantTasks++;
                                break;
                            case URGENT:
                                urgentTasks++;
                                break;
                            case OPTIONAL:
                                optionalTasks++;
                                break;
                        }
                    }
                }

                callback.onTaskCountsByTypeUpdated(importantTasks, urgentTasks, optionalTasks);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.toException());
            }
        });
    }

    public void getTaskCountsByCategory(FirebaseUser user, TaskCountsByCategoryCallback callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users").child(user.getUid()).child("allTasks");

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int workTasks = 0;
                int personalTasks = 0;
                int homeTasks = 0;
                int fitnessTasks = 0;
                int otherTasks = 0;

                for (DataSnapshot taskSnapshot : snapshot.getChildren()) {
                    Task task = taskSnapshot.getValue(Task.class);
                    if (task != null) {
                        switch (task.getCategory()) {
                            case WORK:
                                workTasks++;
                                break;
                            case PERSONAL:
                                personalTasks++;
                                break;
                            case HOME:
                                homeTasks++;
                                break;
                            case FITNESS:
                                fitnessTasks++;
                                break;
                            case OTHER:
                                otherTasks++;
                                break;
                        }
                    }
                }

                callback.onTaskCountsByCategoryUpdated(workTasks, personalTasks, homeTasks, fitnessTasks, otherTasks);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.toException());
            }
        });
    }

    public void loadTasksForUser(FirebaseUser user, TasksByDateCallback callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference tasksRef = database.getReference("users").child(user.getUid()).child("allTasks");

        tasksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Task> tasks = new ArrayList<>();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");

                for (DataSnapshot taskSnapshot : snapshot.getChildren()) {
                    Task task = taskSnapshot.getValue(Task.class);
                    if (task != null) {
                        tasks.add(task);
                    }
                }

                // Sort the taskList by date in ascending order
                Collections.sort(tasks, (task1, task2) -> {
                    LocalDate date1 = LocalDate.parse(task1.getDate(), formatter);
                    LocalDate date2 = LocalDate.parse(task2.getDate(), formatter);
                    return date1.compareTo(date2);
                });

                callback.onTasksRetrieved(tasks);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.toException());
            }
        });
    }


    public void updateTaskCompletionStatus(FirebaseUser user, String taskId, boolean isCompleted, RequestDb callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference taskRef = database.getReference("users")
                .child(user.getUid())
                .child("allTasks")
                .child(taskId);

        taskRef.child("completed").setValue(isCompleted)
                .addOnSuccessListener(aVoid -> {
                    if (callback != null) {
                        callback.onSuccess();
                    }
                })
                .addOnFailureListener(e -> {
                    if (callback != null) {
                        callback.onFailure(e);
                    }
                });
    }

    public void deleteTaskByUser(FirebaseUser user, String taskId, RequestDb callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference taskRef = database.getReference("users")
                .child(user.getUid())
                .child("allTasks")
                .child(taskId);

        taskRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    if (callback != null) {
                        callback.onSuccess();
                    }
                })
                .addOnFailureListener(e -> {
                    if (callback != null) {
                        callback.onFailure(e);
                    }
                });
    }

    public void updateTaskByUser(FirebaseUser user, Task updatedTask, RequestDb callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference taskRef = database.getReference("users")
                .child(user.getUid())
                .child("allTasks")
                .child(updatedTask.getId());

        taskRef.setValue(updatedTask)
                .addOnSuccessListener(aVoid -> {
                    if (callback != null) {
                        callback.onSuccess();
                    }
                })
                .addOnFailureListener(e -> {
                    if (callback != null) {
                        callback.onFailure(e);
                    }
                });
    }
}
