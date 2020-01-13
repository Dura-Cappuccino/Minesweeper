package Control;

import Config.Config;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class GUIsettings implements Initializable {

    private GUIgame guiGame;
    @FXML
    private BorderPane settingsView;
    @FXML
    private ImageView backButton;
    @FXML
    private ChoiceBox levelBox;
    @FXML
    private TextField widthField;
    @FXML
    private TextField heightField;
    @FXML
    private TextField minesField;
    @FXML
    private CheckBox soundSwitch;
    @FXML
    private CheckBox themeSwitch;
    @FXML
    private Button saveButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        soundSwitch.setSelected(Config.getSound());
        themeSwitch.setSelected(Config.getTheme());

        levelBox.setValue(Config.getLevel());
        levelBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number val, Number newVal) {
                if(!levelBox.getItems().get(newVal.intValue()).toString().equalsIgnoreCase("custom"))
                    disableCustom();
                else
                    enableCustom();
            }
        });

        if(Config.getLevel().equalsIgnoreCase("custom")) {
            heightField.setText(Integer.toString(Config.getHeight()));
            widthField.setText(Integer.toString(Config.getWidth()));
            minesField.setText(Integer.toString(Config.getMines()));
        }

        saveButton.setOnMousePressed((MouseEvent e) -> {
            String level = levelBox.getSelectionModel().getSelectedItem().toString();
            int height = 0;
            int width = 0;
            int mines = 0;

            if(level.equalsIgnoreCase("custom")) {
                height = Integer.parseInt(heightField.getText());
                width = Integer.parseInt(widthField.getText());
                mines = Integer.parseInt(minesField.getText());
            }
            if(validateSave(height, width, mines)) {
                Config.saveConfig(level, height, width, mines, soundSwitch.isSelected(), themeSwitch.isSelected());
                exitSettingsView();
            } else {
                System.out.println("Invalid settings values.");
            }
        });
        backButton.setOnMousePressed((MouseEvent e) -> {
            exitSettingsView();
        });
    }

    private boolean validateSave(int h, int w, int m) {
        if(levelBox.getValue().equals("Custom")) {
            if(h < 2 || h > 500 ||
               w < 2 || w > 500 ||
               m < 0 || m > w * h) {
                return false;
            }
        }
        return true;
    }

    private void exitSettingsView() {
        Stage stage = (Stage)settingsView.getScene().getWindow();
        ((Pane)settingsView.getParent()).getChildren().remove(settingsView);
        guiGame.resumeGame();
        stage.show();
    }

    private void disableCustom() {
        heightField.setText("");
        widthField.setText("");
        minesField.setText("");
        heightField.setDisable(true);
        widthField.setDisable(true);
        minesField.setDisable(true);
    }

    private void enableCustom() {
        heightField.setDisable(false);
        widthField.setDisable(false);
        minesField.setDisable(false);
    }

    public void init(GUIgame guiGame) {
        this.guiGame = guiGame;
    }
}
