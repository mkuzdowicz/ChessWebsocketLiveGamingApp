package com.kuzdowicz.livegaming.chess.app.schedulers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.kuzdowicz.livegaming.chess.app.domain.UserAccount;
import com.kuzdowicz.livegaming.chess.app.repositories.UsersRepository;

@Component
public class CheckUsersCollectionScheduler {

	private static final Logger logger = LoggerFactory.getLogger(CheckUsersCollectionScheduler.class);
	private static final long ONE_HOUR_MS = 3600000;
	private static final long ONE_DAY_MS = ONE_HOUR_MS * 24;
	private static final long THREE_DAYS_MS = ONE_DAY_MS * 3;

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	private final UsersRepository usersRpository;

	@Autowired
	public CheckUsersCollectionScheduler(UsersRepository usersRpository) {
		this.usersRpository = usersRpository;
	}

	@Scheduled(fixedRate = THREE_DAYS_MS)
	public void cleanUsersFromNotConfirmedByOneWeek() {
		logger.debug("cleanUsersFromNotConfirmedByOneWeek()");
		logger.debug("Method executed at time : " + dateFormat.format(new Date()));
		logger.debug("-------------------------------------------------------");

		List<UserAccount> notConfirmedAccounts = usersRpository.findAll().stream()//
				.filter(u -> StringUtils.isNoneBlank(u.getRegistrationHashString()))//
				.filter(u -> !u.getIsRegistrationConfirmed())//
				.filter(u -> isNotConfirmedAccountDeprecated(u.getRegistrationDate()))//
				.collect(Collectors.toList());

		notConfirmedAccounts.forEach(u -> {
			usersRpository.delete(u);
			logger.debug("User: " + u.getUsername() + " was deprecated");
			logger.debug(u.getUsername() + " was removed from db");
		});

	}

	public static boolean isNotConfirmedAccountDeprecated(Date registrationDate) {
		Integer ONE_WEEK_PLUS_ONE_DAY = 1;
		DateTime currentDate = DateTime.now();
		DateTime userRegistrationDateTime = new DateTime(registrationDate);
		Integer numberOfDays = Days.daysBetween(userRegistrationDateTime, currentDate).getDays();
		return numberOfDays > ONE_WEEK_PLUS_ONE_DAY ? true : false;
	}

}
