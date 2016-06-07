package com.kuzdowicz.livegaming.chess.app.livegaming;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.google.gson.Gson;

@Component
public class WebSocketSessionsRepository {

	private final static Logger logger = LoggerFactory.getLogger(WebSocketSessionsRepository.class);

	protected volatile static Map<String, WebSocketSession> sessionsMap = new ConcurrentHashMap<>();

	private final Gson gson;

	@Autowired
	public WebSocketSessionsRepository(Gson gson) {
		this.gson = gson;
	}

	public synchronized void addSession(String username, WebSocketSession session) {
		String usernameKey = "username";
		session.getAttributes().put(usernameKey, username);
		sessionsMap.put(username, session);
	}

	public synchronized void removeSession(String username) {
		sessionsMap.remove(username);
	}

	public void sendToAllConnectedSessions(String msg) {
		for (String username : sessionsMap.keySet()) {
			WebSocketSession userSession = sessionsMap.get(username);
			try {
				String jsonUsersList = gson.toJson(LiveGamingUsersRepository.gameUsersMap.values());
				TextMessage tm = new TextMessage(jsonUsersList.getBytes());
				userSession.sendMessage(tm);
			} catch (IOException e) {
				logger.warn("exception occured: ", e);
			}
		}
	}

	public synchronized void sendToAllConnectedSessionsActualParticipantList() {

		try {
			String jsonUsersList = gson.toJson(LiveGamingUsersRepository.gameUsersMap.values());

			for (String username : sessionsMap.keySet()) {
				WebSocketSession userSession = sessionsMap.get(username);
				if (userSession != null) {
					TextMessage tm = new TextMessage(jsonUsersList.getBytes());
					userSession.sendMessage(tm);
				}
			}
		} catch (Exception e) {
			logger.warn("exception occured: ", e);
		}
	}

	public void sendToSession(String toUsernameName, String fromUsername, String message) {
		WebSocketSession userSession = sessionsMap.get(toUsernameName);
		if (userSession != null) {
			try {
				TextMessage tm = new TextMessage(message.getBytes());
				userSession.sendMessage(tm);
			} catch (IOException e) {
				logger.warn("exception occured: ", e);
			}
		}

	}

	public Boolean userHaveSessionAndIsConnected(String username) {
		if (sessionsMap.containsKey(username)) {
			return true;
		}
		return false;
	}

}
