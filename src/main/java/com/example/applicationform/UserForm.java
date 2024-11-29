package com.example.applicationform;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserForm extends Application {

    private static final String FILE_PATH = "userRecords.txt";

    private TextField nameField;
    private TextField idNumberField;
    private TextField provinceField;
    private DatePicker dateOfBirthPicker;
    private ToggleGroup genderSelectionGroup;
    private Label feedbackLabel;

    private List<String[]> savedRecords = new ArrayList<>();
    private int currentIndex = -1;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("User Application Form");

        VBox formInputs = createInputFields();
        VBox controlButtons = createActionButtons();

        HBox mainContainer = new HBox(40, formInputs, controlButtons);
        mainContainer.setPadding(new Insets(40));
        mainContainer.setStyle("-fx-background-color: #F4F7FB; -fx-text-fill: #333333;");

        HBox.setHgrow(formInputs, Priority.ALWAYS);
        HBox.setHgrow(controlButtons, Priority.NEVER);

        Scene scene = new Scene(mainContainer, 900, 550);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createInputFields() {
        nameField = new TextField();
        idNumberField = new TextField();
        provinceField = new TextField();
        dateOfBirthPicker = new DatePicker();

        genderSelectionGroup = new ToggleGroup();
        RadioButton maleRadioButton = new RadioButton("Male");
        RadioButton femaleRadioButton = new RadioButton("Female");
        maleRadioButton.setToggleGroup(genderSelectionGroup);
        femaleRadioButton.setToggleGroup(genderSelectionGroup);

        HBox genderSelectionBox = new HBox(20, maleRadioButton, femaleRadioButton);
        genderSelectionBox.setAlignment(Pos.CENTER_LEFT);

        nameField.setPromptText("Enter Full Name");
        idNumberField.setPromptText("Enter ID Number");
        provinceField.setPromptText("Enter Home Province");

        nameField.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: #4F4F4F; -fx-font-size: 14px; -fx-border-color: #E4E4E4; -fx-border-radius: 5px;");
        idNumberField.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: #4F4F4F; -fx-font-size: 14px; -fx-border-color: #E4E4E4; -fx-border-radius: 5px;");
        provinceField.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: #4F4F4F; -fx-font-size: 14px; -fx-border-color: #E4E4E4; -fx-border-radius: 5px;");
        dateOfBirthPicker.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: #4F4F4F; -fx-font-size: 14px; -fx-border-color: #E4E4E4; -fx-border-radius: 5px;");

        VBox inputBox = new VBox(20,
                new Label("Full Name"), nameField,
                new Label("ID Number"), idNumberField,
                new Label("Gender"), genderSelectionBox,
                new Label("Home Province"), provinceField,
                new Label("Date of Birth"), dateOfBirthPicker
        );
        inputBox.setPadding(new Insets(25));
        inputBox.setAlignment(Pos.TOP_LEFT);
        inputBox.setStyle("-fx-background-color: #FFFFFF; -fx-border-radius: 10px; -fx-drop-shadow: 2 2 5 #888888;");

        return inputBox;
    }

    private VBox createActionButtons() {
        Button newRecordButton = new Button("New Record");
        Button deleteButton = new Button("Delete Record");
        Button restoreButton = new Button("Restore Record");
        Button nextButton = new Button("Next Record");
        Button previousButton = new Button("Previous Record");
        Button closeButton = new Button("Close");

        String buttonStyle = "-fx-background-color: #42A5F5; -fx-text-fill: white; -fx-font-size: 14px; -fx-border-radius: 8px; -fx-padding: 10 20;";
        newRecordButton.setStyle(buttonStyle);
        deleteButton.setStyle(buttonStyle);
        restoreButton.setStyle(buttonStyle);
        nextButton.setStyle(buttonStyle);
        previousButton.setStyle(buttonStyle);
        closeButton.setStyle(buttonStyle);

        deleteButton.setDisable(true);
        restoreButton.setDisable(true);
        nextButton.setDisable(true);
        previousButton.setDisable(true);

        addHoverEffects(newRecordButton);
        addHoverEffects(deleteButton);
        addHoverEffects(restoreButton);
        addHoverEffects(nextButton);
        addHoverEffects(previousButton);
        addHoverEffects(closeButton);

        newRecordButton.setOnAction(e -> saveUserData());
        closeButton.setOnAction(e -> closeApplication());
        nextButton.setOnAction(e -> displayNextRecord());

        VBox actionBox = new VBox(20, newRecordButton, deleteButton, restoreButton, nextButton, previousButton, closeButton);
        actionBox.setPadding(new Insets(20));
        actionBox.setAlignment(Pos.CENTER_RIGHT);

        return actionBox;
    }

    private void addHoverEffects(Button button) {
        button.setOnMouseEntered(e -> button.setEffect(new DropShadow(10, 2, 2, null)));
        button.setOnMouseExited(e -> button.setEffect(null));
    }

    private void saveUserData() {
        String fullName = nameField.getText();
        String idNumber = idNumberField.getText();
        String province = provinceField.getText();
        String dob = (dateOfBirthPicker.getValue() != null) ? dateOfBirthPicker.getValue().toString() : "";
        RadioButton selectedGender = (RadioButton) genderSelectionGroup.getSelectedToggle();
        String gender = (selectedGender != null) ? selectedGender.getText() : "";

        if (fullName.isEmpty() || idNumber.isEmpty() || province.isEmpty() || dob.isEmpty() || gender.isEmpty()) {
            showAlert("Error", "All fields are mandatory.");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(String.join(",", fullName, idNumber, gender, province, dob));
            writer.newLine();
            showAlert("Success", "Record saved successfully.");

            // Clear the form inputs after saving the data
            resetFormFields();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void displayNextRecord() {
        if (currentIndex + 1 < savedRecords.size()) {
            currentIndex++;
            String[] record = savedRecords.get(currentIndex);
            populateFormWithRecord(record);
        } else {
            showAlert("End of Records", "No more records.");
        }
    }

    private void populateFormWithRecord(String[] record) {
        nameField.setText(record[0]);
        idNumberField.setText(record[1]);
        provinceField.setText(record[3]);
        dateOfBirthPicker.setValue(java.time.LocalDate.parse(record[4]));
        selectGender(record[2]);
    }

    private void selectGender(String gender) {
        for (Toggle toggle : genderSelectionGroup.getToggles()) {
            RadioButton button = (RadioButton) toggle;
            if (button.getText().equalsIgnoreCase(gender)) {
                button.setSelected(true);
                break;
            }
        }
    }

    private void closeApplication() {
        System.exit(0);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void resetFormFields() {
        nameField.clear();
        idNumberField.clear();
        provinceField.clear();
        dateOfBirthPicker.setValue(null);
        genderSelectionGroup.selectToggle(null);
    }
}
