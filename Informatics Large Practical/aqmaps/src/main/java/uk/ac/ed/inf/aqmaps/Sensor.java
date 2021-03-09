package uk.ac.ed.inf.aqmaps;

import com.google.gson.Gson;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;

/**
 * A class to represent each sensor.
 */
public class Sensor {

	String location; // The location of the sensor(in w3words form).
	Double battery; // Battery of the sensor.
	String reading; // The air quality reading of the sensor.

	public String rgbstring;
	public String markersymbol;
	public Point point; // The coordinates of the sensor.

	private String port; // The port number.

	/**
	 * Set the coordinates of the sensor from its corresponding w3words.
	 * @throws Exception a single line error to let users understand what happen.
	 */
	public void setLatLng() throws Exception {
		String[] splitwords = location.split("\\.");
		String httpstring = "http://localhost:" + port + "/words/" + splitwords[0] + "/" + splitwords[1] + "/"
				+ splitwords[2] + "/details.json";
		String http = Httprequest.getResponce(httpstring, port);
		W3word w3word = new Gson().fromJson(http, W3word.class);
		this.point = Point.fromLngLat(w3word.coordinates.lng, w3word.coordinates.lat);
	}

	/**
	 * Set the RGBstring and Marker_symbol.
	 */
	public void setRGBstring() {
		this.rgbstring = "#aaaaaa"; // Not visited.
		this.markersymbol = "no symbol";
		// Readings are NaN or null.
		if (reading.equals("NaN") || reading.equals("null")) {
			this.rgbstring = "#000000"; // Black.
			this.markersymbol = "cross";
		} else {
			Double reading1 = Double.parseDouble(reading);
			if (0 <= reading1 && reading1 < 32) {
				this.rgbstring = "#00ff00";// Green.
				this.markersymbol = "lighthouse";
			}
			if (32 <= reading1 && reading1 < 64) {
				this.rgbstring = "#40ff00";// Medium Green.
				this.markersymbol = "lighthouse";
			}
			if (64 <= reading1 && reading1 < 96) {
				this.rgbstring = "#80ff00";// Light Green.
				this.markersymbol = "lighthouse";
			}
			if (96 <= reading1 && reading1 < 128) {
				this.rgbstring = "#c0ff00";// Lime Green.
				this.markersymbol = "lighthouse";
			}
			if (128 <= reading1 && reading1 < 160) {
				this.rgbstring = "#ffc000";// Gold.
				this.markersymbol = "danger";
			}
			if (160 <= reading1 && reading1 < 192) {
				this.rgbstring = "#ff8000";// Orange.
				this.markersymbol = "danger";
			}
			if (192 <= reading1 && reading1 < 224) {
				this.rgbstring = "#ff4000";// Red/Orange.
				this.markersymbol = "danger";
			}
			if (224 <= reading1 && reading1 < 256) {
				this.rgbstring = "#ff0000";// Red.
				this.markersymbol = "danger";
			}
			// If battery is less than 10, the readings are not trust_able.
			if (battery < 10) {
				this.rgbstring = "#000000";
				this.markersymbol = "cross";
			}
		}

	}

	/**
	 * Let sensor become a feature and return it.
	 * 
	 * @return A feature of the sensor which contains four properties.
	 */
	public Feature toFeature() {
		Feature feature = Feature.fromGeometry(point);
		// add four properties.
		feature.addStringProperty("location", location);
		feature.addStringProperty("rgb-string", rgbstring);
		feature.addStringProperty("marker-color", rgbstring);
		feature.addStringProperty("marker-symbol", markersymbol);
		return feature;
	}

	/**
	 * A setter of setting port number.
	 * 
	 * @param port - the port number.
	 */
	public void setPort(String port) {
		this.port = port;

	}

}
