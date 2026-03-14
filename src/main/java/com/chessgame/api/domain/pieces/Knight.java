package com.chessgame.api.domain.pieces;

import java.util.HashSet;
import java.util.Set;
import com.chessgame.api.domain.model.Board;
import com.chessgame.api.domain.model.ChessPiece;
import com.chessgame.api.domain.model.Color;
import com.chessgame.api.domain.model.Position;

public class Knight extends ChessPiece {

	public Knight(Board board, Color color) {
		super(board, color);
	}

	@Override
	public String toString() {
		return "N";
	}

	private boolean canMove(Position position) {
		ChessPiece p = (ChessPiece) getBoard().piece(position);
		return p == null || p.getColor() != getColor();
	}

	@Override
	public Set<Position> possibleMoves() {
		Set<Position> set = new HashSet<>();
		Position p = new Position(0, 0);

		int[] rowOffsets = { -1, -2, -2, -1, 1, 2, 2, 1 };
		int[] colOffsets = { -2, -1, 1, 2, 2, 1, -1, -2 };

		for (int i = 0; i < 8; i++) {
			p.setValues(position.getRow() + rowOffsets[i], position.getColumn() + colOffsets[i]);
			
			if (getBoard().positionExists(p) && canMove(p)) {
				set.add(new Position(p.getRow(), p.getColumn()));
			}
		}

		return set;
	}
}