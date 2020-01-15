package etf.dotsandboxes.nj160040d.logic;

import java.util.Collections;
import java.util.List;

public class CompetitiveSolver extends AlphaBetaSolver {

    public CompetitiveSolver(AIPlayer player) { super(player); }

    @Override
    public Edge getNextMove() {
        if (player.game.getState().getNumberOfAvailableMoves() == 0) return Edge.INVALID;
        List<Edge> moves = player.game.getState().getAvailableMoves();
        Collections.shuffle(moves);
        for (Edge move : moves) {
            if (!player.game.getState().addsNthEdge(move, 4) &&
                    !player.game.getState().addsNthEdge(move, 3) &&
                    !player.game.getState().addsNthEdge(move, 2)) return move;
        }
        return search(player.game.getState(), moves, -player.game.getState().getMaxScore(), player.game.getState().getMaxScore(), 0, true).getMove();
    }
}