package com.kuzdowicz.livegaming.chess.app.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.kuzdowicz.livegaming.chess.app.models.UserAccount;

public interface UsersRepository extends MongoRepository<UserAccount, String> {

	UserAccount findOneByUsername(String username);

	UserAccount findOneByRegistrationHashString(String hash);

	@Query(value = "{ numberOfWonChessGames : { $gt : 0 } }")
	List<UserAccount> findAllWhereNumberOfWonChessGamesGt0(Pageable pageable);

}
