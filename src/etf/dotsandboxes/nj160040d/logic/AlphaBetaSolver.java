package etf.dotsandboxes.nj160040d.logic;

import etf.dotsandboxes.nj160040d.Game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AlphaBetaSolver implements Solver {

    private Game game;
    private AIPlayer player;

    public AlphaBetaSolver(AIPlayer player) {
        this.player = player;
        this.game = player.game;
    }

    @Override
    public Edge getNextMove() {
        if (game.getState().getNumberOfAvailableMoves() == 0) return new Edge();
        List<Edge> availableMoves = game.getState().getAvailableMoves();
        List<Edge> considerableMoves = new ArrayList<>(availableMoves.size());
        for (Edge move : availableMoves) {
            if (game.getState().closesBox(move)) return move;
            if (!game.getState().addsThirdEdge(move)) considerableMoves.add(move);
        }
        if (considerableMoves.size() == 0) return availableMoves.get(0);
        Collections.shuffle(considerableMoves);
        return dfs(game.getState(), considerableMoves, player, 0, -game.getState().getMaxScore(), game.getState().getMaxScore()).getMove();
    }

    protected static int calculateHeuristic(State state) {
        if (state.getCurrentPlayer() == state.getPlayer1()) return state.getPlayer1Score() - state.getPlayer2Score();
        else return state.getPlayer2Score() - state.getPlayer1Score();
    }

    protected static Pair dfs(State state, List<Edge> availableMoves, AIPlayer player, int depth, int a, int b) {
        if (depth >= player.getMaxDepth() || availableMoves.size() == 0) return new Pair(null, calculateHeuristic(state));

        boolean isReferencePlayer = state.getCurrentPlayer() == player;
        Pair pair = new Pair(null, isReferencePlayer ? -state.getMaxScore() : state.getMaxScore());

        for (int i = 0; i < availableMoves.size(); ++i) {
            State nextState = state.getNextBoardState(availableMoves.get(i));
            List<Edge> nextAvailableMoves = new ArrayList<>(availableMoves.size() - 1);
            for (int j = 0; j < availableMoves.size() - 1; ++j)
                nextAvailableMoves.add(availableMoves.get(j < i ? j : j + 1));
            int currentScore = state.getCurrentPlayerScore(), nextScore = nextState.getCurrentPlayerScore();
            Pair nextPair = dfs(nextState, nextAvailableMoves, player, depth + 1, a, b);
            if (isReferencePlayer && pair.getHeuristic() < nextPair.getHeuristic() ||
                !isReferencePlayer && pair.getHeuristic() > nextPair.getHeuristic()) {
                pair.setHeuristic(nextPair.getHeuristic());
                pair.setMove(availableMoves.get(i));
            }
            if (currentScore == nextScore && (
                    isReferencePlayer && nextPair.getHeuristic() >= b ||
                    !isReferencePlayer && nextPair.getHeuristic() <= a)) return pair;
            if (isReferencePlayer) {
                a = Math.max(a, nextPair.getHeuristic());
            } else {
                b = Math.min(b, nextPair.getHeuristic());
            }
        }

        return pair;
    }
}