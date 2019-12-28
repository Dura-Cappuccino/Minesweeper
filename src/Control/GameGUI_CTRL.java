package Control;

import Config.Config;
import Model.Board;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class GameGUI_CTRL implements Initializable {

    @FXML
    private GridPane boardView;
    @FXML
    private Label mineLabel;
    @FXML
    private Label clockLabel;
    @FXML
    private ImageView resetLabel;

    Board model;
    boolean gamecont;
    boolean won;

    Timeline timer;
    int clock;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Config.readConfig();
        initNew(Config.getHeight(), Config.getWidth(), Config.getMines());
    }

    private void initNew(int height, int width, int mines) {

        //create view
        model = new Board(height, width, mines);
        model.printSolution();
        gamecont = false;
        boardView.setPadding(new Insets(10));

        for(int r=0; r < height; r++) {
            for(int c=0; c < width; c++) {

                Button cell = new Button();
                cell.setMinWidth(30);
                cell.setMaxWidth(30);
                cell.setMinWidth(30);
                cell.setMaxWidth(30);
                cell.textProperty().bind(model.valueProperty(r, c));

                cell.setOnMousePressed(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouse) {
                        int row = GridPane.getRowIndex(cell);
                        int col = GridPane.getColumnIndex(cell);

                        if(mouse.getButton().equals(MouseButton.MIDDLE)){
                            gamecont = model.openCascade(row, col);
                        }
                        else if(mouse.getButton().equals(MouseButton.PRIMARY)){
                            gamecont = model.openSingle(row, col);
                        }
                        else if(mouse.getButton().equals(MouseButton.SECONDARY)){
                            model.putFlag(row, col);
                        }
                        if(!gamecont)
                            endGame();
                        if(model.boardComplete()){
                            gamecont = false;
                            won = true;
                            endGame();
                        }
                    }
                });

                boardView.add(cell, c, r);
            }
        }

        //bind mines
        mineLabel.textProperty().bind(model.minesLeftProperty().asString());

        //inilitalize + bind clock
        clock = 0;
        timer = new Timeline(new KeyFrame(Duration.millis(1000), ae -> incrementClock()));
        timer.setCycleCount(Animation.INDEFINITE);

        timer.play();
    }

    private void endGame() {
        timer.stop();
        for(Node node: boardView.getChildren()) {
            node.setDisable(true);
        }
        if(!won)
            System.out.println("Oops, better luck next time...");
        else
            System.out.println("Congratulations! You won.");
    }

    private void incrementClock() {
        if(clock > 997) //FIXME: threading issue, should stop at clock > 999
            timer.stop();
        clock++;
        clockLabel.setText(String.format("%03d", clock));
    }
}
