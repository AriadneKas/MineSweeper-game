import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;

//Main class of the app
public class MineSweeperGame extends Application{
    BorderPane root = new BorderPane();
    private Board gameBoard;
    private int timeLimit;
    private int level;
    private int numMines;
    private boolean hasHyperMine;
    Timeline timeline;
    int t;

    private static final javafx.scene.image.Image FLAG_IMAGE = new javafx.scene.image.Image("pngFiles/flag.png");
    private static final javafx.scene.image.Image MINE_IMAGE = new javafx.scene.image.Image("pngFiles/mine.png");
    private static final javafx.scene.image.Image TIME_IMAGE = new javafx.scene.image.Image("pngFiles/time.png");

    private Label minesLabel = new Label("@");
    private Label flagsLabel = new Label("@");
    private Label timeLabel = new Label("@");

    public void loadGameFromFile(String fileName) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));

            checkValidDescription(fileName);
            level = Integer.parseInt(reader.readLine());
            numMines = Integer.parseInt(reader.readLine());
            timeLimit = Integer.parseInt(reader.readLine());
            hasHyperMine = Integer.parseInt(reader.readLine()) == 1 ;

            checkValidValues();

        } catch (IOException e) {
            //System.out.println("Error loading game configuration from file");
            e.printStackTrace();
        } catch (InvalidDescriptionException | InvalidValueException e) {
            System.out.println(e);
        }

    }

    private void checkValidDescription(String fileName) throws InvalidDescriptionException {
        int noOfLines = 0;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            while (reader.readLine() != null && noOfLines < 5) {
                noOfLines++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (noOfLines != 4)
            throw new InvalidDescriptionException();
    }

    private void checkValidValues() throws InvalidValueException {
        switch (level) {
            case 1:
                //mines
                if(numMines>11 || numMines<9)
                    throw new InvalidValueException("Invalid Number of Mines for level 1. Check second row");
                //time
                if(timeLimit>180 || timeLimit<120)
                    throw new InvalidValueException("Invalid Time Limit for level 1. Check third row");
                //hyper mine
                if(hasHyperMine)
                    throw new InvalidValueException("Value 1(only for level 2) or 0. Check fourth row");
                break;
            case 2:
                if(numMines>45 || numMines<35)
                    throw new InvalidValueException("Invalid Number of Mines for level 2. Check second row");
                if(timeLimit>360 || timeLimit<240)
                    throw new InvalidValueException("Invalid Time Limit for level 2. Check third row");
                break;
            default:
                throw new InvalidValueException("Invalid level. Check first row");
        }
    }


    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception{
        primaryStage.setWidth(350);
        primaryStage.setHeight(100);

        //Create Menu Bar
        MenuBar menuBar = new MenuBar();
        menuBar.getStyleClass().add("menu-bar");

        //create menu
        Menu appMenu = new Menu("Application");
        appMenu.getStyleClass().add("menu-item");

        //Menu items
        MenuItem start = new MenuItem("Start");
        start.setOnAction(e-> startGameHandler(primaryStage));
        MenuItem load = new MenuItem("Load");
        load.setOnAction(e-> loadGameHandler());
        MenuItem create = new MenuItem("Create");
        create.setOnAction(e-> PopUpBoxes.CreateScenarioBox());
        MenuItem exitGame = new MenuItem("Exit");
        exitGame.setOnAction(e-> primaryStage.close());

        //create menu
        Menu details = new Menu("Details");
        details.getStyleClass().add("menu-item");

        //Menu items
        MenuItem rounds = new MenuItem("Rounds");
        rounds.setOnAction(e-> PopUpBoxes.roundsHandler());
        MenuItem solution = new MenuItem("Solution");
        solution.setOnAction(e-> solutionHandler());


        appMenu.getItems().addAll(load,start,create,exitGame);
        details.getItems().addAll(rounds,solution);
        menuBar.getMenus().addAll(appMenu,details);



        ImageView mineView = new ImageView(MINE_IMAGE);
        mineView.setFitHeight(20);
        mineView.setFitWidth(20);
        ImageView flagView = new ImageView(FLAG_IMAGE);
        flagView.setFitHeight(20);
        flagView.setFitWidth(20);
        ImageView timeView = new ImageView(TIME_IMAGE);
        timeView.setFitHeight(20);
        timeView.setFitWidth(20);


        HBox infoBox = new HBox(mineView, minesLabel, flagView, flagsLabel,timeView,timeLabel);
        infoBox.setPadding(new Insets(5));
        infoBox.setSpacing(40);
        infoBox.getStyleClass().add("h-box");

        VBox top = new VBox(menuBar,infoBox);
        top.setMaxSize(800,50);

        root.setTop(top);

        Scene scene = new Scene(root);
        scene.getStylesheets().add("stylesheets.css");
        primaryStage.setScene(scene);
        primaryStage.setTitle("Minesweeper");
        primaryStage.show();
    }

    //Handler for start menu item set on action
    public void startGameHandler(Stage stage){
        //new game with same scenario
        gameBoard = new Board();
        root.setCenter(this.gameBoard);
        minesLabel.setText(Integer.toString(numMines));
        timeLabel.setText(Integer.toString(timeLimit));
        flagsLabel.setText("0");

        switch (level) {
            case 1:
                gameBoard.initializeNewGame(9, 9, numMines, false, timeLimit);
                stage.setHeight(550);
                stage.setWidth(450);
                break;
            case 2:
                gameBoard.initializeNewGame(16, 16, numMines, hasHyperMine, timeLimit);
                stage.setHeight(850);
                stage.setWidth(750);
                break;
        }
        timer();
    }

    //Handler for load menu item set on action
    public void loadGameHandler(){
        //load from file SCENARIO-ID.txt
        int idScenario = PopUpBoxes.loadGameBox();
        loadGameFromFile("src/medialab/SCENARIO-" + idScenario +".txt");

        minesLabel.setText(Integer.toString(numMines));
        timeLabel.setText(Integer.toString(timeLimit));
        flagsLabel.setText("0");
    }

    //Handler for solution menu item set on action
    public void solutionHandler(){
        if(this.gameBoard != null){
            this.gameBoard.revealMines();
        }
    }


    public void updateFlagsLabel() {
        flagsLabel.setText(Integer.toString(gameBoard.getFlagsPressed()));
    }


    public void updateTimeLabel() {
        timeLabel.setText(Integer.toString(t));
    }

    //timer
    public void timer() {
        t = timeLimit;
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            t--;
            updateTimeLabel();
            updateFlagsLabel();
            if(timeLimit==0 || gameBoard.gameOver()){
                timeline.stop();}
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }


}
