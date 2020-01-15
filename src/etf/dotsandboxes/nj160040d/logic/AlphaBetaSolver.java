package etf.dotsandboxes.nj160040d.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AlphaBetaSolver implements Solver {

    private AIPlayer player;

    public AlphaBetaSolver(AIPlayer player) {
        this.player = player;
    }

    @Override
    public Edge getNextMove() {
        if (player.game.getState().getNumberOfAvailableMoves() == 0) return Edge.INVALID;
        List<Edge> moves = player.game.getState().getAvailableMoves();
        List<Edge> viableMoves = new ArrayList<>();
        Collections.shuffle(moves);
        for (Edge move : moves) {
            if (player.game.getState().addsNthEdge(move, 4)) return move;
            if (!player.game.getState().addsNthEdge(move, 3)) {
                if (!player.game.getState().addsNthEdge(move, 2)) return move;
                viableMoves.add(move);
            }
        }
        if (viableMoves.isEmpty()) return moves.get((int) Math.floor(Math.random() * moves.size()));
        return search(player.game.getState(), viableMoves, -player.game.getState().getMaxScore(), player.game.getState().getMaxScore(), 0).getMove();
    }

    private Pair search(State state, List<Edge> moves, int alpha, int beta, int depth) {
        if (moves.size() == 0) return new Pair(Edge.INVALID, state.getPlayerHeuristic(player));
        if (depth == player.getMaxDepth()) return new Pair(moves.get((int) Math.floor(Math.random() * moves.size())), state.getPlayerHeuristic(player));

        System.out.println("Depth: " + depth + " alpha: " + alpha + " beta: " + beta);

        Pair[] nextMovePairs = new Pair[moves.size()];
        for (int i = 0; i < nextMovePairs.length; ++i) {
            State nextState = state.getNextBoardState(moves.get(i));
            nextMovePairs[i] = new Pair(moves.get(i), nextState.getPlayerHeuristic(player));
            System.out.println("Neighbour " + (i + 1) + ": " + (moves.get(i).isHorizontal() ? "H" : "V") + moves.get(i).getX() + ", " + moves.get(i).getY() + " heuristic: " + nextMovePairs[i].getHeuristic());
        }
        Arrays.sort(nextMovePairs);

        boolean isReferencePlayer = state.getCurrentPlayer() == player;
        Pair resultPair = new Pair(Edge.INVALID, isReferencePlayer ? -state.getMaxScore() : state.getMaxScore());

        for (int i = 0; i < nextMovePairs.length; ++i) {
            State nextState = state.getNextBoardState(nextMovePairs[i].getMove());
            List<Edge> nextMoves = new ArrayList<>(nextMovePairs.length - 1);
            for (int j = 0; j < nextMovePairs.length - 1; ++j) nextMoves.add(nextMovePairs[(j < i ? j : j + 1)].getMove());
            Pair nextPair = search(nextState, nextMoves, alpha, beta, depth + 1);
            if (isReferencePlayer && resultPair.getHeuristic() < nextPair.getHeuristic() ||
                    !isReferencePlayer && resultPair.getHeuristic() > nextPair.getHeuristic()) {
                resultPair.setMove(nextMovePairs[i].getMove());
                resultPair.setHeuristic(nextPair.getHeuristic());
            }
            if (state.getCurrentPlayerScore() == nextState.getCurrentPlayerScore() && (
                    isReferencePlayer && nextPair.getHeuristic() >= beta ||
                            !isReferencePlayer && nextPair.getHeuristic() <= alpha)) return resultPair;
            if (isReferencePlayer) alpha = Math.max(alpha, resultPair.getHeuristic());
            else beta = Math.max(beta, resultPair.getHeuristic());
        }

        return resultPair;
    }
/*
    @Override
    public Edge getNextMove() {
        if (player.game.getState().getNumberOfAvailableMoves() == 0) return Edge.INVALID;
        List<Edge> availableMoves = player.game.getState().getAvailableMoves();
        List<Edge> addsFirstEdge = new ArrayList<>();
        List<Edge> addsSecondEdge = new ArrayList<>();
        for (Edge move : availableMoves) {
            if (player.game.getState().addsNthEdge(move, 4)) return move;
            if (!player.game.getState().addsNthEdge(move, 3)) {
                if (player.game.getState().addsNthEdge(move, 2)) addsSecondEdge.add(move);
                else addsFirstEdge.add(move);
            }
        }
        if (addsFirstEdge.size() > 0) return addsFirstEdge.get((int) Math.floor(Math.random() * addsFirstEdge.size()));
        if (addsSecondEdge.size() > 0) {
            Collections.shuffle(addsSecondEdge);
            return dfs(player.game.getState(), addsSecondEdge, player, 0, -player.game.getState().getMaxScore(), player.game.getState().getMaxScore()).getMove();
        }
        Collections.shuffle(availableMoves);
        return dfs(player.game.getState(), availableMoves, player, 0, -player.game.getState().getMaxScore(), player.game.getState().getMaxScore()).getMove();
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
    }*/
}