package com.chessgame.api.domain.model;

import com.chessgame.api.domain.exception.BoardException;

import lombok.Getter;

@Getter
public class Board {

	private int rows;
	private int columns;
	private Piece[][] pieces;
	
	public Board(int rows, int columns) {
		if (rows < 1 || columns < 1) {
			throw new BoardException("Error creating board: there must be at least 1 row and 1 column");
		}
		this.rows = rows;
		this.columns = columns;
		pieces = new Piece[rows][columns];
	}

	public Piece piece(int row, int column) {
        validatePosition(row, column);
        return pieces[row][column];
    }
	
	public Piece piece(Position position) {
        validatePosition(position);
        return pieces[position.getRow()][position.getColumn()];
    }
	
	public void placePiece(Piece piece, Position position) {
		if (thereIsAPiece(position)) {
			throw new BoardException("There is already a piece on position " + position);
		}
		pieces[position.getRow()][position.getColumn()] = piece;
		piece.setPosition(position);
	}
	
	public Piece removePiece(Position position) {
		validatePosition(position);
        Piece targetPiece = piece(position);
        
        if (targetPiece == null) {
            return null;
        }

        targetPiece.setPosition(null);
        pieces[position.getRow()][position.getColumn()] = null;
        return targetPiece;
	}
	
	private boolean positionExists(int row, int column) {
		return row >= 0 && row < rows && column >= 0 && column < columns;
	}
	
	public boolean positionExists(Position position) {
        return positionExists(position.getRow(), position.getColumn());
    }

    public boolean thereIsAPiece(Position position) {
        validatePosition(position);
        return piece(position) != null;
    }
    
    private void validatePosition(Position position) {
        if (!positionExists(position)) {
            throw new BoardException("Position not on the board");
        }
    }

    private void validatePosition(int row, int column) {
        if (!positionExists(row, column)) {
            throw new BoardException("Position not on the board");
        }
    }
}
