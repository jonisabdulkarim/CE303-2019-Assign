// This class represents a single move of a player.
final class Move
{
    private final InfluenceCard card;
    private final Coordinates move;

    Move(InfluenceCard card, Coordinates move) {
        assert move != null;

        this.card = card;
        this.move = move;
    }

    Coordinates getMove() { return move; }
    InfluenceCard getCard() { return card; }
}
