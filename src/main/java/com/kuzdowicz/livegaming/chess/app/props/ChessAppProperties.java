package com.kuzdowicz.livegaming.chess.app.props;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

public class ChessAppProperties {

	private static final Logger logger = Logger.getLogger(ChessAppProperties.class);
	private static Resource resource = new ClassPathResource("/chessApp.properties");
	private static Properties props;

	public static String getProperty(String key) {
		String property = null;
		try {
			props = PropertiesLoaderUtils.loadProperties(resource);
			property = props.getProperty(key);
		} catch (IOException e) {
			logger.debug(e);
		}
		return property;
	}

}
