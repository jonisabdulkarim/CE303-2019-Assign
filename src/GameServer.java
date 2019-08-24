import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameServer {

    private static final int PORT = 8888;
    private static final List<GameService> services = new ArrayList<>();
    private static int clients;
    private static int bots;
    private static int winner;

    public static void main(String[] args) throws IOException, InterruptedException {
        GameState game = new GameState();
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Started GameServer at port " + PORT);
        System.out.println("Waiting for clients to connect...");

        //initial client
        Socket socket = serverSocket.accept();
        GameService service = new GameService(game, socket, 1);
        services.add(service);
        Thread t = new Thread(service);
        t.start();

        System.out.println("Primary client connected");

        while(!service.enter){
            t.join(2000);
        }

        for (int i = 1; i < clients; i++) {
            socket = serverSocket.accept();
            service = new GameService(game, socket, (i+1));
            services.add(service);
            t = new Thread(service);
            t.start();
            service.message("Player " + (i+1) + " has connected.");
        }

        if(clients > 1)
            messageAll("All players have connected.");

        if(bots != 0)
            messageAll("Setting up bots");


        for (int i = clients; i < bots + clients; i++) {
            game.newBot((i+1));
        }

        messageAll("Setting up board...");
        messageAll("Empty board:");
        printBoard(game);

        boolean gameOver = false;

        game.generateRandomStart();

        messageAll("Generating random starts... ");
        printBoard(game);

        messageAll("Game is ready.");



        while(!gameOver){
            for (int i = 0; i < clients; i++) {
                services.get(i).switchTurn();
                if(game.blockedPlayer(i+1)){
                    messageAll("Player " + (i+1) + " is blocked.");
                }
                else if(game.isNotBlocked(i + 1)){
                    messageAll("It is now player " + (i+1) + "'s turn");
                    while(services.get(i).isCurrent()){
                        Thread.sleep(2000);
                    }
                    printBoard(game);
                }
            }
            for (int i = clients; i < bots + clients; i++) {
                if (game.blockedPlayer(i+1)){
                    messageAll("Player " + (i+1) + " is blocked.");
                }
                else if(game.isNotBlocked(i + 1)){
                    messageAll("It is now player " + (i+1) + "'s turn");
                    Thread.sleep(2000);
                    if(!game.players.get(i).makeMove())
                        System.out.println("false");
                    printBoard(game);
                }
            }
            if(game.checkGameOver())
                gameOver = true;
        }

        messageAll("The game has ended. Thanks for playing!");
        int[] scores = game.endGame();
        for (int i = 0; i < game.players.size(); i++) {
            messageAll("Score for player " + (i+1) + " is " + scores[i]);
        }
        messageAll("Winner is: " + winner);
    }

    static void printBoard(GameState game) {
        int y = 1;
        messageAll("x  1  2  3  4  5  6  7  8  9  10");
        for (int[] row :
                game.getBoard()) {
            messageAll(y + " " + Arrays.toString(row));
            y++;
        }
    }

    static void messageAll(String s){
        for (GameService service : services) {
            service.message(s);
        }
    }

    static boolean isPrimary(){
        return services.size() == 1;
    }

    static void setClients(int n){
        clients = n;
    }

    static void setBots(int n){
        bots = n;
    }

    static void setWinner(int w){ winner = w; }

    static int getClients() {
        return clients;
    }
}
