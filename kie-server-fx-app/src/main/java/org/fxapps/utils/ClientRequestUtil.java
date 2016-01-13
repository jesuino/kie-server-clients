package org.fxapps.utils;

import javax.ws.rs.core.UriBuilder;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.DefaultHttpClient;
import org.fxapps.navigation.Navigation;
import org.fxapps.navigation.Param;
import org.fxapps.service.KieServerClientManager;
import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;

/**
 * 
 * Help us to simply deal with HTTP requests
 * 
 * @author wsiqueir
 *
 */
public class ClientRequestUtil {

	private static ClientExecutor executor;

	static {
		String usr = (String) Navigation.getInstance().getData()
				.get(Param.USER);
		String psw = (String) Navigation.getInstance().getData()
				.get(Param.PASSWORD);
		DefaultHttpClient httpclient = new DefaultHttpClient();
		httpclient.getCredentialsProvider().setCredentials(AuthScope.ANY,
				new UsernamePasswordCredentials(usr, psw));
		executor = new ApacheHttpClient4Executor(httpclient);
	}

	private ClientRequestUtil() {

	}

	// TODO: extract to a factory class
	public static ClientRequest create(String... pathFragments) {
		String location = KieServerClientManager.getInstance().getServerInfo()
				.getLocation();

		UriBuilder builder = UriBuilder.fromUri(location);
		for (String p : pathFragments) {
			builder = builder.path(p);
		}
		System.out.println(builder.build("TEST").toString());
		return new ClientRequest(builder, executor);
	}

}
