package application;

import java.time.LocalDate;

public class Admin extends User {
    public Admin(String username, String role) {
        super(username, role);
    }

    public void createTask(String title, String assignedTo, LocalDate deadline) {
        Task t = new Task(0, title, assignedTo, deadline); // ID auto-generated in DB
        TaskManager.getInstance().addTask(t);
    }

    public void deleteTask(Task task) {
        TaskManager.getInstance().deleteTask(task.getTaskId());
    }
}