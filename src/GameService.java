import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class GameService implements Runnable{
    private final GameState game;
    private Scanner in;
    private PrintWriter out;
    private int ID;
    boolean enter;
    private boolean current;


    GameService(GameState game, Socket socket, int ID) {
        this.game = game;
        this.ID = ID;
        current = false;
        enter = false;
        try{
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void run(){
        enter();

        while(enter){
            while(current){
                try{
                    Request request = Request.parse(in.nextLine());
                    String response = execute(game, request);
                    out.println(response + "\r\n");
                    Thread.sleep(2000);
                } catch (NoSuchElementException | InterruptedException e){
                    enter = false;
                    current = false;
                }
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }

        exit();
    }

    private void enter(){
        if(GameServer.isPrimary()){
            out.println("Designated primary client.");
            out.println("Enter number of human players (including yourself): ");
            GameServer.setClients(Integer.parseInt(in.nextLine()));
            out.println("Enter number of bots: ");
            GameServer.setBots(Integer.parseInt(in.nextLine()));
        }
        enter = true;
        if(GameServer.getClients() != 0)
            game.newPlayer(ID);
    }

    private void exit(){

    }

    private String execute(GameState game, Request request){
        try{
            switch(request.type){
                case MOVE:
                    InfluenceCard card = getInfluenceCard(request.params[0].toUpperCase());
                    if (!game.players.get(ID - 1).hasCard(card)){
                        return "Card has already been used up";
                    }
                    int y = Integer.parseInt(request.params[1]) - 1;
                    int x = Integer.parseInt(request.params[2]) - 1;
                    Move move = new Move(card, new Coordinates(x, y));

                    if(game.isMoveAllowed(move, ID)){
                        game.setBoard(move, ID);
                        if(card != InfluenceCard.DOUBLE)
                            switchTurn();
                        if(card != InfluenceCard.NONE)
                            game.players.get(ID - 1).removeCard(card);
                        if(card == InfluenceCard.NONE) {
                            GameServer.messageAll("Player " + ID + " has completed normal move");
                            GameServer.messageAll("Coordinates: x: " + (x+1) + " y: " + (y+1) );
                            return "Normal move accepted. Next player's turn.";
                        }
                        else if(card == InfluenceCard.DOUBLE) {
                            GameServer.printBoard(game);
                            GameServer.messageAll("Player " + ID + " has used DOUBLE card");
                            GameServer.messageAll("Coordinates: x: " + (x+1) + " y: " + (y+1) );
                            return "Double card accepted. Apply second move.";
                        }
                        else if(card == InfluenceCard.FREEDOM) {
                            GameServer.messageAll("Player " + ID + " has used FREEDOM card");
                            GameServer.messageAll("Coordinates: x: " + (x+1) + " y: " + (y+1) );
                            return "Freedom card accepted. Next player's turn.";
                        }
                        else if(card == InfluenceCard.REPLACEMENT){
                            GameServer.messageAll("Player " + ID + " has used REPLACEMENT card");
                            GameServer.messageAll("Coordinates: x: " + (x+1) + " y: " + (y+1) );
                            return "Replacement card accepted. Next player's turn.";
                        }
                    } else {
                        if(card == InfluenceCard.NONE)
                            return "Invalid move. Cell must be empty and adjacent" +
                                    " to your stone(s).";
                        if(card == InfluenceCard.DOUBLE)
                            return "Invalid double move. Cell must be empty and adjacent" +
                                    " to your stone(s).";
                        if(card == InfluenceCard.FREEDOM)
                            return "Invalid freedom move. Cell must be empty.";
                        if(card == InfluenceCard.REPLACEMENT)
                            return "Invalid replacement move. " +
                                    "Cell must be adjacent to your stone(s).";
                    }
                case INVALID:
                    return "Invalid command entered. Try again.";
                case EXIT:
                    System.exit(0);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    private InfluenceCard getInfluenceCard(String param) {
        InfluenceCard card = InfluenceCard.NONE;
        for(InfluenceCard cardType : InfluenceCard.values()){
            if(cardType.name().equals(param))
                card = cardType;
        }
        return card;
    }

    void message(String s){
        out.println(s);
    }

    void switchTurn(){
        current = !current;
    }

    boolean isCurrent(){
        return current;
    }
}
