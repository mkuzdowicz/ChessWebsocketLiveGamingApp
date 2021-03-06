package com.kuzdowicz.livegaming.chess.app.db.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.kuzdowicz.livegaming.chess.app.db.domain.ChessGame;

public interface ChessGamesRepository extends MongoRepository<ChessGame, String> {

	@Query(value = "{ $or : [ {whitePlayerName : ?0},  {blackPlayerName : ?0} ]}")
	List<ChessGame> findAllByWhitePlayerNameOrBlackPlayerName(String username);

}
