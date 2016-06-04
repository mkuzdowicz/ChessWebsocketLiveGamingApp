package com.kuzdowicz.livegaming.chess.app.websockets;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.Session;

import org.apache.log4j.Logger;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class WebSocketSessionsRepository {

	private final Logger logger = Logger.getLogger(WebSocketSessionsRepository.class);

	volatile static Map<String, WebSocketSession> sessionsMap = new ConcurrentHashMap<>();

	private Gson gson = new GsonBuilder().setPrettyPrinting().create();

	public synchronized void addSession(String username, WebSocketSession session) {
		String usernameKey = "username";
		session.getAttributes().put(usernameKey, username);
		sessionsMap.put(username, session);
	}

	public synchronized void removeSession(String username) {
		WebSocketSession session = sessionsMap.remove(username);
		logger.info("session: " + session.getId() + " removed");
	}

	public void sendToAllConnectedSessions(String msg) {
		logger.info("sendToAllConnectedSessions");
		for (String username : sessionsMap.keySet()) {
			WebSocketSession userSession = sessionsMap.get(username);
			try {
				String jsonUsersList = gson.toJson(GameUsersRepository.gameUsersMap.values());
				TextMessage tm = new TextMessage(jsonUsersList.getBytes());
				userSession.sendMessage(tm);
			} catch (IOException e) {
				logger.info(e);
			}
		}
	}

	public synchronized void sendToAllConnectedSessionsActualParticipantList() {

		try {

			String jsonUsersList = gson.toJson(GameUsersRepository.gameUsersMap.values());

			for (String username : sessionsMap.keySet()) {
				WebSocketSession userSession = sessionsMap.get(username);
				if (userSession != null) {

					TextMessage tm = new TextMessage(jsonUsersList.getBytes());
					userSession.sendMessage(tm);

				}

			}
		} catch (Exception e) {
			logger.info(e);
		}
	}

	public void sendToSession(String toUsernameName, String fromUsername, String message) {
		logger.info("sendToSession()");

		WebSocketSession userSession = sessionsMap.get(toUsernameName);
		if (userSession != null) {
			try {
				TextMessage tm = new TextMessage(message.getBytes());
				userSession.sendMessage(tm);
			} catch (IOException e) {
				logger.debug(e);
			}
		}

	}

	public void printOutAllSessionsOnOpen(Session addedSession) {

		logger.info("dodano sesje: " + addedSession.getId());
		logger.info("sessionOwner: " + addedSession.getUserProperties().get("sessionOwner"));

		logger.info("obecne sesje: ");
		for (String username : sessionsMap.keySet()) {
			WebSocketSession session = sessionsMap.get(username);
			logger.info(session);
		}

	}

	public void printOutAllSessions() {

		logger.info("obecne sesje: ");
		System.out.println(sessionsMap);

	}

	public Boolean userHaveSessionAndIsConnected(String username) {
		if (sessionsMap.containsKey(username)) {
			return true;
		}
		return false;
	}

	public void printOutAllSessionsOnClose(Session removedSession) {
		logger.info("usunieto sesje: " + removedSession.getId());
		logger.info("sessionOwner: " + removedSession.getUserProperties().get("sessionOwner"));

		logger.info("pozostałe sesje: ");
		for (String username : sessionsMap.keySet()) {
			WebSocketSession session = sessionsMap.get(username);
			logger.info(session.getId());
		}
	}

}
