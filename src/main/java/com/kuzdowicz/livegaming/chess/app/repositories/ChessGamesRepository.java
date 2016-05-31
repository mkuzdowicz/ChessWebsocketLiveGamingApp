package com.kuzdowicz.livegaming.chess.app.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.kuzdowicz.livegaming.chess.app.models.ChessGame;


public interface ChessGamesRepository extends MongoRepository<ChessGame, String> {

	List<ChessGame> findAllByWhiteColUsernameOrBlackColUsername(String username);

}
