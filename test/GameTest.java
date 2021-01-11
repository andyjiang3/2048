import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.*;


public class GameTest {

    // Testing using the boards below since it would be hard to
    // test on a randomly generated board
    /*
     * Test Board 1:
     * 
     * 0 0 0 0 
     * 0 0 0 0 
     * 4 2 2 2 
     * 2 2 2 4
     * 
     * Test Board 2: 
     * 8 8 8 8 
     * 8 8 8 8 
     * 8 8 8 8
     * 8 8 8 8
     * 
     * Test Board 3: 
     * 8 16 8 16 
     * 16 8 16 8 
     * 8 16 8 16 
     * 8 16 8 0
     * 
     * Test Board 3: 
     * 0 0 0 0 
     * 0 0 0 0 
     * 2 2 2 2 
     * 4 4 4 4
     */

    // Only need logic side, independent from graphics.
    GameLogic logic = new GameLogic();

    // addTile function Test
    @Test
    public void addTileTest() {
        logic.addTile();
        logic.addTile();

        // check that exactly two tile not empty.
        assertEquals(2, 16 - logic.emptySpaces().size());
    }

    // addTile full board test, make sure tile doesn't get added
    @Test
    public void addTileInFullBoardTest() {
        // Test using Board 2
        int[][] board2 = { { 8, 8, 8, 8 }, 
                           { 8, 8, 8, 8 }, 
                           { 8, 8, 8, 8 }, 
                           { 8, 8, 8, 8 } };
        
        logic.setBoard(board2);
        logic.addTile();

        // check to make sure none of the tile is 2 or 4
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                assertEquals(8, logic.getTile(i, j));
            }
        }
    }

    // left and down have the same logic as below.

    // Move up test (testing shifting)
    @Test
    public void moveUpTest() {
        // Test using Board 1
        int[][] board1 = { { 0, 0, 0, 0 }, 
                           { 0, 0, 0, 0 }, 
                           { 4, 2, 2, 2 }, 
                           { 2, 2, 2, 4 } };

        logic.setBoard(board1);
        logic.moveTiles(1);

        // check that bottom 2x4 is empty
        for (int i = 2; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                assertEquals(0, logic.getTile(i, j));
            }
        }
    }

    // Move right test (testing merge)
    @Test
    public void moveRightTest() {
        // Test using Board 2
        int[][] board2 = { { 8, 8, 8, 8 }, 
                           { 8, 8, 8, 8 }, 
                           { 8, 8, 8, 8 }, 
                           { 8, 8, 8, 8 } };

        logic.setBoard(board2);
        logic.moveTiles(2);

        // check that left 4x1 is empty
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 1; j++) {
                assertEquals(0, logic.getTile(i, j));
            }
        }

        // 16s on last column
        for (int i = 0; i < 4; i++) {
            for (int j = 3; j < 4; j++) {
                assertEquals(16, logic.getTile(i, j));
            }
        }
    }

    // performing move that where board is unchanged doesn't add tile
    @Test
    public void moveNoAddTest() {
        // Test using Board 4
        int[][] board1 = { { 0, 0, 0, 0 }, 
                           { 0, 0, 0, 0 }, 
                           { 2, 2, 2, 2 }, 
                           { 4, 4, 4, 4 } };

        logic.setBoard(board1);

        // down board is same
        // method adds tile if board is changed
        logic.makeMove(3);

        int[][] boardExcepted = { { 0, 0, 0, 0 }, 
                                  { 0, 0, 0, 0 }, 
                                  { 2, 2, 2, 2 }, 
                                  { 4, 4, 4, 4 } };

        assertArrayEquals(boardExcepted, logic.getBoard());
    }

    // move tiles, adding another tile (making so that theres no valid moves)
    // ends game test (also make Move test)
    @Test
    public void moveAddEndTest() {
        // Test using Board 3
        int[][] board3 = { { 8, 16, 8, 16 }, 
                           { 16, 8, 16, 8 }, 
                           { 8, 16, 8, 16 }, 
                           { 8, 16, 8, 0 } };

        logic.setBoard(board3);
        logic.makeMove(2);

        assertEquals(GameState.LOSS, logic.getGameState());
    }

    // transpose test, reverse follows same logic
    @Test
    public void transposeTest() {
        // Test using Board 1
        int[][] board = { { 0, 0, 0, 0 }, 
                          { 0, 0, 0, 0 }, 
                          { 4, 2, 2, 2 }, 
                          { 2, 2, 2, 4 } };

        int[][] boardTransposed = { { 0, 0, 4, 2 }, 
                                    { 0, 0, 2, 2 }, 
                                    { 0, 0, 2, 2 },
                                    { 0, 0, 2, 4 } };

        logic.setBoard(board);
        logic.transposeBoard();

        assertArrayEquals(boardTransposed, logic.getBoard());
    }

    // transpose with 0s test, make sure size doesn't change
    @Test
    public void transposeZerosTest() {
        int[][] board = { { 0, 0, 0, 0 }, 
                          { 0, 0, 0, 0 }, 
                          { 0, 0, 0, 0 }, 
                          { 0, 0, 0, 0 } };

        int[][] boardTransposed = { { 0, 0, 0, 0 }, 
                                    { 0, 0, 0, 0 }, 
                                    { 0, 0, 0, 0 }, 
                                    { 0, 0, 0, 0 } };

        logic.setBoard(board);
        logic.transposeBoard();

        assertArrayEquals(boardTransposed, logic.getBoard());
    }

    // score test
    @Test
    public void scoreTest() {
        // Test using Board 1
        int[][] board = { { 0, 0, 0, 0 }, 
                          { 0, 0, 0, 0 }, 
                          { 4, 2, 2, 2 }, 
                          { 2, 2, 2, 4 } };

        logic.setBoard(board);
        logic.makeMove(3);

        assertEquals(8, logic.getScore());
    }

    // same 2 col, score test
    @Test
    public void scoreColTest() {
        GameLogic scoreTest = new GameLogic();
        // Test using Board 2
        int[][] board2 = { { 8, 8, 8, 8 }, 
                           { 8, 8, 8, 8 }, 
                           { 8, 8, 8, 8 }, 
                           { 8, 8, 8, 8 } };
        scoreTest.setBoard(board2);

        scoreTest.makeMove(2);

        assertEquals(128, scoreTest.getScore());
    }

    // score should be the same if no merge is performed
    @Test
    public void scoreNoMergeTest() {
        // Test using Board 3
        int[][] board3 = { { 8, 16, 8, 16 }, 
                           { 16, 8, 16, 8 }, 
                           { 8, 16, 8, 16 }, 
                           { 8, 16, 8, 0 } };

        logic.setBoard(board3);
        logic.makeMove(2);

        assertEquals(0, logic.getScore());
    }

    // empty board, performing move does not change score
    @Test
    public void scoreEmptyBoardTest() {
        int[][] board = { { 0, 0, 0, 0 }, 
                          { 0, 0, 0, 0 }, 
                          { 0, 0, 0, 0 }, 
                          { 0, 0, 0, 0 } };

        logic.setBoard(board);
        logic.makeMove(1);

        assertEquals(0, logic.getScore());
    }

    // undoing move after game ended doesn't change board
    @Test
    public void undoGameNotStartedTest() {
        GameLogic undoEdge = new GameLogic();

        // Game state should be not started.
        // Test using Board 1
        int[][] board1 = { { 0, 0, 0, 0 }, 
                           { 0, 0, 0, 0 }, 
                           { 4, 2, 2, 2 }, 
                           { 2, 2, 2, 4 } };

        undoEdge.setBoard(board1);
        undoEdge.makeMove(3);
        undoEdge.undoMove();

        // array should not be the same, test. 4 not 2.
        assertEquals(4, undoEdge.getTile(3, 2));
    }

}
