import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;


public class PopUpBoxes {

    static int idScenario;

    //popup for when game is over
    public static void gameOverBox(String title, String message){
        Stage window = new Stage();

        //block interaction with other windows until this is over
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        //window.setMaxWidth(300);
        window.setMaxHeight(200);
        window.setMinWidth(300);

        Label label = new Label();
        label.setText(message);

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e->window.close());

        VBox layout = new VBox(20);
        layout.getChildren().addAll(label, closeButton);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }

    public static int loadGameBox(){
        Stage window = new Stage();

        //block interaction with other windows until this is over
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Load Game");
        window.setMaxHeight(200);
        window.setMinWidth(300);

        GridPane grid = new GridPane();
        grid.setMinSize(100, 100);
        grid.setPadding(new Insets(10,10,10,10));
        grid.setVgap(5);
        grid.setHgap(5);
        grid.setAlignment(Pos.CENTER);

        Label label = new Label("Select the ID:");
        grid.add(label, 0, 0);

        TextField id = new TextField();
        grid.add(id, 1, 0);

        Button loadButton = new Button("Load");
        loadButton.setOnAction(e-> isInt(id,id.getText()));
        grid.add(loadButton, 0, 1);

        Button exitButton = new Button("Exit");
        exitButton.setOnAction(e-> window.close());
        grid.add(exitButton, 1, 1);

        Scene scene = new Scene(grid);
        window.setScene(scene);
        window.showAndWait();

        return idScenario;
    }

    public static void CreateScenarioBox(){
        Stage window = new Stage();

        //block interaction with other windows until this is over
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Create Scenario");
        window.setMaxHeight(200);
        window.setMinWidth(200);

        GridPane grid = new GridPane();
        grid.setMinSize(100, 100);
        grid.setPadding(new Insets(10,10,10,10));
        grid.setVgap(5);
        grid.setHgap(5);
        grid.setAlignment(Pos.CENTER);

        Label label1 = new Label("Scenario:");
        grid.add(label1, 0, 0);
        TextField scenarioId = new TextField();
        grid.add(scenarioId, 1, 0);

        Label label2 = new Label("Level:");
        grid.add(label2, 0, 1);
        TextField level = new TextField();
        grid.add(level, 1, 1);

        Label label3 = new Label("Mines:");
        grid.add(label3, 0, 2);
        TextField mines = new TextField();
        grid.add(mines, 1, 2);

        Label label4 = new Label("Time Limit:");
        grid.add(label4, 0, 3);
        TextField time = new TextField();
        grid.add(time, 1, 3);

        Label label5 = new Label("HyperMine:");
        grid.add(label5, 0, 4);
        TextField HyperMine = new TextField();
        grid.add(HyperMine, 1, 4);


        TextField values[] = {level,mines,time,HyperMine};

        Button loadButton = new Button("Load");
        loadButton.setOnAction(e-> writeScenarioFile(values, Integer.parseInt(scenarioId.getText())));
        grid.add(loadButton, 0, 5);

        Button exitButton = new Button("Exit");
        exitButton.setOnAction(e-> window.close());
        grid.add(exitButton, 1, 5);

        Scene scene = new Scene(grid);
        window.setScene(scene);
        window.showAndWait();
    }

    public static void roundsHandler() {
        // Get the game history from the GameHistory class
        List<String> gameHistory = GameHistory.getGameHistory();

        // Create a popup window to display the game history
        Stage window = new Stage();
        window.setTitle("Game History");
        Label  title = new Label("Mines| Tries | Time | State");
        VBox rounds = new VBox();
        for (String gameData : gameHistory) {
            Label gameLabel = new Label(gameData);
            rounds.getChildren().add(gameLabel);
        }

        VBox layout = new VBox(title,rounds);
        Scene popupScene = new Scene(layout, 150, 150);
        window.setScene(popupScene);
        window.show();
    }

    private static boolean isInt(TextField input, String message){
        try{
            int id = Integer.parseInt(message);
            idScenario = id;
            return true;
        }catch (NumberFormatException e){
            System.out.println("Error: " +message + "not an integer");
            return false;
        }
    }

    private static void writeScenarioFile(TextField values[],int id){
        try {
            FileWriter fileWriter = new FileWriter("src/medialab/SCENARIO-" + id + ".txt");
            PrintWriter printWriter = new PrintWriter(fileWriter);
            for(TextField value: values) {
                printWriter.println(value.getText());
            }
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
