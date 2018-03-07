package com.fincons.token.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class PropertiesHelperOLD {

	private static Properties connectionConfig = new Properties();

	private static final Logger logger = Logger.getLogger(PropertiesHelper.class.getName());
	private static String configFilePath;

	static {

		configFilePath = System.getenv("CONFIG_PATH");
		readProp(connectionConfig);
	}

	public static void readProp(Properties props) {
		if (props.isEmpty()) {
			InputStream input = null;

			try {
				input = new FileInputStream(configFilePath + "config.properties");

				props.load(input);
			} catch (Exception ex) {
				logger.error("Reading configuration failed", ex);
			} finally {
				if (input != null) {
					try {
						input.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			logger.info("Configuration file " + configFilePath + " loaded");
		}
	}

	public static Properties getConnectionConfig() {
		return connectionConfig;
	}

}
