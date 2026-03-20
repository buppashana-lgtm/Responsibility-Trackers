package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class MainApp extends Application {

    private User loggedInUser;
    private VBox cardContainer;
    private ComboBox<String> statusFilter;
    private DatePicker deadlineFilter;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Responsibility Tracker");


        Label lblUser = new Label("Username:");
        TextField txtUser = new TextField();
        Label lblRole = new Label("Role (Teacher, GroupLeader, Student, GroupMember):");
        TextField txtRole = new TextField();
        Button btnLogin = new Button("Login");

        VBox loginBox = new VBox(10, lblUser, txtUser, lblRole, txtRole, btnLogin);
        loginBox.setPadding(new Insets(20));
        loginBox.setAlignment(Pos.CENTER);
        Scene loginScene = new Scene(loginBox, 400, 300);

        // ---------------- DASHBOARD ----------------
        BorderPane dashboard = new BorderPane();
        dashboard.setPadding(new Insets(10));

        VBox topContainer = new VBox(10);
        HBox topBar = new HBox(20);
        Label lblWelcome = new Label();
        Button btnBack = new Button("Back");
        topBar.getChildren().addAll(lblWelcome, btnBack);
        topBar.setAlignment(Pos.CENTER_LEFT);

        // Filters
        HBox filterBox = new HBox(10);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All", "Pending", "In Progress", "Completed");
        statusFilter.setValue("All");
        deadlineFilter = new DatePicker();
        Button btnApplyFilter = new Button("Apply Filter");
        Button btnClearFilter = new Button("Clear Filter");
        filterBox.getChildren().addAll(new Label("Filter by Status:"), statusFilter,
                new Label("Filter by Deadline:"), deadlineFilter, btnApplyFilter, btnClearFilter);

        topContainer.getChildren().addAll(topBar, filterBox);
        dashboard.setTop(topContainer);

        // Center cards
        ScrollPane scrollPane = new ScrollPane();
        cardContainer = new VBox(10);
        cardContainer.setPadding(new Insets(10));
        scrollPane.setContent(cardContainer);
        dashboard.setCenter(scrollPane);

        // Bottom Admin Controls
        VBox controlBox = new VBox(10);
        controlBox.setPadding(new Insets(10));
        TextField txtTaskTitle = new TextField(); txtTaskTitle.setPromptText("Task Title");
        TextField txtAssignTo = new TextField(); txtAssignTo.setPromptText("Assign to username");
        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Deadline");
        Button btnCreateTask = new Button("Create Task");
        Button btnDeleteTask = new Button("Delete Selected Task");
        controlBox.getChildren().addAll(txtTaskTitle, txtAssignTo, datePicker, btnCreateTask, btnDeleteTask);
        dashboard.setBottom(controlBox);

        Scene dashboardScene = new Scene(dashboard, 850, 600);

        // ---------------- ACTIONS ----------------
        btnLogin.setOnAction(e -> {
            String username = txtUser.getText().trim();
            String role = txtRole.getText().trim();
            if(username.isEmpty() || role.isEmpty()) { showAlert("Error","Enter username and role!"); return; }

            switch(role) {
                case "Teacher":
                case "GroupLeader": loggedInUser = new Admin(username, role); break;
                case "Student":
                case "GroupMember": loggedInUser = new Student(username, role); break;
                default: showAlert("Error","Invalid role!"); return;
            }

            lblWelcome.setText("Welcome " + loggedInUser.getUsername() + " (" + loggedInUser.getRole() + ")");
            refreshCards(null, null);

            boolean isAdmin = loggedInUser instanceof Admin;
            controlBox.setVisible(isAdmin);

            primaryStage.setScene(dashboardScene);
        });

        btnCreateTask.setOnAction(e -> {
            if(!(loggedInUser instanceof Admin)) return;
            String title = txtTaskTitle.getText().trim();
            String assignedTo = txtAssignTo.getText().trim();
            LocalDate deadline = datePicker.getValue();
            if(title.isEmpty() || assignedTo.isEmpty() || deadline==null) { showAlert
                    ("Error","All fields required!"); return; }

            ((Admin) loggedInUser).createTask(title, assignedTo, deadline);

            txtTaskTitle.clear(); txtAssignTo.clear(); datePicker.setValue(null);
            refreshCards(statusFilter.getValue(), deadlineFilter.getValue());
        });

        btnDeleteTask.setOnAction(e -> {
            if(!(loggedInUser instanceof Admin)) return;
            Task selected = cardContainer.getChildren().stream()
                    .filter(n -> n instanceof VBox && ((VBox)n).getUserData()!=null)
                    .map(n -> (Task)((VBox)n).getUserData())
                    .findFirst().orElse(null);
            if(selected != null) {
                ((Admin) loggedInUser).deleteTask(selected);
                refreshCards(statusFilter.getValue(), deadlineFilter.getValue());
            }
        });

        btnBack.setOnAction(e -> {
            txtUser.clear(); txtRole.clear();
            primaryStage.setScene(loginScene);
        });

        btnApplyFilter.setOnAction(e -> {
            refreshCards(statusFilter.getValue(), deadlineFilter.getValue());
        });

        btnClearFilter.setOnAction(e -> {
            statusFilter.setValue("All");
            deadlineFilter.setValue(null);
            refreshCards(null, null);
        });

        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    private void refreshCards(String statusFilterValue, LocalDate deadlineFilterValue) {
        cardContainer.getChildren().clear();
        List<Task> tasks = TaskManager.getInstance().getAllTasks();

        tasks = tasks.stream().filter(task -> {
            if(loggedInUser instanceof Student && !task.getAssignedTo().equalsIgnoreCase
                    (loggedInUser.getUsername())) return false;
            if(statusFilterValue != null && !statusFilterValue.equals("All") && !task.getStatus().equals
                    (statusFilterValue)) return false;
            if(deadlineFilterValue != null && !task.getDeadline().equals(deadlineFilterValue)) return false;
            return true;
        }).collect(Collectors.toList());

        for(Task task : tasks) {
            VBox card = new VBox(5);
            card.setPadding(new Insets(10));
            card.setStyle("-fx-border-color: black; -fx-border-radius: 5; -fx-background-radius: 5;" +
                    " -fx-background-color: #f4f4f4;");

            if(task.isOverdue()) {
                card.setStyle("-fx-border-color: black; -fx-border-radius: 5; -fx-background-radius: 5;" +
                        " -fx-background-color: #ffd6d6;");
            }

            Label lblTitle = new Label(task.getTaskTitle());
            Label lblAssigned = new Label("Assigned to: " + task.getAssignedTo());
            Label lblDeadline = new Label("Deadline: " + task.getDeadline());

            ProgressBar progress = new ProgressBar(task.getProgressValue());
            progress.setPrefWidth(200);
            progress.setStyle("-fx-accent: " + task.getProgressColor() + ";");

            Button btnUpdate = new Button("Next Stage");
            btnUpdate.setDisable(!(loggedInUser instanceof Student));
            btnUpdate.setOnAction(e -> {
                if(loggedInUser instanceof Student) {
                    ((Student) loggedInUser).updateTaskStatus(task);
                    refreshCards(statusFilterValue, deadlineFilterValue);
                }
            });

            card.getChildren().addAll(lblTitle, lblAssigned, lblDeadline, progress);
            if(loggedInUser instanceof Student) card.getChildren().add(btnUpdate);

            card.setUserData(task);
            cardContainer.getChildren().add(card);
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(msg); alert.showAndWait();
    }
}