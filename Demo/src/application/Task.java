package application;

import java.time.LocalDate;

public class Task {
    private int taskId;
    private String taskTitle;
    private String assignedTo;
    private String status; // Pending, In Progress, Completed
    private LocalDate deadline;

    public Task(int taskId, String taskTitle, String assignedTo, LocalDate deadline) {
        this.taskId = taskId;
        this.taskTitle = taskTitle;
        this.assignedTo = assignedTo;
        this.deadline = deadline;
        this.status = "Pending";
    }

    public int getTaskId() { return taskId; }
    public void setTaskId(int taskId) { this.taskId = taskId; }

    public String getTaskTitle() { return taskTitle; }
    public String getAssignedTo() { return assignedTo; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDate getDeadline() { return deadline; }

    public boolean isOverdue() {
        return status.equals("Pending") && deadline.isBefore(LocalDate.now());
    }

    public double getProgressValue() {
        switch(status) {
            case "Pending": return 0.33;
            case "In Progress": return 0.66;
            case "Completed": return 1.0;
        }
        return 0;
    }

    public String getProgressColor() {
        switch(status) {
            case "Pending": return "blue";    // Pending → blue
            case "In Progress": return "red"; // In Progress → red
            case "Completed": return "green"; // Completed → green
        }
        return "blue";
    }
}