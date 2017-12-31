package org.fxapps.kieserverclient.service;

/**
 * 
 * Manages the creation of KieServerClient class to make it work as a singleton
 * 
 * @author wsiqueir
 *
 */
public class KieServerClientManager {

	static KieServerClientService INSTANCE;

	private KieServerClientManager() {

	}

	public static KieServerClientService login(String url, String username,
			String password) {
		INSTANCE = new KieServerClientServiceImpl();
		INSTANCE.login(url, username, password);
		return getInstance();
	}

	public static void logout() {
		INSTANCE = null;
	}

	public static boolean isLoggedIn() {
		return INSTANCE != null;
	}

	public static KieServerClientService getInstance() {
		if (INSTANCE == null) {
			throw new Error("Not logged in!");
		}
		return INSTANCE;
	}

}
