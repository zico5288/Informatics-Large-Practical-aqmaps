package uk.ac.ed.inf.aqmaps;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;

/**
 * A class contains a static method for getting a String from an url_String on
 * the web_server.
 */
public class Httprequest {
	/**
	 * Download the information as a string from a geojson file with the specified
	 * url_String and we need to use this method many times in the app, hence it is
	 * a public static method.
	 * 
	 * @param urlstring - The string of the http link.
	 * @param port      - Run web server on this port number.
	 * @return Everything in the specific geojson file as a string.
	 * @throws IOException An error line which could let users easily understand if we cannot
	 *               download anything and exit the application.
	 */
	public static String getResponce(String urlstring, String port) throws IOException, InterruptedException {
		try {
			var client = HttpClient.newHttpClient();
			var request = HttpRequest.newBuilder().uri(URI.create(urlstring)).build();
			var response = client.send(request, BodyHandlers.ofString());
			return response.body();
		} catch (Exception e) {
			System.out.println("Fatal error: Unable to connect to " + urlstring + " at port " + port + ".");
			System.exit(1);
		}
		return null;

	}
}