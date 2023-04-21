import java.util.*;
import java.time.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.*;
public class GameHistory {
    private static final String FILENAME = "game_history.txt";

    //takes all the parameters, formats them as string and writes then to file
    public static void saveGame(int numMines, long gameTime, int numLeftClicks, boolean playerWon) {
        String state = "Lost";
        if(playerWon){
            state = "Won";
        }
        try (FileWriter writer = new FileWriter("game_history.txt", true)) {
            writer.write(  "  "+numMines + "    |    " + numLeftClicks + "    |    " + gameTime + "    |    " +  state + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Read game history and keep only the last five games
        List<String> gameHistory = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File("game_history.txt"))) {
            while (scanner.hasNextLine()) {
                gameHistory.add(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int numGames = gameHistory.size();
        if (numGames > 5) {
            try (PrintWriter writer = new PrintWriter("game_history.txt")) {
                for (int i = numGames - 5; i < numGames; i++) {
                    writer.println(gameHistory.get(i));
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    //reads the game history from file and returns a list of strings
    public static List<String> getGameHistory() {
        List<String> gameHistory = new ArrayList<>();

        // Read the game history from the file
        try (BufferedReader reader = new BufferedReader(new FileReader(FILENAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                gameHistory.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return gameHistory;
    }
}