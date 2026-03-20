package application;

public class Student extends User {
    public Student(String username, String role) {
        super(username, role);
    }

    public void updateTaskStatus(Task task) {
        if(task.getAssignedTo().equalsIgnoreCase(this.username)) {
            if(task.getStatus().equals("Pending")) task.setStatus("In Progress");
            else if(task.getStatus().equals("In Progress")) task.setStatus("Completed");
            TaskManager.getInstance().updateTaskStatus(task.getTaskId(), this.username, task.getStatus());
        }
    }
}