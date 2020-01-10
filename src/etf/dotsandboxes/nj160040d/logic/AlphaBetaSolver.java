package etf.dotsandboxes.nj160040d.logic;

import etf.dotsandboxes.nj160040d.Game;

import java.util.ArrayList;
import java.util.Arrays;
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
        List<Edge> addsFirstEdge = new ArrayList<>();
        List<Edge> addsSecondEdge = new ArrayList<>();
        for (Edge move : availableMoves) {
            if (game.getState().addsNthEdge(move, 4)) return move;
            if (!game.getState().addsNthEdge(move, 3)) {
                if (game.getState().addsNthEdge(move, 2)) addsSecondEdge.add(move);
                else addsFirstEdge.add(move);
            }
        }
        if (addsFirstEdge.size() > 0) return addsFirstEdge.get((int) Math.floor(Math.random() * addsFirstEdge.size()));
        if (addsSecondEdge.size() > 0) {
            Collections.shuffle(addsSecondEdge);
            return dfs(game.getState(), addsSecondEdge, player, 0, -game.getState().getMaxScore(), game.getState().getMaxScore()).getMove();
        }
        Collections.shuffle(availableMoves);
        return dfs(game.getState(), availableMoves, player, 0, -game.getState().getMaxScore(), game.getState().getMaxScore()).getMove();
    }

    protected static int calculateHeuristic(State state) {
        if (state.getCurrentPlayer() == state.getPlayer1()) return state.getPlayer1Score() - state.getPlayer2Score();
        else return state.getPlayer2Score() - state.getPlayer1Score();
    }

    protected static Pair dfs(State state, List<Edge> availableMoves, AIPlayer player, int depth, int a, int b) {
        if (availableMoves.size() == 0) return new Pair(null, calculateHeuristic(state));
        if (depth == player.getMaxDepth()) return new Pair(availableMoves.get((int) Math.floor(Math.random() * availableMoves.size())), calculateHeuristic(state));

        State[] nextStates = new State[availableMoves.size()];
        Pair[] nextStatePairs = new Pair[availableMoves.size()];
        for (int i = 0; i < availableMoves.size(); ++i) {
            nextStates[i] = state.getNextBoardState(availableMoves.get(i));
            nextStatePairs[i] = new Pair(availableMoves.get(i), calculateHeuristic(nextStates[i]));
        }
        Arrays.sort(nextStatePairs);

        boolean isReferencePlayer = state.getCurrentPlayer() == player;
        Pair pair = new Pair(null, isReferencePlayer ? -state.getMaxScore() : state.getMaxScore());

        for (int i = 0; i < nextStatePairs.length; ++i) {
            State nextState = state.getNextBoardState(nextStatePairs[i].getMove());
            List<Edge> nextAvailableMoves = new ArrayList<>(nextStatePairs.length - 1);
            for (int j = 0; j < nextStatePairs.length - 1; ++j)
                nextAvailableMoves.add(nextStatePairs[(j < i ? j : j + 1)].getMove());
            int currentScore = state.getCurrentPlayerScore(), nextScore = nextState.getCurrentPlayerScore();
            Pair nextPair = dfs(nextState, nextAvailableMoves, player, depth + 1, a, b);
            if (isReferencePlayer && pair.getHeuristic() < nextPair.getHeuristic() ||
                !isReferencePlayer && pair.getHeuristic() > nextPair.getHeuristic()) {
                pair.setHeuristic(nextPair.getHeuristic());
                pair.setMove(nextStatePairs[i].getMove());
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