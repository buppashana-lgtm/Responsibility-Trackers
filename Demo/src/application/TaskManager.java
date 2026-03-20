package application;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    private static TaskManager instance;

    private TaskManager() {}

    public static TaskManager getInstance() {
        if(instance == null) instance = new TaskManager();
        return instance;
    }

    // Add task to DB
    public void addTask(Task task) {
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            String sql = "INSERT INTO tasks (task_title, assigned_to, status, deadline) VALUES (?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, task.getTaskTitle());
            ps.setString(2, task.getAssignedTo());
            ps.setString(3, task.getStatus());
            ps.setDate(4, Date.valueOf(task.getDeadline()));
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next()) task.setTaskId(rs.getInt(1));
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void updateTaskStatus(int taskId, String username, String status) {
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            String sql = "UPDATE tasks SET status=? WHERE task_id=? AND assigned_to=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            ps.setInt(2, taskId);
            ps.setString(3, username);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void deleteTask(int taskId) {
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            String sql = "DELETE FROM tasks WHERE task_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, taskId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM tasks");
            while(rs.next()) {
                Task t = new Task(
                        rs.getInt("task_id"),
                        rs.getString("task_title"),
                        rs.getString("assigned_to"),
                        rs.getDate("deadline").toLocalDate()
                );
                t.setStatus(rs.getString("status"));
                tasks.add(t);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return tasks;
    }
}