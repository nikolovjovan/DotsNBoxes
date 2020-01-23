package etf.dotsandboxes.nj160040d.logic;

import etf.dotsandboxes.nj160040d.Game;
import etf.dotsandboxes.nj160040d.util.UnsafeLinkedList;

import java.util.*;

public class AlphaBetaSolver implements Solver {

    protected static final int HEURISTIC_SCORE_MULTI = 20;
    protected static final int HEURISTIC_THREE_EDGES_MULTI = 15;
    protected static final int HEURISTIC_TWO_EDGES_MULTI = 1;

    protected AIPlayer player;

    // TODO: Remove this
    protected boolean newImpl;

    public AlphaBetaSolver(AIPlayer player) {
        this.player = player;
        // TODO: Remove newImpl and this logic...
        if (this.player.getMaxDepth() % 2 == 0) {
            this.player.setMaxDepth(this.player.getMaxDepth() / 2);
            this.newImpl = true;
        } else {
            this.player.setMaxDepth(this.player.getMaxDepth() / 2 + 1);
            this.newImpl = false;
        }
        System.out.println(this.player.getName() + " maxDepth: " + this.player.getMaxDepth() + " newImpl: " + this.newImpl);
    }

    @Override
    public Edge getNextMove() {
        UnsafeLinkedList<Edge> availableMoves = new UnsafeLinkedList<>(player.game.getState().getAvailableMoves());
        if (availableMoves.isEmpty()) return Edge.INVALID;
        UnsafeLinkedList<Edge> viableMoves = new UnsafeLinkedList<>();
        Collections.shuffle(availableMoves);
        for (Edge move : availableMoves) {
            if (player.game.getState().addsNthEdge(move, 4)) return move;
            if (!player.game.getState().addsNthEdge(move, 3)) {
                if (!player.game.getState().addsNthEdge(move, 2)) return move;
                viableMoves.add(move);
            }
        }
        if (viableMoves.isEmpty()) return availableMoves.get((int) Math.floor(Math.random() * availableMoves.size()));
        return getBestMove(viableMoves, false);
    }

    protected int getHeuristic(State state) {
        int scoreHeuristic = HEURISTIC_SCORE_MULTI * (this.player == state.getPlayer1() ? 1 : -1) *
                (state.getPlayer1Score() - state.getPlayer2Score());
        int edgeHeuristic = HEURISTIC_THREE_EDGES_MULTI * state.getBoxCount(3) -
                HEURISTIC_TWO_EDGES_MULTI * state.getBoxCount(2);
        if (state.getCurrentPlayer() == player) return scoreHeuristic + edgeHeuristic;
        return scoreHeuristic - edgeHeuristic;
    }

    protected Node search(UnsafeLinkedList<Edge> moves, int alpha, int beta, int depth, boolean turnByTurn) {
        State state = player.game.getState();
        boolean maxNode = state.getCurrentPlayer() == player;
        boolean cvcMode = player.game.getMode() == Game.Mode.CvC_STEP;

        if (moves.isEmpty()) return new Node(Edge.INVALID, getHeuristic(state));
        if (depth == player.getMaxDepth()) return new Node(moves.get((int) Math.floor(Math.random() * moves.size())), getHeuristic(state));
//
//        if (depth == 0) System.out.println("Reference player: " + player.getName());
//        System.out.println(" >>>>> Entry previous move: " + (state.getPreviousMoves().empty() ? Edge.INVALID : state.getPreviousMoves().peek()) + " Depth: " + depth + " (" + (maxNode ? "MAX" : "MIN") + ") Alpha: " + alpha + " Beta: " + beta);

        if (depth == 0 && cvcMode) {
            if (player.getHeuristics() == null) player.setHeuristics(new ArrayList<>());
            else player.getHeuristics().clear();
        }

        Node resultNode = new Node(Edge.INVALID, maxNode ? Integer.MIN_VALUE : Integer.MAX_VALUE);

        ListIterator<Edge> movesIt = moves.listIterator();

        Node[] nextNodes = new Node[moves.size()];
        for (int i = 0; i < nextNodes.length; ++i) {
            Edge move = movesIt.next();

            // TODO: Remove this test
            State temp = state.getClone();

            if (!state.nextMove(move, turnByTurn))
                System.err.println("Failed to make next move: " + move + "!");
            nextNodes[i] = new Node(move, getHeuristic(state));
            if (!state.undoMove(move, turnByTurn))
                System.err.println("Failed to make next move: " + move + "!");

            if (!temp.equals(state)) {
                System.out.println("Invalid undo for move: " + move + "!");
            }

            if (depth == 0 && cvcMode) player.getHeuristics().add(nextNodes[i]);
        }

//        System.out.print("Unsorted nextNodes: ");
//        for (int i = 0; i < nextNodes.length; ++i) System.out.print(nextNodes[i].getHeuristic() + " ");
//        System.out.println();

        if (maxNode) Arrays.sort(nextNodes);
        else Arrays.sort(nextNodes, Collections.reverseOrder());

//        System.out.print("Sorted nextNodes: ");
//        for (int i = 0; i < nextNodes.length; ++i) System.out.print(nextNodes[i].getHeuristic() + " ");
//        System.out.println();

        moves.clear();
        for (int i = 0; i < nextNodes.length; ++i) moves.addLast(nextNodes[i].getMove());

        movesIt = moves.listIterator();
        for (int i = 0; i < nextNodes.length; ++i) {
            Edge move = movesIt.next();

            int currentScore = maxNode ? state.getPlayerScore(player) : state.getOpponentScore(player);

//            System.out.print("Before removing: ");
//            for (Edge e : moves) System.out.print(e + " ");
//            System.out.println();

            // TODO: Remove this test
            State temp = state.getClone();

            if (!state.nextMove(move, turnByTurn))
                System.err.println("Failed to make next move: " + move + "!");
            movesIt.remove();

//            System.out.print("After removing: ");
//            for (Edge e : moves) System.out.print(e + " ");
//            System.out.println();

            Node nextNode = search(moves, alpha, beta, depth + 1, turnByTurn);
            int nextScore = maxNode ? state.getPlayerScore(player) : state.getOpponentScore(player);

//            System.out.println(" <<<<< Return previous move: " + (state.getPreviousMoves().empty() ? Edge.INVALID : state.getPreviousMoves().peek()) + " Depth: " + depth + " (" + (maxNode ? "MAX" : "MIN") + ")");

            if (!state.undoMove(move, turnByTurn))
                System.err.println("Failed to make next move: " + move + "!");
            movesIt.add(move);

            if (!temp.equals(state)) {
                System.out.println("Invalid undo for move: " + move + "!");
            }

//            System.out.print("After adding back: ");
//            for (Edge e : moves) System.out.print(e + " ");
//            System.out.println();

            if (depth == 0 && cvcMode) nextNodes[i].setHeuristic(nextNode.getHeuristic());

            if (maxNode && resultNode.getHeuristic() < nextNode.getHeuristic() ||
                    !maxNode && resultNode.getHeuristic() > nextNode.getHeuristic()) {
                resultNode.setMove(nextNodes[i].getMove());
                resultNode.setHeuristic(nextNode.getHeuristic());
            }

            if (currentScore == nextScore && (maxNode && nextNode.getHeuristic() >= beta ||
                    !maxNode && nextNode.getHeuristic() <= alpha)) return resultNode;

            if (maxNode) alpha = Math.max(alpha, resultNode.getHeuristic());
            else beta = Math.min(beta, resultNode.getHeuristic());
        }

        return resultNode;
    }

    protected Node search(State state, List<Edge> moves, int alpha, int beta, int depth, boolean turnByTurn) {
        boolean maxNode = state.getCurrentPlayer() == player;
        boolean cvcMode = player.game.getMode() == Game.Mode.CvC_STEP;

        if (moves.isEmpty()) return new Node(Edge.INVALID, getHeuristic(state));
        if (depth == player.getMaxDepth()) return new Node(moves.get((int) Math.floor(Math.random() * moves.size())), getHeuristic(state));

//        if (depth == 0) System.out.println("Reference player: " + player.getName());
//        System.out.println("Depth: " + depth + " (" + (maxNode ? "MAX" : "MIN") + ") Previous move: " + (state.getPreviousMoves().empty() ? Edge.INVALID : state.getPreviousMoves().peek()) + " Alpha: " + alpha + " Beta: " + beta);

        if (depth == 0 && cvcMode) {
            if (player.getHeuristics() == null) player.setHeuristics(new ArrayList<>());
            else player.getHeuristics().clear();
        }

        Node resultNode = new Node(Edge.INVALID, maxNode ? Integer.MIN_VALUE : Integer.MAX_VALUE);

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
                if (depth == 0 && cvcMode) player.getHeuristics().add(nextNode);
                if (maxNode && resultNode.getHeuristic() < nextNode.getHeuristic() ||
                        !maxNode && resultNode.getHeuristic() > nextNode.getHeuristic()) {
                    resultNode.setMove(moves.get(i));
                    resultNode.setHeuristic(nextNode.getHeuristic());
                }
                if (state.getCurrentPlayerScore() == nextState.getCurrentPlayerScore() && (
                        maxNode && nextNode.getHeuristic() >= beta ||
                                !maxNode && nextNode.getHeuristic() <= alpha)) return resultNode;
                if (maxNode) alpha = Math.max(alpha, resultNode.getHeuristic());
                else beta = Math.min(beta, resultNode.getHeuristic());
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
            if (depth == 0 && cvcMode) player.getHeuristics().add(nextNodes[i]);
//            // TODO: Remove debug info
//            System.out.print("Next move " + (i + 1) + ": " + moves.get(i));
//            if (closedBoxesNum != -1) System.out.print(" closed boxes: " + closedBoxesNum);
//            System.out.println(" heuristic: " + nextNodes[i].getHeuristic());
        }

//        System.out.print("Unsorted nextNodes: ");
//        for (int i = 0; i < nextNodes.length; ++i) System.out.print(nextNodes[i].getHeuristic() + " ");
//        System.out.println();

        if (maxNode) Arrays.sort(nextNodes);
        else Arrays.sort(nextNodes, Collections.reverseOrder());

//        System.out.print("Sorted nextNodes: ");
//        for (int i = 0; i < nextNodes.length; ++i) System.out.print(nextNodes[i].getHeuristic() + " ");
//        System.out.println();

        for (int i = 0; i < nextNodes.length; ++i) {
            List<Edge> nextMoves = new ArrayList<>(nextNodes.length - 1);
            for (int j = 0; j < nextNodes.length - 1; ++j) nextMoves.add(nextNodes[(j < i ? j : j + 1)].getMove());
            Node nextNode = search(nextNodes[i].getState(), nextMoves, alpha, beta, depth + 1, turnByTurn);
            if (depth == 0 && cvcMode) nextNodes[i].setHeuristic(nextNode.getHeuristic());
            if (maxNode && resultNode.getHeuristic() < nextNode.getHeuristic() ||
                    !maxNode && resultNode.getHeuristic() > nextNode.getHeuristic()) {
                resultNode.setMove(nextNodes[i].getMove());
                resultNode.setHeuristic(nextNode.getHeuristic());
            }
            if (state.getCurrentPlayerScore() == nextNodes[i].getState().getCurrentPlayerScore() && (
                    maxNode && nextNode.getHeuristic() >= beta ||
                            !maxNode && nextNode.getHeuristic() <= alpha)) return resultNode;
            if (maxNode) alpha = Math.max(alpha, resultNode.getHeuristic());
            else beta = Math.min(beta, resultNode.getHeuristic());
        }

        return resultNode;
    }

    protected Edge getBestMove(UnsafeLinkedList<Edge> moves, boolean turnByTurn) {
        player.game.getState().setCanModifyGame(false);
//        Node result = search(player.game.getState(), moves, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, turnByTurn);
//        Node result = search(moves, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, turnByTurn); // WIP undo instead of copy
        // TODO: Remove this
        Node result = newImpl ?
                search(moves, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, turnByTurn) :
                search(player.game.getState(), moves, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, turnByTurn);
        player.game.getState().setCanModifyGame(true);
        return result.getMove();
    }
}