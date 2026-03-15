package com.chessgame.api.infrastructure.web.dto;

import java.util.List;
import java.util.UUID;

import com.chessgame.api.domain.model.Color;

public record GameResponse(
	    UUID id,
	    String[][] board,
	    Color currentPlayer,
	    boolean isCheck,
	    boolean isCheckMate,
	    List<String> capturedPieces,
	    String aiSuggestion
	) {}