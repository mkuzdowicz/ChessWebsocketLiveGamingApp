package com.kuzdowicz.livegaming;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.kuzdowicz.livegaming.chess.app.ChessLiveGamingAppApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ChessLiveGamingAppApplication.class)
@WebAppConfiguration
public class ChessLiveGamingAppApplicationTests {

	@Test
	public void contextLoads() {
	}

}
