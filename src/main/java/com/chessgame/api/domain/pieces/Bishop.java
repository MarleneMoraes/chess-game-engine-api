package com.chessgame.api.domain.pieces;

import java.util.HashSet;
import java.util.Set;
import com.chessgame.api.domain.model.Board;
import com.chessgame.api.domain.model.ChessPiece;
import com.chessgame.api.domain.model.Color;
import com.chessgame.api.domain.model.Position;

public class Bishop extends ChessPiece {

    public Bishop(Board board, Color color) {
        super(board, color);
    }

    @Override
    public String toString() {
        return "B";
    }

    @Override
    public Set<Position> possibleMoves() {
        Set<Position> set = new HashSet<>();

        checkDirection(set, new Position(position.getRow() - 1, position.getColumn() - 1), -1, -1);
        checkDirection(set, new Position(position.getRow() - 1, position.getColumn() + 1), -1, 1);
        checkDirection(set, new Position(position.getRow() + 1, position.getColumn() + 1), 1, 1);
        checkDirection(set, new Position(position.getRow() + 1, position.getColumn() - 1), 1, -1);

        return set;
    }

    private void checkDirection(Set<Position> set, Position p, int rowOffset, int colOffset) {
        while (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) {
            set.add(new Position(p.getRow(), p.getColumn()));
            p.setValues(p.getRow() + rowOffset, p.getColumn() + colOffset);
        }
        if (getBoard().positionExists(p) && isThereOpponentPiece(p)) {
            set.add(new Position(p.getRow(), p.getColumn()));
        }
    }
}