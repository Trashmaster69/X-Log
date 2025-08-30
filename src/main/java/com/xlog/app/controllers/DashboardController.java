package com.xlog.app.controllers;

import com.xlog.app.data.Database;
import com.xlog.app.data.TaskRepository;
import com.xlog.app.models.StatType;
import com.xlog.app.models.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class DashboardController {
    @FXML private VBox pendingList;
    @FXML private VBox completedList;
    @FXML private ProgressBar dayProgress;
    @FXML private Label progressPct;
    @FXML private Label topHabitLabel;
    @FXML private Text greetingText;

    @FXML private AnchorPane addModal;
    @FXML private TextField txtHabit;
    @FXML private ComboBox<StatType> cmbCategory;

    private final TaskRepository repo = new TaskRepository();

    @FXML
    public void initialize() {
        try { Database.init(); } catch (SQLException e) { e.printStackTrace(); }
        greetingText.setText(greetingForNow());
        cmbCategory.getItems().setAll(StatType.values());
        cmbCategory.getSelectionModel().select(StatType.STRENGTH);
        refresh();
    }

    private String greetingForNow() {
        int hour = java.time.LocalTime.now().getHour();
        String part = hour < 12 ? "Morning" : hour < 18 ? "Afternoon" : "Evening";
        return "Good " + part + "\n" +
               LocalDate.now().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH) +
               ", " + LocalDate.now();
    }

    /* ---------- UI refresh ---------- */
    private void refresh() {
        pendingList.getChildren().clear();
        completedList.getChildren().clear();

        List<Task> pending   = repo.getPending();
        List<Task> completed = repo.getCompletedSession();

        pending.forEach(t -> pendingList.getChildren().add(pendingRow(t)));
        completed.forEach(t -> completedList.getChildren().add(completedRow(t)));

        if (pending.isEmpty()) {
            Label l = new Label("No habits yet. Click + New Habit to add one.");
            l.getStyleClass().add("subdued");
            l.setPadding(new Insets(10,0,10,0));
            pendingList.getChildren().add(l);
        }
        if (completed.isEmpty()) {
            Label l = new Label("Nothing completed yet. Check a habit to complete it.");
            l.getStyleClass().add("subdued");
            l.setPadding(new Insets(10,0,10,0));
            completedList.getChildren().add(l);
        }

        int total = pending.size() + completed.size();
        double prog = total == 0 ? 0 : (completed.size() / (double) total);
        dayProgress.setProgress(prog);
        progressPct.setText(String.format("%.0f%% complete", prog*100));
        topHabitLabel.setText(!pending.isEmpty() ? pending.get(0).getName()
                              : (!completed.isEmpty() ? completed.get(0).getName() : "—"));
    }

    /* ---------- Row builders (old layout) ---------- */
    private HBox pendingRow(Task t) {
        HBox row = new HBox(12);
        row.getStyleClass().add("habit-row");
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 12, 10, 12));

        CheckBox cb = new CheckBox(); cb.getStyleClass().add("circle-check");
        cb.setOnAction(e -> { repo.complete(t.getId()); refresh(); });

        Label title = new Label(t.getName()); title.getStyleClass().add("habit-title");

        HBox chip = new HBox(6);
        chip.setAlignment(Pos.CENTER);
        chip.getStyleClass().addAll("chip", t.getStat().name().toLowerCase() + "-chip");
        chip.getChildren().add(new Label(switch (t.getStat()) {
            case INTELLIGENCE -> "Intelligence";
            case STRENGTH    -> "Strength";
            case CHI         -> "Chi";
            case CHARISMA    -> "Charisma";
        }));

        VBox left = new VBox(4);
        left.getChildren().addAll(title, chip);

        VBox rewards = new VBox(4);
        rewards.getChildren().addAll(new Label("+30 coins"), new Label("+15 exp"));
        rewards.getChildren().forEach(n -> n.getStyleClass().add("reward"));

        Button done = new Button("Mark Done");
        done.getStyleClass().add("ghost");
        done.setOnAction(e -> { repo.complete(t.getId()); refresh(); });

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        row.getChildren().addAll(cb, left, spacer, rewards, done);
        return row;
    }

    private HBox completedRow(Task t) {
        HBox row = new HBox(10);
        row.getStyleClass().addAll("habit-row", "completed");
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 12, 8, 12));

        Label bullet = new Label(); bullet.getStyleClass().add("bullet");
        bullet.setMinSize(10,10); bullet.setMaxSize(10,10);

        Label title = new Label(t.getName());
        title.getStyleClass().addAll("habit-title", "strike");

        HBox chip = new HBox(6);
        chip.setAlignment(Pos.CENTER);
        chip.getStyleClass().addAll("chip", t.getStat().name().toLowerCase() + "-chip");
        chip.getChildren().add(new Label(switch (t.getStat()) {
            case INTELLIGENCE -> "Intelligence";
            case STRENGTH    -> "Strength";
            case CHI         -> "Chi";
            case CHARISMA    -> "Charisma";
        }));

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        row.getChildren().addAll(bullet, title, chip, spacer);
        return row;
    }

    /* ---------- Add habit modal ---------- */
    @FXML private void openAdd(){ addModal.setVisible(true); txtHabit.requestFocus(); }
    @FXML private void closeAdd(){ addModal.setVisible(false); }
    @FXML private void confirmAdd() {
        String name = txtHabit.getText() == null ? "" : txtHabit.getText().trim();
        StatType cat = cmbCategory.getValue() == null ? StatType.STRENGTH : cmbCategory.getValue();
        if (!name.isEmpty()) {
            repo.add(name, cat);
            txtHabit.clear();
            closeAdd();
            refresh();
        }
    }

    /* ---------- Nav ---------- */
    @FXML private void gotoUserDetails(javafx.event.ActionEvent evt){
        try {
            FXMLLoader l = new FXMLLoader(getClass().getResource("/com/xlog/app/user_details.fxml"));
            Scene s = new Scene(l.load(), 1280, 800);
            s.getStylesheets().add(getClass().getResource("/com/xlog/app/style.css").toExternalForm());
            ((Stage)((Node)evt.getSource()).getScene().getWindow()).setScene(s);
        } catch (IOException e) { e.printStackTrace(); }
    }
    @FXML private void gotoStore(javafx.event.ActionEvent evt){
        try {
            FXMLLoader l = new FXMLLoader(getClass().getResource("/com/xlog/app/shop.fxml"));
            Scene s = new Scene(l.load(), 1280, 800);
            s.getStylesheets().add(getClass().getResource("/com/xlog/app/style.css").toExternalForm());
            ((Stage)((Node)evt.getSource()).getScene().getWindow()).setScene(s);
        } catch (IOException e) { e.printStackTrace(); }
    }
}
