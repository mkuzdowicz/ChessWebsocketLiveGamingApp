package com.kuzdowicz.livegaming.chess.app.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.kuzdowicz.livegaming.chess.app.models.UserAccount;


public interface UsersRepository extends MongoRepository<UserAccount, String> {

	UserAccount findOneByUsername(String username);
	
	UserAccount findOneByRegistrationHashString(String hash);

}
