package com.kuzdowicz.livegaming.chess.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ChessLiveGamingAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChessLiveGamingAppApplication.class, args);
	}
}
