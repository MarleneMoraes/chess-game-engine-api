package com.chessgame.api.domain.pieces;

import java.util.HashSet;
import java.util.Set;

import com.chessgame.api.domain.model.Board;
import com.chessgame.api.domain.model.ChessMatch;
import com.chessgame.api.domain.model.ChessPiece;
import com.chessgame.api.domain.model.Color;
import com.chessgame.api.domain.model.Position;

public class Pawn extends ChessPiece {

	private final ChessMatch chessMatch;

	public Pawn(Board board, Color color, ChessMatch chessMatch) {
		super(board, color);
		this.chessMatch = chessMatch;
	}

	@Override
	public Set<Position> possibleMoves() {
		Set<Position> set = new HashSet<>();
		Position p = new Position(0, 0);

		int direction = (getColor() == Color.WHITE) ? -1 : 1;

		p.setValues(position.getRow() + direction, position.getColumn());
		
		if (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) {
			set.add(new Position(p.getRow(), p.getColumn()));

			Position p2 = new Position(position.getRow() + (direction * 2), position.getColumn());
		
			if (getBoard().positionExists(p2) && !getBoard().thereIsAPiece(p2) && getMoveCount() == 0) {
				set.add(new Position(p2.getRow(), p2.getColumn()));
			}
		}

		int[] sideOffsets = { -1, 1 };
		
		for (int offset : sideOffsets) {
			p.setValues(position.getRow() + direction, position.getColumn() + offset);
			
			if (getBoard().positionExists(p) && isThereOpponentPiece(p)) {
				set.add(new Position(p.getRow(), p.getColumn()));
			}
		}

		checkEnPassant(set, direction);

		return set;
	}

	private void checkEnPassant(Set<Position> set, int direction) {
		int enPassantRow = (getColor() == Color.WHITE) ? 3 : 4;

		if (position.getRow() == enPassantRow) {
			
			int[] sideOffsets = { -1, 1 };
			
			for (int offset : sideOffsets) {
				Position sidePos = new Position(position.getRow(), position.getColumn() + offset);
				if (getBoard().positionExists(sidePos) && isThereOpponentPiece(sidePos)
						&& getBoard().piece(sidePos) == chessMatch.getEnPassantVulnerable()) {
					set.add(new Position(sidePos.getRow() + direction, sidePos.getColumn()));
				}
			}
		}
	}

	@Override
	public String toString() {
		return "P";
	}
}