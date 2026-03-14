package com.chessgame.api.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.chessgame.api.domain.exception.ChessException;
import com.chessgame.api.domain.pieces.Bishop;
import com.chessgame.api.domain.pieces.King;
import com.chessgame.api.domain.pieces.Knight;
import com.chessgame.api.domain.pieces.Pawn;
import com.chessgame.api.domain.pieces.Queen;
import com.chessgame.api.domain.pieces.Rook;

import lombok.Getter;

@Getter
public class ChessMatch {

	private int turn;
	private Color currentPlayer;
	private final Board board;
	private boolean check;
	private boolean checkMate;
	private ChessPiece enPassantVulnerable;
	private ChessPiece promoted;

	private final List<Piece> piecesOnTheBoard = new ArrayList<>();
	private final List<Piece> capturedPieces = new ArrayList<>();

	public ChessMatch() {
		board = new Board(8, 8);
		turn = 1;
		currentPlayer = Color.WHITE;
		initialSetup();
	}

	public ChessPiece[][] getPieces() {
		ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
		for (int i = 0; i < board.getRows(); i++) {
			for (int j = 0; j < board.getColumns(); j++) {
				mat[i][j] = (ChessPiece) board.piece(i, j);
			}
		}
		return mat;
	}

	public Set<Position> possibleMoves(ChessPosition sourcePosition) {
		Position position = sourcePosition.toPosition();
		validateSourcePosition(position);
		return board.piece(position).possibleMoves();
	}

	public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
		Position source = sourcePosition.toPosition();
		Position target = targetPosition.toPosition();

		validateSourcePosition(source);
		validateTargetPosition(source, target);

		Piece capturedPiece = makeMove(source, target);

		if (testCheck(currentPlayer)) {
			undoMove(source, target, capturedPiece);
			throw new ChessException("You can't put yourself in check");
		}

		ChessPiece movedPiece = (ChessPiece) board.piece(target);

		promoted = null;
		if (movedPiece instanceof Pawn) {
			if ((movedPiece.getColor() == Color.WHITE && target.getRow() == 0)
					|| (movedPiece.getColor() == Color.BLACK && target.getRow() == 7)) {
				promoted = (ChessPiece) board.piece(target);
				promoted = replacePromotedPiece("Q"); // Default para Rainha
			}
		}

		check = testCheck(opponent(currentPlayer));

		if (testCheckMate(opponent(currentPlayer))) {
			checkMate = true;
		} else {
			nextTurn();
		}

		if (movedPiece instanceof Pawn
				&& (target.getRow() == source.getRow() - 2 || target.getRow() == source.getRow() + 2)) {
			enPassantVulnerable = movedPiece;
		} else {
			enPassantVulnerable = null;
		}

		return (ChessPiece) capturedPiece;
	}

	public ChessPiece replacePromotedPiece(String type) {
		if (promoted == null) {
			throw new IllegalStateException("There is no piece to be promoted");
		}
		if (!type.equals("B") && !type.equals("N") && !type.equals("R") && !type.equals("Q")) {
			return promoted;
		}

		Position pos = promoted.getChessPosition().toPosition();
		Piece p = board.removePiece(pos);
		piecesOnTheBoard.remove(p);

		ChessPiece newPiece = createNewPiece(type, promoted.getColor());
		board.placePiece(newPiece, pos);
		piecesOnTheBoard.add(newPiece);

		return newPiece;
	}

	private ChessPiece createNewPiece(String type, Color color) {
		if (type.equals("B"))
			return new Bishop(board, color);
		if (type.equals("N"))
			return new Knight(board, color);
		if (type.equals("Q"))
			return new Queen(board, color);
		return new Rook(board, color);
	}

	private Piece makeMove(Position source, Position target) {
		ChessPiece p = (ChessPiece) board.removePiece(source);
		p.increaseMoveCount();
		Piece capturedPiece = board.removePiece(target);
		board.placePiece(p, target);

		if (capturedPiece != null) {
			piecesOnTheBoard.remove(capturedPiece);
			capturedPieces.add(capturedPiece);
		}

		if (p instanceof King && target.getColumn() == source.getColumn() + 2) {
			executeRookMove(new Position(source.getRow(), source.getColumn() + 3),
					new Position(source.getRow(), source.getColumn() + 1));
		}

		if (p instanceof King && target.getColumn() == source.getColumn() - 2) {
			executeRookMove(new Position(source.getRow(), source.getColumn() - 4),
					new Position(source.getRow(), source.getColumn() - 1));
		}

		if (p instanceof Pawn && source.getColumn() != target.getColumn() && capturedPiece == null) {
			Position pawnPosition = new Position(
					p.getColor() == Color.WHITE ? target.getRow() + 1 : target.getRow() - 1, target.getColumn());
			capturedPiece = board.removePiece(pawnPosition);
			capturedPieces.add(capturedPiece);
			piecesOnTheBoard.remove(capturedPiece);
		}

		return capturedPiece;
	}

	private void executeRookMove(Position src, Position tgt) {
		ChessPiece rook = (ChessPiece) board.removePiece(src);
		board.placePiece(rook, tgt);
		rook.increaseMoveCount();
	}

	private void undoMove(Position source, Position target, Piece capturedPiece) {
		ChessPiece p = (ChessPiece) board.removePiece(target);
		p.decreaseMoveCount();
		board.placePiece(p, source);

		if (capturedPiece != null) {
			board.placePiece(capturedPiece, target);
			capturedPieces.remove(capturedPiece);
			piecesOnTheBoard.add(capturedPiece);
		}

		if (p instanceof King && target.getColumn() == source.getColumn() + 2) {
			reverseRookMove(new Position(source.getRow(), source.getColumn() + 1),
					new Position(source.getRow(), source.getColumn() + 3));
		}

		if (p instanceof King && target.getColumn() == source.getColumn() - 2) {
			reverseRookMove(new Position(source.getRow(), source.getColumn() - 1),
					new Position(source.getRow(), source.getColumn() - 4));
		}

		if (p instanceof Pawn && source.getColumn() != target.getColumn() && capturedPiece == enPassantVulnerable) {
			ChessPiece pawn = (ChessPiece) board.removePiece(target);
			Position pawnPos = new Position(p.getColor() == Color.WHITE ? 3 : 4, target.getColumn());
			board.placePiece(pawn, pawnPos);
		}
	}

	private void reverseRookMove(Position current, Position original) {
		ChessPiece rook = (ChessPiece) board.removePiece(current);
		board.placePiece(rook, original);
		rook.decreaseMoveCount();
	}

	private void validateSourcePosition(Position position) {
		if (!board.thereIsAPiece(position)) {
			throw new ChessException("There is no piece on source position");
		}
		if (currentPlayer != ((ChessPiece) board.piece(position)).getColor()) {
			throw new ChessException("The chosen piece is not yours");
		}
		if (!board.piece(position).isThereAnyPossibleMove()) {
			throw new ChessException("There is no possible moves for the chosen piece");
		}
	}

	private void validateTargetPosition(Position source, Position target) {
		if (!board.piece(source).possibleMove(target)) {
			throw new ChessException("The chosen piece can't move to target position");
		}
	}

	private void nextTurn() {
		turn++;
		currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}

	private Color opponent(Color color) {
		return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}

	private ChessPiece king(Color color) {
		return piecesOnTheBoard.stream().filter(p -> p instanceof King && ((ChessPiece) p).getColor() == color)
				.map(p -> (ChessPiece) p).findFirst()
				.orElseThrow(() -> new IllegalStateException("There is no " + color + " king on the board"));
	}

	private boolean testCheck(Color color) {
		Position kingPosition = king(color).getChessPosition().toPosition();
		return piecesOnTheBoard.stream().filter(p -> ((ChessPiece) p).getColor() == opponent(color))
				.anyMatch(p -> p.possibleMoves().contains(kingPosition));
	}

	private boolean testCheckMate(Color color) {
		if (!testCheck(color))
			return false;

		List<Piece> list = piecesOnTheBoard.stream().filter(p -> ((ChessPiece) p).getColor() == color).toList();

		for (Piece p : list) {
			Set<Position> moves = p.possibleMoves();
			for (Position target : moves) {
				Position source = ((ChessPiece) p).getPosition();
				Piece capturedPiece = makeMove(source, target);
				boolean stillInCheck = testCheck(color);
				undoMove(source, target, capturedPiece);
				if (!stillInCheck)
					return false;
			}
		}
		return true;
	}

	private void placeNewPiece(char column, int row, ChessPiece piece) {
		board.placePiece(piece, new ChessPosition(column, row).toPosition());
		piecesOnTheBoard.add(piece);
	}

	private void initialSetup() {
		for (int i = 0; i < 8; i++) {
			char column = (char) ('a' + i);
			placeNewPiece(column, 2, new Pawn(board, Color.WHITE, this));
			placeNewPiece(column, 7, new Pawn(board, Color.BLACK, this));
		}

		setupBackRank(Color.WHITE, 1);
		setupBackRank(Color.BLACK, 8);
	}

	private void setupBackRank(Color color, int row) {
		placeNewPiece('a', row, new Rook(board, color));
		placeNewPiece('b', row, new Knight(board, color));
		placeNewPiece('c', row, new Bishop(board, color));
		placeNewPiece('d', row, new Queen(board, color));
		placeNewPiece('e', row, new King(board, color, this));
		placeNewPiece('f', row, new Bishop(board, color));
		placeNewPiece('g', row, new Knight(board, color));
		placeNewPiece('h', row, new Rook(board, color));
	}
}