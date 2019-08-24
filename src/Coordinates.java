// This class represents coordinates on the board
final class Coordinates {
    private final int x;
    private final int y;

    Coordinates(int x, int y)
    {
        assert y >= 0 && y < GameState.ROWS;
        assert x >= 0 && x < GameState.COLUMNS;

        this.x = x;
        this.y = y;
    }

    int getX() { return x; }
    int getY() { return y; }
}
