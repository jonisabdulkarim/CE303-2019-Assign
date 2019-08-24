import java.util.HashMap;
import java.util.Map;

public class Bot implements PlayerLogic {
    private int id;
    private GameState game;
    private Map<InfluenceCard, Boolean> cards;
    private boolean blocked;

    Bot(GameState game, int id){
        this.id = id;
        this.game = game;
        cards = new HashMap<>();
        makeCards(cards);
        blocked = false;
    }

    @Override
    public int getMyPlayerId() {
        return id;
    }

    @Override
    public boolean makeMove(){
        for (int x = 0; x < 6; x++) {
            for(int y = 0; y < 10; y++) {
                if (cards.get(InfluenceCard.DOUBLE)) {

                    Move move = new Move(InfluenceCard.DOUBLE,
                            new Coordinates(x, y));
                    if (game.isMoveAllowed(move, id)) {
                        game.setBoard(move, id);
                        GameServer.messageAll("Player " + id + " has used double card");
                        GameServer.messageAll("Coordinates: x: " + (x+1) + " y: " + (y+1) );
                        return true;
                    }
                }
                else{
                    for (InfluenceCard card : cards.keySet()) {
                        if (cards.get(card)) {
                            Move move = new Move(card,
                                    new Coordinates(x, y));
                            if (game.isMoveAllowed(move, id)) {
                                game.setBoard(move, id);
                                if(card == InfluenceCard.NONE)
                                    GameServer.messageAll("Player " + id + " has completed normal move");
                                else
                                    GameServer.messageAll("Player " + id + " has used " + card.name() + " card");
                                GameServer.messageAll("Coordinates: x: " + (x+1) + " y: " + (y+1) );
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void removeCard(InfluenceCard card) {
        cards.replace(card, false);
    }

    @Override
    public boolean hasCard(InfluenceCard card) {
        return cards.get(card);
    }

    @Override
    public void setBlocked() {
        blocked = true;
    }

    @Override
    public boolean playerIsNotBlocked() {
        return !blocked;
    }
}
