package com.fincons.proton.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import java.util.logging.Logger;

import com.ibm.hrl.proton.server.executor.ProtonExecutor;

public class PropertiesHelper {

	private static Properties connectionConfig = new Properties();
	private static Properties proton = new Properties();
	
	private static final Logger logger = Logger.getLogger(PropertiesHelper.class.getName());
	private static String configFilePath;
	
	static {
		
		configFilePath = System.getProperty("com.fincons.configFile");
		readProp(connectionConfig);
		
		configFilePath = System.getProperty("com.fincons.protonConfig");
		readProp(proton);
	}

	public static void readProp(Properties props) {
		if (props.isEmpty()) {
			InputStream input = null;
		
			try {
				input = new FileInputStream(configFilePath);
			
				props.load(input);
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				if (input != null) {
					try {
						input.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}else{
			logger.info("Properties for "+configFilePath +" loaded");
		}
	}
	
	public static Properties getConnectionConfig() {
		return connectionConfig;
	}

	public static Properties getProton() {
		return proton;
	}

}
