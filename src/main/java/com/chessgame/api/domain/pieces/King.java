package com.chessgame.api.domain.pieces;

import java.util.HashSet;
import java.util.Set;

import com.chessgame.api.domain.model.Board;
import com.chessgame.api.domain.model.ChessMatch;
import com.chessgame.api.domain.model.ChessPiece;
import com.chessgame.api.domain.model.Color;
import com.chessgame.api.domain.model.Position;

public class King extends ChessPiece {

	private final ChessMatch chessMatch;

	public King(Board board, Color color, ChessMatch chessMatch) {
		super(board, color);
		this.chessMatch = chessMatch;
	}

	@Override
	public String toString() {
		return "K";
	}

	private boolean canMove(Position position) {
		ChessPiece p = (ChessPiece) getBoard().piece(position);
		return p == null || p.getColor() != getColor();
	}

	private boolean testRookCastling(Position position) {
		ChessPiece p = (ChessPiece) getBoard().piece(position);
		return p instanceof Rook && p.getColor() == getColor() && p.getMoveCount() == 0;
	}

	@Override
	public Set<Position> possibleMoves() {
		Set<Position> set = new HashSet<>();
		Position p = new Position(0, 0);

		int[] rowOffsets = { -1, 1, 0, 0, -1, -1, 1, 1 };
		int[] colOffsets = { 0, 0, -1, 1, -1, 1, -1, 1 };

		for (int i = 0; i < 8; i++) {
			p.setValues(position.getRow() + rowOffsets[i], position.getColumn() + colOffsets[i]);
			if (getBoard().positionExists(p) && canMove(p)) {
				set.add(new Position(p.getRow(), p.getColumn()));
			}
		}

		if (getMoveCount() == 0 && !chessMatch.isCheck()) {
			Position posT1 = new Position(position.getRow(), position.getColumn() + 3);
			
			if (testRookCastling(posT1)) {
				Position p1 = new Position(position.getRow(), position.getColumn() + 1);
				Position p2 = new Position(position.getRow(), position.getColumn() + 2);
				
				if (getBoard().piece(p1) == null && getBoard().piece(p2) == null) {
					set.add(new Position(position.getRow(), position.getColumn() + 2));
				}
			}

			Position posT2 = new Position(position.getRow(), position.getColumn() - 4);
			if (testRookCastling(posT2)) {
				Position p1 = new Position(position.getRow(), position.getColumn() - 1);
				Position p2 = new Position(position.getRow(), position.getColumn() - 2);
				Position p3 = new Position(position.getRow(), position.getColumn() - 3);
				
				if (getBoard().piece(p1) == null && getBoard().piece(p2) == null && getBoard().piece(p3) == null) {
					set.add(new Position(position.getRow(), position.getColumn() - 2));
				}
			}
		}

		return set;
	}
}