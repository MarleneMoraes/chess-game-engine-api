package com.chessgame.api.domain.exception;

public class BoardException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public BoardException(String msg) {
		super(msg);
	}

	public BoardException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
