package com.kuzdowicz.livegaming.chess.app.services;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

public interface AccountRegistrationMailService {

	void sendRegistrationMail(String toMailAddress, String username, String randomHashForLink)
			throws AddressException, MessagingException;

}
