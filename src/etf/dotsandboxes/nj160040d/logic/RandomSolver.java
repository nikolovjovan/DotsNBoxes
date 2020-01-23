package etf.dotsandboxes.nj160040d.logic;

import etf.dotsandboxes.nj160040d.util.UnsafeLinkedList;

public class RandomSolver implements Solver {

    private AIPlayer player;

    public RandomSolver(AIPlayer player) {
        this.player = player;
    }

    @Override
    public Edge getNextMove() {
        UnsafeLinkedList<Edge> availableMoves = player.game.getState().getAvailableMoves();
        if (availableMoves.isEmpty()) return new Edge();
        for (Edge move : availableMoves) if (player.game.getState().addsNthEdge(move, 4)) return move;
        return availableMoves.get((int) Math.floor(Math.random() * availableMoves.size()));
    }
}