package etf.dotsandboxes.nj160040d.logic;

import etf.dotsandboxes.nj160040d.Game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AlphaBetaSolver implements Solver {

    protected static final int HEURISTIC_SCORE_MULTI = 20;
    protected static final int HEURISTIC_THREE_EDGES_MULTI = 15;
    protected static final int HEURISTIC_TWO_EDGES_MULTI = 1;

    protected AIPlayer player;

    public AlphaBetaSolver(AIPlayer player) {
        this.player = player;
    }

    @Override
    public Edge getNextMove() {
        if (player.game.getState().getAvailableMovesCount() == 0) return Edge.INVALID;
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
        return search(player.game.getState(), viableMoves, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, false).getMove();
    }

    protected int getHeuristic(State state) {
        int scoreHeuristic = HEURISTIC_SCORE_MULTI * (this.player == state.getPlayer1() ? 1 : -1) *
                (state.getPlayer1Score() - state.getPlayer2Score());
        int edgeHeuristic = HEURISTIC_THREE_EDGES_MULTI * state.getBoxCount(3) -
                HEURISTIC_TWO_EDGES_MULTI * state.getBoxCount(2);
        if (state.getCurrentPlayer() == player) return scoreHeuristic + edgeHeuristic;
        return scoreHeuristic - edgeHeuristic;
    }

    protected Node search(State state, List<Edge> moves, int alpha, int beta, int depth, boolean turnByTurn) {
        if (moves.size() == 0) return new Node(Edge.INVALID, getHeuristic(state));
        if (depth == player.getMaxDepth()) return new Node(moves.get((int) Math.floor(Math.random() * moves.size())), getHeuristic(state));

        boolean isReferencePlayer = state.getCurrentPlayer() == player;
        boolean cvcMode = player.game.getMode() == Game.Mode.CvC_STEP;

//        if (depth == 0) System.out.println("Reference player: " + player.getName());
//        System.out.println("Depth: " + depth + " (" + (isReferencePlayer ? "MAX" : "MIN") + ") Move: " + state.getLastMove() + " Alpha: " + alpha + " Beta: " + beta);

        if (depth == 0 && cvcMode) {
            if (player.game.getHeuristics() == null) player.game.setHeuristics(new ArrayList<>());
            else player.game.getHeuristics().clear();
        }

        Node resultNode = new Node(Edge.INVALID, isReferencePlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE);

        if (depth + 1 == player.getMaxDepth()) {
            for (int i = 0; i < moves.size(); ++i) {
//                System.out.print("Result: move: " + resultNode.getMove() + " heuristic: " + resultNode.getHeuristic());
                State nextState = state.getNextBoardState(moves.get(i));
                // TODO: Remove this int and change State.closeAllAvailableBoxes method to return void
                int closedBoxesNum = -1;
                if (turnByTurn) closedBoxesNum = nextState.closeAllAvailableBoxes();
                Node nextNode = new Node(moves.get(i), getHeuristic(nextState));
//                // TODO: Remove debug info
//                System.out.print(" Next move " + (i + 1) + ": " + moves.get(i));
//                if (turnByTurn) System.out.print(" closed boxes: " + closedBoxesNum);
//                System.out.println(" heuristic: " + nextNode.getHeuristic());
                if (depth == 0 && cvcMode) player.game.getHeuristics().add(nextNode);
                if (isReferencePlayer && resultNode.getHeuristic() < nextNode.getHeuristic() ||
                        !isReferencePlayer && resultNode.getHeuristic() > nextNode.getHeuristic()) {
                    resultNode.setMove(moves.get(i));
                    resultNode.setHeuristic(nextNode.getHeuristic());
                }
                if (state.getCurrentPlayerScore() == nextState.getCurrentPlayerScore() && (
                        isReferencePlayer && nextNode.getHeuristic() >= beta ||
                                !isReferencePlayer && nextNode.getHeuristic() <= alpha)) return resultNode;
                if (isReferencePlayer) alpha = Math.max(alpha, resultNode.getHeuristic());
                else beta = Math.max(beta, resultNode.getHeuristic());
            }
            return resultNode;
        }

        Node[] nextNodes = new Node[moves.size()];

        for (int i = 0; i < nextNodes.length; ++i) {
            State nextState = state.getNextBoardState(moves.get(i));
            // TODO: Remove this int and change State.closeAllAvailableBoxes method to return void
            int closedBoxesNum = -1;
            if (turnByTurn) closedBoxesNum = nextState.closeAllAvailableBoxes();
            nextNodes[i] = new Node(moves.get(i), nextState, getHeuristic(nextState));
            if (depth == 0 && cvcMode) player.game.getHeuristics().add(nextNodes[i]);
//            // TODO: Remove debug info
//            System.out.print("Next move " + (i + 1) + ": " + moves.get(i));
//            if (closedBoxesNum != -1) System.out.print(" closed boxes: " + closedBoxesNum);
//            System.out.println(" heuristic: " + nextNodes[i].getHeuristic());
        }
        Arrays.sort(nextNodes);

        for (int i = 0; i < nextNodes.length; ++i) {
            List<Edge> nextMoves = new ArrayList<>(nextNodes.length - 1);
            for (int j = 0; j < nextNodes.length - 1; ++j) nextMoves.add(nextNodes[(j < i ? j : j + 1)].getMove());
            Node nextNode = search(nextNodes[i].getState(), nextMoves, alpha, beta, depth + 1, turnByTurn);
            if (depth == 0 && cvcMode) nextNodes[i].setHeuristic(nextNode.getHeuristic());
            if (isReferencePlayer && resultNode.getHeuristic() < nextNode.getHeuristic() ||
                    !isReferencePlayer && resultNode.getHeuristic() > nextNode.getHeuristic()) {
                resultNode.setMove(nextNodes[i].getMove());
                resultNode.setHeuristic(nextNode.getHeuristic());
            }
            if (state.getCurrentPlayerScore() == nextNodes[i].getState().getCurrentPlayerScore() && (
                    isReferencePlayer && nextNode.getHeuristic() >= beta ||
                            !isReferencePlayer && nextNode.getHeuristic() <= alpha)) return resultNode;
            if (isReferencePlayer) alpha = Math.max(alpha, resultNode.getHeuristic());
            else beta = Math.max(beta, resultNode.getHeuristic());
        }

        return resultNode;
    }
}