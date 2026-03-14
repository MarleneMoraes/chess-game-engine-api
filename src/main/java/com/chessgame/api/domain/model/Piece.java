package com.chessgame.api.domain.model;

import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
public abstract class Piece {

    @Setter
    protected Position position;
    
    @Getter(AccessLevel.PROTECTED)
    private final Board board;

    public Piece(Board board) {
        this.board = board;
        this.position = null;
    }

    public abstract Set<Position> possibleMoves();

    public boolean possibleMove(Position targetPosition) {
        return possibleMoves().contains(targetPosition);
    }

    public boolean isThereAnyPossibleMove() {
        return !possibleMoves().isEmpty();
    }
}