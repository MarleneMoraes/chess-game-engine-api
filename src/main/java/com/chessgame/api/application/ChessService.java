package com.chessgame.api.application;

import com.chessgame.api.domain.model.ChessMatch;
import com.chessgame.api.domain.model.ChessPiece;
import com.chessgame.api.domain.model.ChessPosition;
import com.chessgame.api.domain.model.GameDifficulty;
import com.chessgame.api.infrastructure.persistence.GameEntity;
import com.chessgame.api.infrastructure.persistence.GameRepository;
import com.chessgame.api.infrastructure.web.dto.GameResponse;
import com.chessgame.api.infrastructure.web.dto.MoveRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChessService {

    private final GameRepository gameRepository;

    @Transactional
    public GameResponse createGame(GameDifficulty difficulty) {
        ChessMatch match = new ChessMatch(); 
        
        GameEntity entity = new GameEntity();
        entity.setDifficulty(difficulty);
        entity.setCurrentTurn("WHITE");
        entity.setBoardFen("startpos");
        
        entity = gameRepository.save(entity);
        return mapToResponse(entity, match);
    }

    @Transactional
    public GameResponse makeMove(UUID gameId, MoveRequest request) {
        GameEntity entity = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Jogo não encontrado"));

        ChessMatch match = new ChessMatch(); 
        
        match.performChessMove(
            new ChessPosition(request.source().charAt(0), Integer.parseInt(request.source().substring(1))),
            new ChessPosition(request.target().charAt(0), Integer.parseInt(request.target().substring(1)))
        );

        entity.setCurrentTurn(match.getCurrentPlayer().toString());
        entity.setCheck(match.isCheck());
        entity.setCheckMate(match.isCheckMate());
        gameRepository.save(entity);

        return mapToResponse(entity, match);
    }
    
    public GameResponse getGameById(UUID id) {
        GameEntity entity = gameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jogo não encontrado"));

       ChessMatch match = new ChessMatch(); 

        return mapToResponse(entity, match);
    }

    private GameResponse mapToResponse(GameEntity entity, ChessMatch match) {
        return new GameResponse(
            entity.getId(),
            convertBoard(match.getPieces()),
            match.getCurrentPlayer(),
            match.isCheck(),
            match.isCheckMate(),
            match.getCapturedPieces().stream()
            .map(p -> ((ChessPiece)p).getColor() + "_" + p.getClass().getSimpleName().toUpperCase())
            .toList(),
            null  
        );
    }

    private String[][] convertBoard(ChessPiece[][] pieces) {
        String[][] board = new String[8][8];
        
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
            	
                if (pieces[i][j] != null) {
                    board[i][j] = pieces[i][j].getColor() + "_" + pieces[i][j].getClass().getSimpleName().toUpperCase();
                } else {
                    board[i][j] = null;
                }
            }
        }
        return board;
    }
}