import org.junit.jupiter.api.*;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class GameStateTest {
    private GameState gameState = new GameState();
    private int[][] clearBoard = gameState.getBoard().clone();
    private int[][] halfBoard = new int[][]{
            {0,1,1,1,0,0,0,0,0,0},
            {1,1,1,1,0,0,0,0,0,0},
            {2,1,2,1,2,2,2,2,0,0},
            {1,2,2,2,2,2,2,2,2,2},
            {1,1,1,1,2,0,0,0,0,0},
            {1,1,1,1,2,2,0,0,0,0},
    };
    private int[][] blockBoard = new int[][]{
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {2,2,2,2,2,2,2,2,2,2},
            {1,1,1,1,2,2,3,3,3,3},
            {1,1,1,1,2,2,3,3,3,3},
    };
    private int[][] finishBoard = new int[][]{
            {2,2,1,1,1,2,2,1,1,1},
            {2,2,2,2,1,2,2,2,2,1},
            {1,1,1,2,2,2,2,2,2,2},
            {1,1,2,2,2,2,1,1,1,2},
            {1,1,1,1,2,2,1,1,1,2},
            {1,1,1,1,2,2,1,1,1,2},
    };

    @Test
    void getNumberOfPlayersAfterAddingTwo() {
        gameState.newPlayer(1);
        gameState.newPlayer(2);
        assertEquals(gameState.getNumberOfPlayers(), 2);
    }

    @Test
    void getEmptyBoard() {
        assertTrue(Arrays.deepEquals(gameState.getBoard(), clearBoard));
    }

    @Test
    void makeOneMoveAndUpdateBoard() {
        gameState.setBoard(new Move(InfluenceCard.NONE, new Coordinates(1, 1)), 1);
        assertNotEquals(gameState.getBoard(), clearBoard);
    }

    @Test
    void addingOneNewPlayer() {
        gameState.newPlayer(1);
        assertEquals(gameState.players.size(), 1);
    }

    @Test
    void resettingBoardAfterGameOver() {
        gameState.setTestBoard(blockBoard);
        gameState.resetBoard();
        boolean notE = Arrays.deepEquals(blockBoard, gameState.getBoard());
        assertFalse(notE);
    }

    @Test
    void blockedState(){
        //available move without cards
        gameState.newPlayer(1);
        gameState.newPlayer(2);
        gameState.newBot(3);
        gameState.setTestBoard(halfBoard);
        assertFalse(gameState.blockedPlayer(1));

        //no more available moves without cards
        gameState.players.get(0).removeCard(InfluenceCard.REPLACEMENT);
        gameState.players.get(0).removeCard(InfluenceCard.FREEDOM);
        gameState.players.get(2).removeCard(InfluenceCard.REPLACEMENT);
        gameState.players.get(2).removeCard(InfluenceCard.FREEDOM);
        gameState.setTestBoard(blockBoard);
        assertTrue(gameState.blockedPlayer(1));
        assertTrue(gameState.blockedPlayer(3));
    }

    @Test
    void checkIfAllBlocked(){
        gameState.newPlayer(1);
        gameState.newPlayer(2);
        gameState.players.get(0).removeCard(InfluenceCard.REPLACEMENT);
        gameState.players.get(0).removeCard(InfluenceCard.FREEDOM);
        gameState.players.get(1).removeCard(InfluenceCard.REPLACEMENT);
        gameState.players.get(1).removeCard(InfluenceCard.FREEDOM);
        gameState.setTestBoard(blockBoard);
        assertTrue(gameState.blockedPlayer(1));
        assertFalse(gameState.blockedPlayer(2));
        assertTrue(gameState.checkGameOver());
    }

    @Test
    void checkCorrectWinner(){
        gameState.newPlayer(1);
        gameState.newPlayer(2);
        gameState.setTestBoard(finishBoard);
        assertEquals(gameState.endGame()[0], 30);
        assertEquals(gameState.endGame()[1], 30);
    }
}