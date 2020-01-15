package etf.dotsandboxes.nj160040d.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CompetitiveSolver implements Solver {

    private AIPlayer player;

    public CompetitiveSolver(AIPlayer player) {
        this.player = player;
    }

    @Override
    public Edge getNextMove() {
        if (player.game.getState().getNumberOfAvailableMoves() == 0) return Edge.INVALID;
        List<Edge> moves = player.game.getState().getAvailableMoves();
        List<Edge> viableMoves = new ArrayList<>();
        Collections.shuffle(moves);
        for (Edge move : moves) {
            if (!player.game.getState().addsNthEdge(move, 4) && !player.game.getState().addsNthEdge(move, 3)) {
                if (!player.game.getState().addsNthEdge(move, 2)) return move;
                viableMoves.add(move);
            }
        }
        if (viableMoves.isEmpty()) viableMoves = moves;
        return search(player.game.getState(), viableMoves, -player.game.getState().getMaxScore(), player.game.getState().getMaxScore(), 0).getMove();
    }

    private Pair search(State state, List<Edge> moves, int alpha, int beta, int depth) {
        if (moves.size() == 0) return new Pair(Edge.INVALID, state.getPlayerHeuristic(player));
        if (depth == player.getMaxDepth()) return new Pair(moves.get((int) Math.floor(Math.random() * moves.size())), state.getPlayerHeuristic(player));

        System.out.println("Depth: " + depth + " alpha: " + alpha + " beta: " + beta);

        Pair[] nextMovePairs = new Pair[moves.size()];
        for (int i = 0; i < nextMovePairs.length; ++i) {
            State nextState = state.getNextBoardState(moves.get(i));
            int closedBoxesNum = nextState.closeAllAvailableBoxes(); // TODO: Remove for competitive player
            nextMovePairs[i] = new Pair(moves.get(i), nextState.getPlayerHeuristic(player));
            System.out.println("Neighbour " + (i + 1) + ": " + (moves.get(i).isHorizontal() ? "H" : "V") + moves.get(i).getX() + ", " + moves.get(i).getY() + " closed boxes: " + closedBoxesNum + " heuristic: " + nextMovePairs[i].getHeuristic());
        }
        Arrays.sort(nextMovePairs);

        boolean isReferencePlayer = state.getCurrentPlayer() == player;
        Pair resultPair = new Pair(Edge.INVALID, isReferencePlayer ? -state.getMaxScore() : state.getMaxScore());

        for (int i = 0; i < nextMovePairs.length; ++i) {
            State nextState = state.getNextBoardState(nextMovePairs[i].getMove());
            nextState.closeAllAvailableBoxes(); // TODO: Remove for competitive player
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
}