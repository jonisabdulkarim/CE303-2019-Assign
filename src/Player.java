import java.util.HashMap;
import java.util.Map;

public class Player implements PlayerLogic{

    private final int id;
    private Map<InfluenceCard, Boolean> cards;
    private boolean blocked;


    Player(int id){
        this.id = id;
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
        return false;
    }

    public void removeCard(InfluenceCard card){
        cards.replace(card, false);
    }

    public boolean hasCard(InfluenceCard card){
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
