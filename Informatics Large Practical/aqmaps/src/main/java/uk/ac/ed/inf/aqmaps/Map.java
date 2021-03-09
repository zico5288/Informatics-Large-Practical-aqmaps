package uk.ac.ed.inf.aqmaps;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;

/**
 * A class to represent all the sensors of a map in a specification date.
 * 
 * Each map has port , year, month, date to identify. sensorList - A list of
 * sensor which contains each sensors in this map. locations - A list of Points
 * which contains each sensor's location. w3words - A list of Strings which
 * contains each w3words. battery - A list of Double which contains each
 * sensors' battery. featureList - A list of Feature which contains each
 * sensor(type in Feature).
 */
public class Map {

	private final String port;
	private final String year;
	private final String month;
	private final String date;

	private ArrayList<Sensor> sensorlist = new ArrayList<>();
	private final List<Point> locations = new ArrayList<>();
	private final List<String> w3words = new ArrayList<>();
	private final List<Double> battery = new ArrayList<>();
	
	private final List<Feature> featurelist = new ArrayList<>();

	private FeatureCollection fc; // A FeatureCollection which contains all information of this map.

	/**
	 * Create a new map which contains all the information of the air-quality-data
	 * of that day.
	 * 
	 * @param port  - The port number.
	 * @param year  - The year of the map.
	 * @param month - The month of the map.
	 * @param date  - The date of the map.
	 * @throws Exception a single line error to let users understand what happen.
	 */
	public Map(String port, String year, String month, String date) throws Exception {
		this.port = port;
		this.year = year;
		this.month = month;
		this.date = date;
		// Get the String of corresponding air-quality-data.
		String http = "http://localhost:" + port + "/maps/" + year + "/" + month + "/" + date
				+ "/air-quality-data.json";
		String map = Httprequest.getResponce(http, port);
		Type SensorlistType = new TypeToken<ArrayList<Sensor>>() {
		}.getType();
		// If could not create a sensor_list, print an error line which could let users
		// easily understand and exit the application.
		try {
			this.sensorlist = new Gson().fromJson(map, SensorlistType);
		} catch (Exception e) {
			System.out.println("unable to find the corresponding file (port: " + port + ", year: " + year + ", month: "
					+ month + ", date: " + date + ") on the webserver. Please check for input errors.");
			System.exit(1);
		}
		// Initialise the visited status of each sensor.
		for (Sensor sensor : sensorlist) {
			// Set port number.
			sensor.setPort(port);
			// Set the location point of the sensor.
			sensor.setLatLng();
			// Set RGBstring of the sensor.
			sensor.setRGBstring();
			// Add sensor(type in feature) to the feature_list.
			this.featurelist.add(sensor.toFeature());

			// Add three properties to corresponding list.
			this.locations.add(sensor.point);
			this.w3words.add(sensor.location);
			this.battery.add(sensor.battery);
		}
		// FeatureCollection which contains all information of this map.
		this.fc = FeatureCollection.fromFeatures(featurelist);

	}

	/**
	 * Getter of the private variable.
	 * 
	 * @return A list of the points which contains every location of sensors in the
	 *         map.
	 */
	public List<Point> getLocations() {
		return locations;
	}

	/**
	 * Getter of the private variable.
	 * 
	 * @return A list of Strings which contains every w3words in the map.
	 */
	public List<String> getW3words() {
		return w3words;
	}
	/**
	 * @return A list of Feature.
	 */
	public List<Feature> getFeatureList(){
		return featurelist;
	}
	/**
	 * @return FeatureCollection.
	 */
	public FeatureCollection getFeatureCollection() {
		return fc;
	}
	
	/**
	 * @return the port
	 */
	public String getPort() {
		return port;
	}

	/**
	 * @return the year
	 */
	public String getYear() {
		return year;
	}

	/**
	 * @return the month
	 */
	public String getMonth() {
		return month;
	}

	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}

}
