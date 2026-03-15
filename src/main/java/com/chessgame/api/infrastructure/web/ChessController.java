package com.chessgame.api.infrastructure.web;

import com.chessgame.api.application.ChessService;
import com.chessgame.api.domain.model.GameDifficulty;
import com.chessgame.api.infrastructure.web.dto.GameResponse;
import com.chessgame.api.infrastructure.web.dto.MoveRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
public class ChessController {

	private final ChessService chessService;

	@PostMapping
	public ResponseEntity<GameResponse> createGame(@RequestParam GameDifficulty difficulty) {

		return ResponseEntity.ok(chessService.createGame(difficulty));
	}

	@PatchMapping("/{id}/move")
	public ResponseEntity<GameResponse> makeMove(@PathVariable UUID id, 
												@Valid @RequestBody MoveRequest request) {
		
		return ResponseEntity.ok(chessService.makeMove(id, request));
	}

	@GetMapping("/{id}")
	public ResponseEntity<GameResponse> getGame(@PathVariable UUID id) {
		
		return ResponseEntity.ok(chessService.getGameById(id));
	}
}