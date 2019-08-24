import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

// This class (not yet fully implemented) will give access to the current state of the game.
final class GameState {
    static final int ROWS = 6;
    static final int COLUMNS = 10;
    private static int[][] board;
    List<PlayerLogic> players;

    //
    GameState() {
        resetBoard();
        players = new ArrayList<>();
    }

    // Returns number of players including bots
    int getNumberOfPlayers() {
        return players.size();
    }

    // Returns a rectangular matrix of board cells, with six rows and ten columns.
    // Zeros indicate empty cells.
    // Non-zero values indicate stones of the corresponding player.
    // E.g., 3 means a stone of the third player.
    int[][] getBoard() {
        return board;
    }

    // Applies move on board. Used after checking if move is allowed
    void setBoard(Move newMove, int ID) {
        Coordinates first = newMove.getMove();
        board[first.getX()][first.getY()] = ID;
        if(newMove.getCard() != InfluenceCard.NONE){
            players.get(ID-1).removeCard(newMove.getCard());
        }
    }

    // Checks if the specified move is allowed for the given player
    boolean isMoveAllowed(Move move, int ID) {
        InfluenceCard card = move.getCard();
        int x = move.getMove().getX();
        int y = move.getMove().getY();
        if(x < 0 || x > 6  || y < 0 || y > 10){
            return false;
        }
        switch (card){
            case NONE:
            case DOUBLE:
                return isEmptyCell(move.getMove()) && checkAdjacent(ID, x, y);
            case FREEDOM:
                return isEmptyCell(move.getMove());
            case REPLACEMENT:
                return checkAdjacent(ID, x, y);
        }
        try {
            throw new Exception("Incorrect card specified/System error");
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    // Check if nearby cells contain player-owned stones
    private boolean checkAdjacent(int ID, int x1, int y1) {
        for (int xc = x1 - 1; xc <= x1 + 1; xc++) {
            for (int yc = y1 - 1; yc <= y1 + 1; yc++){
                try {
                    if (board[xc][yc] == ID && !(xc == x1 && yc == y1)) {
                        return true;
                    }
                } catch(IndexOutOfBoundsException ignored){
                }
            }
        }
        return false;
    }

    // Add clients to the game
    void newPlayer(int ID) {
        players.add(new Player(ID));
    }

    // Creates a new AI and adds them to game
    void newBot(int ID){
        players.add(new Bot(this, ID));
    }

    // Randomly creates starting points for each player
    void generateRandomStart() {
        for (PlayerLogic player : players) {
            while(true) {
                int x = ThreadLocalRandom.current().nextInt(0, ROWS);
                int y = ThreadLocalRandom.current().nextInt(0, COLUMNS);

                Coordinates origin = new Coordinates(x, y);

                if (isEmptyCell(origin)) {
                    board[x][y] = player.getMyPlayerId();
                    break;
                }
            }
        }
    }

    // Checks if coordinate leads to an empty cell
    private boolean isEmptyCell(Coordinates coord){
        int x = coord.getX();
        int y = coord.getY();
        return board[x][y] == 0;
    }

    // Clears the board for new game or testing
    void resetBoard(){
        board = new int[ROWS][COLUMNS];
    }

    // Checks if player is blocked, and if so, set value to true
    boolean blockedPlayer(int ID){
        if(players.get(ID-1).hasCard(InfluenceCard.REPLACEMENT)){
            return false;
        }
        if(players.get(ID-1).hasCard(InfluenceCard.FREEDOM)){
            for(int x = 0; x < ROWS; x++){
                for(int y = 0; y < COLUMNS; y++){
                    if (board[x][y] == 0){
                        return false;
                    }
                }
            }
        }
        for(int x = 0; x < ROWS; x++){
            for(int y = 0; y < COLUMNS; y++){
                if (board[x][y] == 0){
                    if(checkAdjacent(ID, x, y)){
                        return false;
                    }
                }
            }
        }
        players.get(ID-1).setBlocked();
        return true;
    }

    // Return boolean value of player being set as blocked
    boolean isNotBlocked(int ID){
        return players.get(ID - 1).playerIsNotBlocked();
    }

    // Fast check if game has finished with all players (except one) being blocked
    boolean checkGameOver(){
        int count = 0;
        for (PlayerLogic player : players) {
            if(player.playerIsNotBlocked()){
                count++;
            }
        }
        return count <= 1;
    }

    // Inform server of winner and give final score
    int[] endGame(){
        int[] scores = new int[players.size()];
        int max = 0;
        for (int i = 0; i < players.size(); i++) {
            for (int x = 0; x < ROWS; x++) {
                for (int y = 0; y < COLUMNS; y++) {
                    if(board[x][y] == i+1)
                        scores[i] += 1;
                }
            }
            if(scores[i] > max)
                max = scores[i];
        }
        for (int i = 0; i < players.size(); i++) {
            if(max == scores[i])
                GameServer.setWinner(i+1);
        }
        return scores;
    }

    // JUnit testing purposes only
    void setTestBoard(int[][] arr){
        board = arr.clone();
    }
}

