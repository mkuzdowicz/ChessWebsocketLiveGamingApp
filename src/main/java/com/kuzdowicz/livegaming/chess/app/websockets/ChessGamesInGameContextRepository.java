package com.kuzdowicz.livegaming.chess.app.websockets;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Seconds;

import com.kuzdowicz.livegaming.chess.app.models.ChessGame;
import com.kuzdowicz.livegaming.chess.app.models.ChessMove;

public class ChessGamesInGameContextRepository {

	private final Logger logger = Logger.getLogger(ChessGamesInGameContextRepository.class);

	private volatile static Map<String, ChessGame> chessGamesMap = new ConcurrentHashMap<>();

	public synchronized void addNewGame(ChessGame game) {
		logger.debug("addNewGame()");

		chessGamesMap.put(game.getUniqueGameHash(), game);

	}

	public synchronized void removeGame(String uniqueGameHash) {
		logger.debug("addNewGame()");

		chessGamesMap.remove(uniqueGameHash);

	}

	public synchronized ChessGame getGameByUniqueHashId(String uniqueGameHash) {
		logger.debug("addNewGame()");

		ChessGame game = chessGamesMap.get(uniqueGameHash);
		return game;

	}

	public synchronized void incrementNumberOfMoves(String uniqueGameHash) {
		logger.debug("incrementNumberOfMoves()");

		ChessGame game = chessGamesMap.get(uniqueGameHash);
		int tempVal = game.getNumberOfMoves();
		tempVal++;
		game.setNumberOfMoves(tempVal);

	}

	public synchronized List<ChessGame> getChessGamesList() {
		logger.debug("getChessGamesList()");

		List<ChessGame> values = chessGamesMap.values().stream().collect(Collectors.toList());
		return values;
	}

	public synchronized void addActualMoveToThisGameObject(String uniqueGameHash, ChessMove currentMove) {
		logger.debug("addNewGame()");

		ChessGame game = chessGamesMap.get(uniqueGameHash);
		game.getListOfMoves().add(currentMove);

	}

	public synchronized static void calculateAndSetTimeDurationBeetwenGameBeginAndEnd(ChessGame game) {

		if (game.getBeginDate() != null && game.getEndDate() != null) {

			DateTime beginDate = new DateTime(game.getBeginDate());
			DateTime endDate = new DateTime(game.getEndDate());
			Seconds secondsDuration = Seconds.secondsBetween(beginDate, endDate);
			Long duration = secondsDuration.toStandardDuration().getMillis();
			DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
			dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			Date time = new Date(duration);
			String formattedTime = dateFormat.format(time);

			if (duration != null) {
				game.setGameDurationMillis(duration);
			}
			if (formattedTime != null) {
				game.setFormattedGameDurationStr(formattedTime);
			}

		}

	}

}
