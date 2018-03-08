/*******************************************************************************
 * This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Peter Smith
 *******************************************************************************/
package org.boris.xlloop.util;

public class ServerExample {
	public static void main(String[] args) throws Exception {
		// Create function server on the default port
		int port = Integer.parseInt(PropertiesHelper.getConnectionConfig().getProperty("Xlloop_port"));
		FunctionHandlerServer fhs = new FunctionHandlerServer(port, true);

		// Add methods and infor
		fhs.addMethods("AES.", AES.class);
		fhs.addMethods("RSA.", RSA.class);

		// Run the engine
		System.out.println("Listening on port " + fhs.getPort() + "...");
		fhs.run();
	}
}
