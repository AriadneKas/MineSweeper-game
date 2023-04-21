import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.awt.event.MouseEvent;
import javafx.event.EventHandler;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;

/**
 * A class that represents tha game board and implements methods for the game logic
 */
public class Board extends GridPane {

    /**
     * Number of rows of the game board
     */
    private int numRows;
    /**
     * Number of columns of the game board
     */
    private int numCols;
    /**
     * Number of mines of the game board
     */
    private int numMines;
    /**
     * A 2D array of Tiles that contains all the cells
     */
    private Tile[][] board;
    /**
     * The time limit of every game
     */
    private int timeLimit;
    /**
     * Number of cells that have been revealed. A cell is revealed when the player presses left click
     */
    private int numCellsRevealed;
    /**
     * Variable to know if the game board has a hyper mine
     */
    private boolean hyperMine;
    /**
     * Counter for the number of left clicks to know the number of tries the player has done
     */
    private int numOfLeftClicks;
    /**
     * Variable to track the game time
     */
    private Timeline timeline;
    /**
     * Variable to save the total game time
     */
    private int gameTime;
    /**
     * Counter for the number of flags that are pressed
     */
    private int flagsPressed;
    /**
     * Boolean variable to know when game is over
     */
    private boolean isGameOver;

    /**
     * Constructor of the class
     */
    public Board() {
        super();
        this.hyperMine = false;
        this.numOfLeftClicks =0;
        this.flagsPressed = 0;
        //this.setMaxSize();

        //setPadding(new Insets(10));
        setAlignment(Pos.CENTER);
        //this.board = new Tile[numRows][numCols];

    }

    /**
     * Initialize and re-initialize a new game
     * @param numRows for the number of rows
     * @param numCols for the number of columns
     * @param numMines for the number of mines
     * @param HyperMine true if the board has a hyper mine
     * @param timeLimit for the time limit
     */
    public void initializeNewGame(int numRows, int numCols, int numMines, boolean HyperMine,int timeLimit){
        this.numRows = numRows;
        this.numCols = numCols;
        this.numMines = numMines;
        this.hyperMine = HyperMine;
        this.timeLimit = timeLimit;
        this.numOfLeftClicks =0;
        this.flagsPressed = 0;
        isGameOver = false;
        startTimer();

        this.board = new Tile[numRows][numCols];
        generateBoard();
    }

    public int getNumRows() {
        return numRows;
    }

    public int getNumCols() {
        return numCols;
    }

    public int getNumMines() {
        return numMines;
    }

    public int getFlagsPressed() {
        return flagsPressed;
    }

    public boolean gameOver(){
        return isGameOver;
    }


    /**
     * Method that reveals the cell after the left click is pressed.
     * Checks if the cell is a mine and if it is it calls revealmines method.
     * If the cell is not a mine it calls revealNeighbors and checks if the
     * game is won.
     * @param row
     * @param col
     */
    public void revealCell(int row, int col) {
        if (!board[row][col].isRevealed() && !board[row][col].isFlagged()) {

            if (board[row][col].isMine()) {
                revealMines();
            } else if (!board[row][col].isMine()) {
                revealNeighbors(row, col);
                if(isGameWon()){
                    gameOver(true);
                }
            }
        }
    }

    public boolean isGameWon() {
        int numSafeCells = numRows * numCols - numMines;
        return numCellsRevealed == numSafeCells;
    }

    /**
     * Method that stops the timer sets disabled the cells and calls method saveGame
     * of class GameHistory so the game infos are saved to a file
     * @param gameState state of the game: win or lost
     */
    public void gameOver(boolean gameState){
        this.timeline.stop();
        isGameOver = true;

        //if the game is over
        if(!gameState){
            for(int r=0;r<getNumRows();r++){
                for(int c=0;c<getNumCols();c++){
                    if(!board[r][c].isMine()) {
                        board[r][c].setDisable(true);
                    }
                }
            }
            PopUpBoxes.gameOverBox("Game Over", "You clicked over a mine");
            GameHistory.saveGame(this.numMines, gameTime, numOfLeftClicks, false);
        }
        //if game is won
        else if(gameState){
            PopUpBoxes.gameOverBox("Game Won", "Congratulations you won");
            GameHistory.saveGame(this.numMines, gameTime, numOfLeftClicks, true);
        }


    }

    /**
     * Reveals all the mines and calls gameOver method
     */
    public void revealMines() {
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                if (board[row][col].isMine()) {
                    board[row][col].setRevealed(true);
                }
            }
        }
        gameOver(false);
    }

    /**
     * Recursive method to reveal the 8 cells that surround the given cell
     * When a cell has no adjacent mines we reveal its neighbors
     * @param row row of the cell
     * @param col column of the cell
     */
    public void revealNeighbors(int row, int col) {
        // If the cell at (row, col) is already revealed or flagged, return
        if (board[row][col].isFlagged() || board[row][col].isRevealed()) {
            return;
        }

        // If the cell at (row, col) has a mine, return
        if (board[row][col].isMine()) {
            return;
        }

        // Reveal the cell at (row, col)
        board[row][col].setRevealed(true);
        numCellsRevealed++;

        // If the cell at (row, col) has no adjacent mines, reveal its neighbors
        if (board[row][col].getAdjacentMines() == 0) {
            for (int i = row - 1; i <= row + 1; i++) {
                for (int j = col - 1; j <= col + 1; j++) {
                    // If the current cell is out of bounds or equal to the original cell, skip it
                    if (i < 0 || i >= numRows || j < 0 || j >= numCols || (i == row && j == col)) {
                        continue;
                    }
                    // Recursively reveal the neighboring cell
                    revealNeighbors(i, j);
                }
            }
        }
    }

    /**
     * Method that reveals the hole row and column of the cell with the hyper mine
     * @param row for the row of the cell
     * @param col for the column of the cell
     */
    public void hyperMineReveal(int row, int col){
        for(int r = 0; r<numCols; r++){
            board[r][col].setRevealed(true);
        }
        for(int c = 0; c<numCols; c++){
            board[row][c].setRevealed(true);
        }
    }

    /**
     * Method that initializes tha game board.
     * Creates the cells(buttons) and sets in each of them a handler for when the mouse is pressed,
     * adds the mines and calls generateMinesFile method,
     * places Hyper mine
     * fills other cells with number of adjacent mines
     */
    private void generateBoard() {
        Random rand = new Random();
        Tile[] mines = new Tile[getNumMines()];

        //create cells
        for(int r=0;r<getNumRows();r++){
            for(int c=0;c<getNumCols();c++){
                Tile cell = new Tile(r, c);
                cell.setOnMousePressed(this::handleTileClick);
                board[r][c] = cell;
                this.add(cell, r, c);
                //board[r][c].setDisable(false);
            }
        }


        // Add mines
        int minesAdded = 0;
        while (minesAdded < getNumMines()) {
            int row = rand.nextInt(getNumRows());
            int col = rand.nextInt(getNumCols());
            if (!board[row][col].isMine()) {
                board[row][col].setMine(true);
                mines[minesAdded++] = new Tile(row, col);
            }
        }

        // Place hyper mine if any
        if (hyperMine) {
            int minePos = rand.nextInt(getNumMines());

            mines[minePos-1].setHyperMine(true);
            board[mines[minePos-1].getRow()][mines[minePos-1].getCol()].setHyperMine(true);
            System.out.println(mines[minePos-1].getRow()+" "+mines[minePos-1].getCol());
        }


        try {
                generateMinesFile("./mines.txt", mines);
            } catch (IOException e) {
                e.printStackTrace();
        }

        // fill other cells with the number of adjacent mines
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                if (!board[r][c].isMine() || !board[r][c].isHyperMine()) {
                    int adjacentMines = countAdjMines(r, c);
                    board[r][c].setAdjacentMines(adjacentMines);
                }
            }
        }



    }

    /**
     * Method to count the adjacent mines of the tile
     * @param row Row of the cell
     * @param col Column of the cell
     * @return the number of the adjacent mines of the cell
     */
    private int countAdjMines(int row, int col) {
        int counter = 0;
        for (int r = row - 1; r <= row + 1; r++) {
            for (int c = col - 1; c <= col + 1; c++) {
                if(r >= 0 && r < numRows && c >= 0 && c < numCols && board[r][c].isMine())
                    counter++;
            }
        }

        return counter;
    }

    /**
     * Method that is used as a timer and when the gameTime is equal to
     * timeLimit it calls method gameOver to terminate game
     */
    public void startTimer() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            gameTime++;
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        if(timeLimit-gameTime == 0){
            gameOver(false);
        }
    }

    /**
     * Handler method for when the player presses lift or right mouse click
     * Left click reveals the cell and right click toggles flag
     * @param event the mouse event
     */
    private void handleTileClick(javafx.scene.input.MouseEvent event) {
        Tile clickedTile = (Tile) event.getSource();

        if (event.isPrimaryButtonDown()) {
            revealCell(clickedTile.getRow(), clickedTile.getCol());
            this.numOfLeftClicks++;
        } else if (event.isSecondaryButtonDown()) {
            if(flagsPressed >= numMines)
                return;
            clickedTile.setFlagged();
            if(clickedTile.isFlagged()){
                if(clickedTile.isHyperMine() && numOfLeftClicks <= 4){
                    hyperMineReveal(clickedTile.getRow(),clickedTile.getCol());
                }
                flagsPressed++;
            }
            else {
                flagsPressed--;
            }
        }
    }

    /**
     * Method that writes the mine locations to a file
     * @param fileName for the name of the file we want to write
     * @param mines Array of Tile objects that implements the mine cells of the board
     * @throws IOException
     */
    public void generateMinesFile(String fileName, Tile[] mines) throws IOException {
        FileWriter fileWriter = new FileWriter(fileName);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        for(Tile mine: mines) {
            printWriter.println(mine.toString());
        }
        printWriter.close();
    }
}