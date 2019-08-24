import java.util.Map;

// This interface represents a player's logic.
// There will be two implementations of this interface: a UI (letting the user play the game) and a bot.
public interface PlayerLogic
{
    // Returns ID of player, in order of connection
    int getMyPlayerId();

    // Default method to place all cards on deck
    default void makeCards(Map<InfluenceCard, Boolean> cards){
        cards.put(InfluenceCard.NONE, true);
        cards.put(InfluenceCard.DOUBLE, true);
        cards.put(InfluenceCard.FREEDOM, true);
        cards.put(InfluenceCard.REPLACEMENT, true);
    }

    // Method to remove card
    void removeCard(InfluenceCard card);

    // Method to check if card is present
    boolean hasCard(InfluenceCard card);

    // Method used by bot to make moves in game
    boolean makeMove();

    // Method used to designate blocked players
    void setBlocked();

    // Method to check if player is still able to play
    boolean playerIsNotBlocked();
}
