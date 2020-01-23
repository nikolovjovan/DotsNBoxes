package etf.dotsandboxes.nj160040d.logic;

import etf.dotsandboxes.nj160040d.util.UnsafeLinkedList;

import java.util.Collections;

public class CompetitiveSolver extends AlphaBetaSolver {

    protected static final int HEURISTIC_SCORE_MULTI = 30;
    protected static final int HEURISTIC_TWO_EDGES_MULTI = 5;

    public CompetitiveSolver(AIPlayer player) { super(player); }

    @Override
    protected int getHeuristic(State state) {
        int scoreHeuristic = HEURISTIC_SCORE_MULTI * (this.player == state.getPlayer1() ? 1 : -1) *
                (state.getPlayer1Score() - state.getPlayer2Score());
        int edgeHeuristic = -HEURISTIC_TWO_EDGES_MULTI * state.getBoxCount(2);
        if (player == state.getCurrentPlayer()) return scoreHeuristic + edgeHeuristic;
        return scoreHeuristic - edgeHeuristic;
    }

    @Override
    public Edge getNextMove() {
        UnsafeLinkedList<Edge> availableMoves = new UnsafeLinkedList<>(player.game.getState().getAvailableMoves());
        if (availableMoves.isEmpty()) return Edge.INVALID;
        Collections.shuffle(availableMoves);
        for (Edge move : availableMoves) {
            if (!player.game.getState().addsNthEdge(move, 4) &&
                    !player.game.getState().addsNthEdge(move, 3) &&
                    !player.game.getState().addsNthEdge(move, 2)) return move;
        }
        return getBestMove(availableMoves, true);
    }
}