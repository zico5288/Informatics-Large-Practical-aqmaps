package uk.ac.ed.inf.aqmaps;

import java.io.FileWriter;
import java.io.IOException;

import com.mapbox.geojson.FeatureCollection;

/**
 * A class in order to generate and output files. The class has three static
 * methods because we might want to use them in some other classes in the future.
 */
public class Writefile {

	/**
	 * Write the output geojson_file in the current working directory.
	 * 
	 * @param geojson - This is a map in String format which contains 33markers and
	 *                the drone flight path.
	 * @param date    - The corresponding date of this map.
	 * @param month   - The corresponding month of this map.
	 * @param year    - The corresponding year of this map.
	 * @exception a single line error to let users understand what happen.
	 */
	private static void writeGeojsonFile(String geojson, String date, String month, String year) {
		FileWriter writer;
		try {
			writer = new FileWriter("readings-" + date + "-" + month + "-" + year + ".geojson");
			writer.write(geojson);
			writer.flush();
			writer.close();
			System.out.println("Successfully wrote " + "readings-" + date + "-" + month + "-" + year + ".geojson");
		} catch (IOException e) {
			System.out.println("Failed to wrote " + "readings-" + date + "-" + month + "-" + year + ".geojson");
			e.printStackTrace();
		}
	}

	/**
	 * Write the output txt_file in the current working directory.
	 * 
	 * @param flightpath - This is the drone flight path in String type.
	 * @param date       - The corresponding date of this map.
	 * @param month      - The corresponding month of this map.
	 * @param year       - The corresponding year of this map.
	 * @exception a single line error to let users understand what happen.
	 */
	private static void writeTxtFile(String flightpath, String date, String month, String year) {
		FileWriter writer;
		try {
			writer = new FileWriter("flightpath-" + date + "-" + month + "-" + year + ".txt");
			writer.write(flightpath);
			writer.flush();
			writer.close();
			System.out.println("Successfully wrote " + "flightpath-" + date + "-" + month + "-" + year + ".txt");
		} catch (IOException e) {
			System.out.println("Failed to wrote " + "readings-" + date + "-" + month + "-" + year + ".geojson");
			e.printStackTrace();
		}
	}

	/**
	 * Write the both geojson_file and txt_file in the current working directory.
	 * 
	 * @param drone - The drone which we use.
	 * @param map   - The corresponding map which the drone flight.
	 */
	public static void outputTwoFiles(GreedyDrone drone, Map map) {
		// write txt_file
		System.out.println(drone.getStringFlightPath());
		Writefile.writeTxtFile(drone.getStringFlightPath(), map.getDate(), map.getMonth(), map.getYear());

		// write geojson_file
		map.getFeatureList().add(drone.getFeatureFlightPath());
		String result = FeatureCollection.fromFeatures(map.getFeatureList()).toJson();
		Writefile.writeGeojsonFile(result, map.getDate(), map.getMonth(), map.getYear());
	}

}
