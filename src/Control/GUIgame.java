package Control;

import Config.Config;
import Model.Board;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import  javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GUIgame extends Application implements Initializable {

    @FXML
    private GUIsettings guiSettings;
    @FXML
    private ImageView settingsButton;
    @FXML
    private ImageView resetButton;
    @FXML
    private Label mineLabel;
    @FXML
    private Label clockLabel;
    @FXML
    private Label gameLabel;
    @FXML
    private GridPane boardView;
    @FXML
    private StackPane mainView;
    @FXML
    private AnchorPane overlayView;

    private static final int cellDimension = 30;
    private static final int inset = 10;
    private static int[] window = {10, 10};

    boolean gamecont;
    boolean won;
    boolean firstmove;

    Timeline timer;
    int clock;

    Board model;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("../View/Minesweeper.fxml"));

        primaryStage.setTitle("Minesweeper");

        //System.out.println("height: " + window[0] + ", width: " + window[1]);
        primaryStage.setScene(new Scene(root, window[1], window[0]));
        //primaryStage.setResizable(false);

        primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                try {
                    Config.writeConfig();
                } catch (IOException e) {
                    System.out.println("unable to save settings.");
                }
            }
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        overlayView.setVisible(false);

        settingsButton.setOnMouseClicked((MouseEvent e) -> {
            timer.stop();
            overlayView.setVisible(true);
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/Settings.fxml"));
                mainView.getChildren().add(loader.load());
                guiSettings = loader.getController();
                guiSettings.init(this);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        resetButton.setOnMousePressed((MouseEvent e) -> {
            timer.stop();
            boardView.getChildren().clear();
            clockLabel.setText("000");
            initNew(Config.getHeight(), Config.getWidth(), Config.getMines());
            Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
            stage.show();
        });

        Config.readConfig();
        initNew(Config.getHeight(), Config.getWidth(), Config.getMines());
    }

    private void initNew(int height, int width, int mines) {
        gameLabel.setVisible(false);

        //calculate window dimensions for this game
        window[0] = height * cellDimension + 2 * inset;
        //FIXME: window resizing on conditional statements is a bug!
        /*if(Config.getHeight() < 16)
            window[0] += 20 - 4 * (height - 10);*/
        window[1] = width * cellDimension + 2 * inset;

        //create view
        firstmove = true;
        model = new Board(height, width, mines);
        model.printSolution();
        gamecont = false;
        won = false;
        boardView.setPadding(new Insets(inset));

        for(int r=0; r < height; r++) {
            for(int c=0; c < width; c++) {

                Button cell = new Button();
                cell.setMinWidth(cellDimension);
                cell.setMaxWidth(cellDimension);
                cell.setMinWidth(cellDimension);
                cell.setMaxWidth(cellDimension);
                cell.textProperty().set("-fx-font-weight: bold;");
                cell.textProperty().bind(model.valueProperty(r, c));

                cell.setOnMousePressed(mouse -> {

                    if(firstmove) {
                        timer.play();
                        firstmove = false;
                    }

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
                        won = true;
                        gamecont = false;
                        endGame();
                    }
                });

                boardView.add(cell, c, r);
            }
        }
        /*for (int r = 0; r < width; r++) {
            ColumnConstraints column = new ColumnConstraints(cellDimension);
            boardView.getColumnConstraints().add(column);
        }
        for (int c = 0; c < width; c++) {
            RowConstraints row = new RowConstraints(cellDimension);
            boardView.getRowConstraints().add(row);
        }*/
        boardView.setStyle("-fx-background-color: darkgrey;");
        boardView.setStyle("-fx-grid-lines-visible: true;");

        //bind minecount
        mineLabel.textProperty().bind(model.minesLeftProperty().asString());
        //inilitalize & bind clock
        clock = 0;
        timer = new Timeline(new KeyFrame(Duration.millis(1000), ae -> incrementClock()));
        timer.setCycleCount(Animation.INDEFINITE);
    }

    private void endGame() {
        timer.stop();
        for(Node node: boardView.getChildren()) {
            node.setDisable(true);
        }

        Bounds boardViewBounds = boardView.localToScene(boardView.getBoundsInLocal());
        Bounds gameLabelBounds = gameLabel.localToScene(gameLabel.getBoundsInLocal());
        Line line = new Line(gameLabelBounds.getMinX(), gameLabelBounds.getMinY(),
                gameLabelBounds.getMinX(), boardViewBounds.getMinY() + boardViewBounds.getHeight()/2);
        PathTransition transition = new PathTransition();
        transition.setNode(gameLabel);
        transition.setDuration(Duration.seconds(2));
        transition.setPath(line);

        if(!won)
            gameLabel.setText("You Lost");
        else
            gameLabel.setText("You Won");

        transition.play();
        gameLabel.setVisible(true);
    }

    private void incrementClock() {
        if(clock > 997) //FIXME: threading issue, should stop at clock > 999
            timer.stop();
        clock++;
        clockLabel.setText(String.format("%03d", clock));
    }

    protected void resumeGame() {
        overlayView.setVisible(false);
        if(gamecont)
            timer.play();
    }
}
