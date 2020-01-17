package Control;

import Config.Config;
import Model.Board;
import javafx.animation.*;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
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
    private StackPane gameView;
    @FXML
    private StackPane mainView;
    @FXML
    private AnchorPane overlayView;

    private GridPane boardView;
    private Label gameLabel;

    private static final int cellDimension = 30;
    private static final int inset = 20;
    private static final int minWH = 370;
    private static final int minWW = 320;
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
            gameView.getChildren().clear();
            clockLabel.setText("000");
            initNew(Config.getHeight(), Config.getWidth(), Config.getMines());
            Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
            stage.show();
        });

        Config.readConfig();
        initNew(Config.getHeight(), Config.getWidth(), Config.getMines());
    }

    private void initNew(int height, int width, int mines) {
        //create game message
        gameLabel = new Label("Message");
        gameLabel.setVisible(false);
        StackPane.setAlignment(gameLabel, Pos.TOP_CENTER);

        //calculate window dimensions for this game
        int calcDim = height * cellDimension + 2 * inset + 50;
        window[0] = calcDim >= minWH ? calcDim : minWH;
        calcDim = width * cellDimension + 2 * inset;
        window[1] = calcDim >= minWH ? calcDim : minWW;

        //create model
        model = new Board(height, width, mines);
        model.printSolution();

        //initialize game control variables
        gamecont = false;
        firstmove = true;
        won = false;

        //create view
        boardView = new GridPane();
        boardView.setAlignment(Pos.CENTER);
        //FIXME: boardView.prefWidthProperty().bind(testStack.widthProperty());
        //FIXME: boardView.prefHeightProperty().bind(testStack.heightProperty());

        for(int c=0; c < width; c++) {
            ColumnConstraints grow = new ColumnConstraints();
            grow.setHgrow(Priority.ALWAYS);
        }
        for(int r=0; r < height; r++) {
            RowConstraints grow = new RowConstraints();
            grow.setVgrow(Priority.ALWAYS);
        }


        for(int r=0; r < height; r++) {
            for(int c=0; c < width; c++) {

                Button cell = new Button();
                cell.setMinHeight(cellDimension);
                cell.setMinWidth(cellDimension);
                cell.setMaxHeight(Double.POSITIVE_INFINITY);
                cell.setMaxWidth(Double.POSITIVE_INFINITY);
                GridPane.setFillWidth(cell, true);
                GridPane.setFillHeight(cell, true);
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
        //TESTCODE, change upon adding theme settings
        boardView.setStyle("-fx-background-color: darkgrey;");
        boardView.setStyle("-fx-grid-lines-visible: true;");
        gameLabel.setStyle("-fx-font-size: 26; -fx-background-color: pink;");

        gameView.getChildren().add(boardView);
        gameView.getChildren().add(gameLabel);


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

        //Show End Game Message and animation
        TranslateTransition transition = new TranslateTransition(Duration.seconds(2), gameLabel);
        transition.setToY(gameView.getHeight()/2 - 10);

        if(!won)
            gameLabel.setText("You Lost");
        else
            gameLabel.setText("You Won");

        gameLabel.setVisible(true);
        transition.play();
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
