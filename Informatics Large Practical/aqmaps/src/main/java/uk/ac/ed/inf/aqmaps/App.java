package uk.ac.ed.inf.aqmaps;

import com.mapbox.geojson.Point;

/**
 * Main class for the App.
 */
public class App {

	/**
	 * Read and parse all arguments from the input and pass them to new Map and
	 * Drone class. Create three drones and corresponding map using different type
	 * of Greedy to find the route, and choose to use the drone which has the
	 * minimum step.
	 * 
	 * @param args - 6 input arguments.
	 * @throws Exception a single line error to let users understand what happen.
	 */
	public static void main(String[] args) throws Exception {
		String date = args[0];
		String month = args[1];
		String year = args[2];
		String latitude = args[3];
		String longitude = args[4];
		String randomseed = args[5];// Random seed.
		String port = args[6];
		// The above 6 arguments will create a httpString to get the air-quality-data of
		// that day.

		Error.showUsersError(date, month, year, latitude, longitude, randomseed, port);
		// Convert latitude & longitude to a point.
		Point startpoint = Point.fromLngLat(Double.parseDouble(longitude), Double.parseDouble(latitude));
		// Create three new map and three new drone using Greedy with different
		// algorithms.
		Map map1 = new Map(port, year, month, date); // Create a new map.
		GreedyDrone drone1 = new GreedyDrone(startpoint, map1, "add");
		Map map2 = new Map(port, year, month, date);
		GreedyDrone drone2 = new GreedyDrone(startpoint, map2, "minus");
		Map map3 = new Map(port, year, month, date);
		GreedyDrone drone3 = new GreedyDrone(startpoint, map3, "first");

		if (drone1.getStep() <= drone2.getStep() && drone1.getStep() <= drone3.getStep()) { // Drone1 step is the minimum among the three
																		// drones step.
			Writefile.outputTwoFiles(drone1, map1);
		} else if (drone2.getStep() <= drone1.getStep() && drone2.getStep() <= drone3.getStep()) { // Drone2 step is the minimum among the
																				// three drones step.
			Writefile.outputTwoFiles(drone2, map2);
		} else { // Drone3 step is the minimum among the three drones step.
			Writefile.outputTwoFiles(drone3, map3);
		}

	}

}
