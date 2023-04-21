import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ColorPicker;

/**
 * A class that represents the cell of the game board
 * 
 */
public class Tile extends Button{

    private int row;
    private int col;
    private boolean revealed;
    private boolean flagged;
    private boolean isMine;
    private boolean isHyperMine;
    private int adjacentMines;

    //graphics variables
    private static final Image FLAG_IMAGE = new Image("pngFiles/flag.png");
    private static final Image MINE_IMAGE = new Image("pngFiles/mine.png");
    private static final Image[] NUMBER_IMAGES = {
            null,
            new Image("pngFiles/one.png"),
            new Image("pngFiles/two.png"),
            new Image("pngFiles/three.png"),
            new Image("pngFiles/four.png"),
            new Image("pngFiles/five.png"),
            new Image("pngFiles/six.png"),
            new Image("pngFiles/seven.png"),
            new Image("pngFiles/eight.png")
    };

    /**
     * Constructor of class
     * @param row row of cell
     * @param col column of cell
     */
    public Tile(int row, int col ) {
        super();
        this.row = row;
        this.col = col;
        this.isMine = false;
        this.isHyperMine = false;
        this.flagged = false;
        this.revealed = false;
        this.adjacentMines = 0;
        setPrefSize(45, 45);
        setMaxSize(45, 45);
        this.getStyleClass().add("tile");
        //this.setStyle("-fx-background-color: #b6bdc4; -fx-border-color: black; -fx-border-width: 1px;");
        //this.setOnAction(e -> setRevealed(this.revealed));
        //this.setOnMouseClicked(e -> handleMouseClick(e));
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public boolean isHyperMine() {
        return isHyperMine;
    }

    public void setHyperMine(boolean isHyperMine) {
        this.isHyperMine = isHyperMine;
    }

    public boolean isMine(){
        return isMine;
    }

    public void setMine(boolean isMine){
        this.isMine = isMine;
    }

    public boolean isRevealed(){
        return revealed;
    }

    /**
     * Set's param revealed = true and changes the view of the cell
     * @param revealed true if cell is revealed
     */
    public void setRevealed(boolean revealed){
        if(this.revealed)
            return;
        this.revealed = revealed;
        if (isMine) {
            this.getStyleClass().add("tile-mine");
            ImageView view = new ImageView(MINE_IMAGE);
            view.setFitHeight(15);
            view.setX(20);
            view.setY(20);
            view.setPreserveRatio(true);
            this.setGraphic(view);
            this.setContentDisplay(ContentDisplay.TOP);
            //this.setDisable(true);
        } else {
            this.getStyleClass().add("tile-revealed");
            ImageView view = new ImageView(NUMBER_IMAGES[adjacentMines]);
            view.setFitHeight(15);
            view.setX(20);
            view.setY(20);
            view.setPreserveRatio(true);
            this.setGraphic(view);
            this.setContentDisplay(ContentDisplay.TOP);
            //this.setDisable(true);
        }
    }

    public boolean isFlagged(){
        return flagged;
    }

    /**
     * Set's param flagged  and changes the view of the cell
     */
    public void setFlagged(){
        this.flagged = !this.flagged;
        if (this.flagged) {
            ImageView view = new ImageView(FLAG_IMAGE);
            view.setFitHeight(15);
            view.setX(20);
            view.setY(20);
            view.setPreserveRatio(true);
            this.setGraphic(view);
            this.setContentDisplay(ContentDisplay.TOP);
        } else {
            this.setGraphic(null);
        }
    }

    public int getAdjacentMines(){
        return adjacentMines;
    }

    public void setAdjacentMines(int adjacentMines) {
        this.adjacentMines = adjacentMines;
    }


    public String toString() {
        String str = this.col + "," + this.row + "," + (isHyperMine ? "1" : "0");

        return str;
    }



}
