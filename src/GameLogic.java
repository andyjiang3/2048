import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class GameLogic {

    // Probability of generating a 2 tile. not all caps because style checker complain
    private final double tileProbability = 0.9;

    // CONCEPT 1: 2-D Arrays
    private int[][] board;
    private int score;
    private int bestScore;
    private GameState gameState;

    // CONCEPT 2: Collections
    // Includes initial board.
    private LinkedList<int[][]> moveHistory;
    private LinkedList<Integer> scoreHistory;

    /**
     * Constructor sets up game state.
     */
    public GameLogic() {
        board = new int[4][4];
        score = 0;
        gameState = GameState.NOT_STARTED;
        moveHistory = new LinkedList<int[][]>();
        scoreHistory = new LinkedList<Integer>();

        bestScore = 0;
        if (fileExist("game_stats.txt")) {
            readFileAndSet("game_stats.txt");
        }
    }

    // TILE LOGIC (Ex. Merging, Sliding)
    // ====================================================

    public void addTile() {
        List<Integer> empty = emptySpaces();
        if (empty.isEmpty()) {
            return;
        }
        int coord = empty.get((int) (Math.random() * empty.size()));
        board[(int) (coord / 10)][coord % 10] = Math.random() < tileProbability ? 2 : 4;

    }

    public int slideUp() {
        /*
         * NOTE: This method only slide upwards. Use transpose & reverse to orient the
         * board accordingly.
         * 
         * Merge each column individually. Keep track of score and return at end.
         */

        int moveScore = 0;

        // Step 1: Clone board
        int[][] boardCopy = new int[4][4];
        for (int i = 0; i < board.length; i++) {
            boardCopy[i] = Arrays.copyOf(board[i], 4);
        }

        // Step 2: Perform slide up (on every column individually)
        for (int col = 0; col < boardCopy[0].length; col++) {

            // Step 2.1: Shift column (ex. 2 0 2 2 = 2 2 2 0)
            LinkedList<Integer> shiftedCol = new LinkedList<Integer>();

            for (int row = 0; row < boardCopy.length; row++) {
                if (boardCopy[row][col] > 0) {
                    shiftedCol.add(boardCopy[row][col]);
                }
            }

            // Step 2.2: Merge column (ex. 2 2 2 0 = 4 2 0 0)
            LinkedList<Integer> mergedCol = new LinkedList<Integer>();
            while (shiftedCol.size() > 1) {
                int head = shiftedCol.pop();
                // Look at value of next
                int next = shiftedCol.peek();
                if (head == next) {
                    // Merge 2 tiles (ex. 2 2 = 4)
                    mergedCol.add(head * 2);
                    moveScore += head * 2;
                    shiftedCol.pop();
                    if (head * 2 == 2048) {
                        gameState = GameState.WIN;
                    }

                    /*
                     * Possible feature - Second Merge: This happens when column tiles are all the
                     * same value. Ex. 2 2 2 2
                     * 
                     * In this case, after the first merge: Loop 1. mergedCol: 4, shiftedCol: 2 2
                     * (4) Loop 2. mergedCol: 8, shiftedCol:
                     * 
                     * (ex. 2 2 2 2 -> 4 2 2 0 -> 4 4 0 0 -> 8 0 0 0)
                     * 
                     * if (!mergedCol.isEmpty() && mergedTile == mergedCol.peekLast()) { score +=
                     * mergedCol.removeLast() * 2; mergedCol.add(head * 2 * 2); } else {
                     * mergedCol.add(head * 2); }
                     */

                } else {
                    mergedCol.add(head);
                }
            }

            // Add rest of tile that is still in list
            mergedCol.addAll(shiftedCol);

            for (int row = 0; row < boardCopy.length; row++) {
                if (mergedCol.size() > 0) {
                    board[row][col] = mergedCol.pop();
                } else {
                    board[row][col] = 0;
                }
            }

        }

        return moveScore;

    }

    // MOVE LOGIC ===============================================================

    // Return if a tile needs to be added
    public boolean moveTiles(int direction) {
        /*
         * 1 - up, 2 - right, 3 - down, 4 - left
         * 
         * right - needs transpose then reverse down - needs reverse left - needs
         * transpose up - needs none
         */

        int[][] boardCopy = new int[4][4];
        for (int i = 0; i < board.length; i++) {
            boardCopy[i] = Arrays.copyOf(board[i], 4);
        }

        if (direction == 2 || direction == 4) {
            transposeBoard();
        }

        if (direction == 2 || direction == 3) {
            reverseBoard();
        }

        score += slideUp();

        // orient board back
        if (direction == 2 || direction == 3) {
            reverseBoard();
        }

        if (direction == 2 || direction == 4) {
            transposeBoard();
        }

        // Check if board changed. If it did, add a tile
        for (int row = 0; row < boardCopy.length; row++) {
            for (int col = 0; col < boardCopy[0].length; col++) {
                if (board[row][col] != boardCopy[row][col]) {
                    return true;
                }
            }
        }

        return false;

    }

    // IMPORTANT METHOD: Combine all methods
    public void makeMove(int direction) {
        Boolean addTile = moveTiles(direction);

        if (addTile) {
            addTile();

            // Only store and write valid moves
            // Push instead of add so that it is added to front of list
            moveHistory.push(getBoard());
            scoreHistory.push(score);
            writeGameStateFile();
        }

        checkLoss();

        // delete game state file if game is won or loss, additionally update Game Stat
        if (gameState == GameState.WIN || gameState == GameState.LOSS) {
            deleteFile("game_state.txt");
            updateBestScore();
        }
    }

    public boolean undoMove() {

        if (!moveHistory.isEmpty() && gameState == GameState.STARTED) {

            // prevent user from removing the initial board
            if (moveHistory.size() == 1) {
                board = moveHistory.peek();
                score = scoreHistory.peek();
            } else {
                // Remove most recent move
                moveHistory.pop();
                // Get board before that move
                board = moveHistory.peek();
                scoreHistory.pop();
                score = scoreHistory.peek();
            }

            // update file
            writeGameStateFile();
            return true;
        }

        return false;
    }

    // BOARD MANIPULATION =======================================================

    // transpose - rows -> cols, cols -> rows
    public void transposeBoard() {
        int[][] transposed = new int[4][4];
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[0].length; col++) {
                transposed[col][row] = board[row][col];
            }
        }

        board = transposed;
    }

    // reversing values in each col
    public void reverseBoard() {
        int[][] reversed = new int[4][4];
        for (int col = 0; col < board[0].length; col++) {
            for (int row = 0; row < board.length; row++) {
                reversed[board.length - 1 - row][col] = board[row][col];
            }
        }

        board = reversed;
    }

    // FILE I/0 =============================================================

    // CONCEPT 3: File I/O
    public void writeGameStateFile() {
        File file;

        // Try to write to existing file. If it doesn't exist, create new file.
        try {
            file = Paths.get("game_state.txt").toFile();
        } catch (InvalidPathException e) {
            file = new File("game_state.txt");
        }

        BufferedWriter bw = null;

        try {

            // Write board to file, in format ex. 2 2 2 2 (row 1)
            bw = new BufferedWriter(new FileWriter(file));

            // Store board
            for (int row = 0; row < board.length; row++) {
                for (int col = 0; col < board[0].length; col++) {
                    bw.write(board[row][col] + " ");
                }
                bw.newLine();
            }

            // Store score
            bw.write(String.valueOf(score));

        } catch (IOException e) {
            System.out.println("Error: " + e);

            // Close file
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (Exception ex) {
                System.out.println("Can't close BufferedWriter: " + ex);
            }
        }
    }

    public void writeGameStatsFile() {
        File file;

        // Try to write to existing file. If it doesn't exist, create new file.
        try {
            file = Paths.get("game_stats.txt").toFile();
        } catch (InvalidPathException e) {
            file = new File("game_stats.txt");
        }

        BufferedWriter bw = null;

        try {
            // Write board to file, in format ex. 2 2 2 2 (row 1)
            bw = new BufferedWriter(new FileWriter(file));

            // Store score
            bw.write(String.valueOf(bestScore));
            bw.newLine();
            bw.flush();

        } catch (IOException e) {
            System.out.println("Error: " + e);

            // Close file
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (Exception ex) {
                System.out.println("Can't close BufferedWriter: " + ex);
            }
        }

    }

    // CONCEPT 3: File I/O
    // Read from fileName
    public void readFileAndSet(String fileName) {

        BufferedReader br = null;
        try {
            String line;
            File file = new File(fileName);
            br = new BufferedReader(new FileReader(file));

            if (fileName.equals("game_state.txt")) {
                for (int i = 0; i < 4; i++) {
                    if ((line = br.readLine()) != null) {
                        // put each value in string into array as element
                        String[] value = line.split(" ");
                        // assign value to board
                        for (int j = 0; j < value.length; j++) {
                            board[i][j] = Integer.parseInt(value[j]);
                        }

                    }
                }

                // Read score
                if ((line = br.readLine()) != null) {
                    score = Integer.parseInt(line);
                }
            } else {
                if ((line = br.readLine()) != null) {
                    bestScore = Integer.parseInt(line);
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("File Not Found Error: " + e);
        } catch (IOException e) {
            System.out.println("Error: " + e);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (Exception ex) {
                System.out.println("Can't close BufferedReader: " + ex);
            }
        }
    }

    // delete file when game ends/user won
    public void deleteFile(String fileName) {
        if (fileExist(fileName)) {
            File file = new File("game_state.txt");
            file.delete();
        }
    }

    public static boolean fileExist(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }

    // GAME STATE =========================================================

    public void checkLoss() {

        if (!emptySpaces().isEmpty()) {
            return;
        }

        // check if same number in cols
        for (int col = 0; col < board[0].length; col++) {

            LinkedList<Integer> checkingCol = new LinkedList<Integer>();

            for (int row = 0; row < board.length; row++) {
                checkingCol.add(board[row][col]);

            }

            while (checkingCol.size() > 1) {
                int head = checkingCol.pop();
                int next = checkingCol.peek();
                if (head == next) {
                    return;
                }
            }
        }

        // check if same number in rows
        for (int row = 0; row < board.length; row++) {

            LinkedList<Integer> checkingCol = new LinkedList<Integer>();

            for (int col = 0; col < board[0].length; col++) {
                checkingCol.add(board[row][col]);

            }

            while (checkingCol.size() > 1) {
                int head = checkingCol.pop();
                int next = checkingCol.peek();
                if (head == next) {
                    return;
                }
            }
        }

        gameState = GameState.LOSS;
    }

    public GameState getGameState() {
        return gameState;
    }

    // START GAME =========================================================

    /**
     * reset (re-)sets the game state to start a new game.
     */
    public void newGame() {
        updateBestScore();
        board = new int[4][4];
        gameState = GameState.STARTED;
        score = 0;

        // Empty history
        moveHistory = new LinkedList<int[][]>();
        scoreHistory = new LinkedList<Integer>();
        addTile();
        addTile();

        // Starting board, add to history and write to file
        moveHistory.push(getBoard());
        scoreHistory.push(score);
        writeGameStateFile();

    }

    public void loadGame() {

        updateBestScore();
        gameState = GameState.STARTED;
        readFileAndSet("game_state.txt");

        // Empty history
        moveHistory = new LinkedList<int[][]>();
        scoreHistory = new LinkedList<Integer>();

        moveHistory.push(getBoard());
        scoreHistory.push(score);

    }

    // MISC =========================================================

    public int getScore() {
        return score;
    }

    public int getBestScore() {
        return bestScore;
    }

    // update best score
    public void updateBestScore() {
        if (score > bestScore) {
            bestScore = score;
            writeGameStatsFile();
        }
    }

    public List<Integer> emptySpaces() {
        List<Integer> empty = new ArrayList<Integer>();
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                // check if empty
                if (board[row][col] == 0) {
                    // For Testing: System.out.println("expected row: " + row + ", expected col:" +
                    // col);
                    empty.add((row * 10) + col); // store coord in format: rowcol
                }
            }
        }
        return empty;
    }

    public int[][] getBoard() {
        int[][] boardCopy = new int[4][4];
        for (int i = 0; i < board.length; i++) {
            boardCopy[i] = Arrays.copyOf(board[i], 4);
        }
        return boardCopy;
    }

// TESTING =========================================================

    public void printBoard() {
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                System.out.print(board[row][col] + ", ");
            }
            System.out.println();
        }
        System.out.println();
    }

    // For testing only.
    public void setBoard(int[][] board2) {
        int[][] boardCopy = new int[4][4];
        for (int i = 0; i < board.length; i++) {
            boardCopy[i] = Arrays.copyOf(board2[i], 4);
        }

        board = boardCopy;
    }

    public int getTile(int row, int col) {
        return board[row][col];
    }

    /**
     * This main method illustrates how the model is completely independent of the
     * view and controller. We can play the game from start to finish without ever
     * creating a Java Swing object.
     * 
     * This is modularity in action, and modularity is the bedrock of the
     * Model-View-Controller design framework.
     * 
     * Run this file to see the output of this method in your console.
     */
    public static void main(String[] args) {
        // For testing, simulate game
        GameLogic t = new GameLogic();
        t.addTile();
        t.printBoard();
        t.addTile();
        t.printBoard();
        t.addTile();
        t.printBoard();
        System.out.println("Up");
        t.moveTiles(1);
        t.printBoard();
        t.addTile();
        t.printBoard();
        t.addTile();
        t.printBoard();
        System.out.println("Right");
        t.moveTiles(2);
        t.printBoard();
        t.addTile();
        t.printBoard();
        t.addTile();
        t.printBoard();
        System.out.println("Down");
        t.moveTiles(3);
        t.printBoard();
        t.addTile();
        t.printBoard();
        t.addTile();
        t.printBoard();
        System.out.println("Left");
        t.moveTiles(4);
        t.printBoard();
        t.addTile();
        t.printBoard();
        t.addTile();
        t.printBoard();
        System.out.println("Up");
        t.moveTiles(1);
        t.printBoard();

    }
}
