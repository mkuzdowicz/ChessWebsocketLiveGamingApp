package com.kuzdowicz.livegaming.chess.app.websockets;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.Session;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

@Service
public class WebSocketSessionHandler {

	private final Logger logger;

	private volatile static Map<String, Session> sessionsMap = new ConcurrentHashMap<>();

	private Gson gson;

	public WebSocketSessionHandler() {
		gson = new Gson();
		logger = Logger.getLogger(WebSocketSessionHandler.class);
	}

	public synchronized void addSession(String username, Session session) {
		sessionsMap.put(username, session);
	}

	public synchronized void removeSession(String username) {
		Session session = sessionsMap.remove(username);
		logger.info("session: " + session.getId() + " removed");
	}

	public void sendToAllConnectedSessions(String msg) {
		for (String username : sessionsMap.keySet()) {
			Session userSession = sessionsMap.get(username);
			try {
				userSession.getBasicRemote().sendText(msg);
			} catch (IOException e) {
				logger.info(e);
			}
		}
	}

	public synchronized void sendToAllConnectedSessionsActualParticipantList() {

		String jsonUsersList = gson.toJson(GameUsersHandler.gameUsersMap
				.values());
		for (String username : sessionsMap.keySet()) {
			Session userSession = sessionsMap.get(username);
			try {
				userSession.getBasicRemote().sendText(jsonUsersList);
			} catch (IOException e) {
				logger.info(e);
			}
		}
	}

	public void sendToSession(String toUsernameName, String fromUsername,
			String message) {
		logger.info("sendToSession()");

		Session userSession = sessionsMap.get(toUsernameName);
		if (userSession != null) {
			try {
				userSession.getBasicRemote().sendText(message);
			} catch (IOException e) {
				logger.debug(e);
			}
		}

	}

	public void printOutAllSessionsOnOpen(Session addedSession) {

		logger.info("dodano sesje: " + addedSession.getId());
		logger.info("sessionOwner: "
				+ addedSession.getUserProperties().get("sessionOwner"));

		logger.info("obecne sesje: ");
		for (String username : sessionsMap.keySet()) {
			Session session = sessionsMap.get(username);
			System.out.println(session);
		}

	}

	public Boolean userHaveSessionAndIsConnected(String username) {
		Session userSession = sessionsMap.get(username);
		Map<String, Object> userPropertiesInSession = userSession
				.getUserProperties();
		if (userPropertiesInSession.containsValue(username)) {
			return true;
		}
		return false;
	}

	public void printOutAllSessionsOnClose(Session removedSession) {
		logger.info("usunieto sesje: " + removedSession.getId());
		logger.info("sessionOwner: "
				+ removedSession.getUserProperties().get("sessionOwner"));

		logger.info("pozostałe sesje: ");
		for (String username : sessionsMap.keySet()) {
			Session session = sessionsMap.get(username);
			System.out.println(session.getId());
		}
	}

}
