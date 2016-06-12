package com.kuzdowicz.livegaming.tests;

import static org.junit.Assert.*;

import java.util.Date;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.kuzdowicz.livegaming.chess.app.db.domain.ChessGame;
import com.kuzdowicz.livegaming.chess.app.livegaming.repositories.LiveChessGamesRepository;

public class LiveChessGamesRepositoryTest {

	private ChessGame game;
	private DateTime dateTimeNow;
	private LiveChessGamesRepository liveChessGamesRepository;

	@Before
	public void prepare() {
		game = new ChessGame();
		dateTimeNow = DateTime.now();
		liveChessGamesRepository = new LiveChessGamesRepository();
	}

	@Test
	public void calculateAndSetTimeDurationBeetwenGameBeginAndEnd_test1() {

		long duration10MinutesExpected = 6000000;
		Date dateAfterTenMinnutes = dateTimeNow.plus(duration10MinutesExpected).toDate();

		game.setBeginDate(dateTimeNow.toDate());
		game.setEndDate(dateAfterTenMinnutes);
		liveChessGamesRepository.calculateAndSetTimeDurationBeetwenGameBeginAndEnd(game);

		long durationResult = game.getGameDurationMillis();

		assertEquals(duration10MinutesExpected, durationResult);
	}

	@Test
	public void calculateAndSetTimeDurationBeetwenGameBeginAndEnd_test2() {

		long duration1HouerExpected = 3600000;
		Date dateAfterTenMinnutes = dateTimeNow.plus(duration1HouerExpected).toDate();

		game.setBeginDate(dateTimeNow.toDate());
		game.setEndDate(dateAfterTenMinnutes);

		liveChessGamesRepository.calculateAndSetTimeDurationBeetwenGameBeginAndEnd(game);

		long durationResult = game.getGameDurationMillis();

		assertEquals(duration1HouerExpected, durationResult);

	}

}
