package dev.ilankal.taskmaster.Interfaces;

public interface TaskCountsCallback {
    void onTaskCountsUpdated(int completedTasks, int pendingTasks, int importantTasks, int urgentTasks, int optionalTasks);
    void onError(Exception e);
}
