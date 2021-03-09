package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;

import java.util.List;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

/**
 * A class to represent a drone to store some information of the basic
 * drone(there is no algorithm in this class). The super class for the
 * Dronegreedy(Apply Greedy algorithm on the basic drone) because they have many
 * identical features and methods. Hence next time we want to change another
 * algorithm, there is no need to change this class. This class is simple and
 * clean.
 *
 */
public class Drone {

	// Two same degree(0 == 360)
	protected static final int maxdegree = 360; //(max is form 0-350, and 0 ==360). 
	protected static final int mindegree = 0;
	// Drone can only be sent in a direction which is a multiple of ten degrees.
	protected static final int addegree = 10;
	// One move is a straight line of length 0.0003 degrees.
	protected static final double steplength = 0.0003;
	// Drone can read within 0.0002 degrees of a air quality sensor.
	protected static final double readsensordistance = 0.0002;
	// Maximum step of one flight is 150.
	protected static final int maxstep = 150;

	// The ordered output all route listed in points.
	protected final List<Point> route = new ArrayList<>();
	// The ordered degree_list which the drone made(each from 0-350).
	protected final List<Integer> degreelist = new ArrayList<>();
	// The ordered output all sensors(null for no sensors reading at that step).
	protected final List<String> outputwords = new ArrayList<>();
	protected final Point startpoint; // the drone start point.
	protected Point nowlocation; // The drone current location.
	protected int step = 0; // Initialise number of step to 0(maximum 150).
	private String flightpath = ""; // Final output flightPath in String type.
	private Feature featurepath; // Final output route in feature type.
	
	/**
	 * Constructor to create a new drone instance and sets the its current location
	 * to the start point.
	 * 
	 * @param startpoint - The start point of the drone.
	 * @param map        - The map which the drone will play for.
	 * @throws Exception a single line error to let users understand what happen.
	 */
	public Drone(Point startpoint, Map map) throws Exception {
		this.startpoint = startpoint;
		this.nowlocation = startpoint;
	}

	/**
	 * Return the next point while rotate below degree(Will not change the drone
	 * current position).
	 * 
	 * @param degree - the number of degree to let drone rotate (0 degree to east).
	 * @return A next location while rotate the number of degree.
	 */
	protected Point getNextLocation(int degree) {
		double latitude = nowlocation.latitude() + Math.sin(Math.toRadians(degree)) * steplength;
		double longitude = nowlocation.longitude() + Math.cos(Math.toRadians(degree)) * steplength;
		return Point.fromLngLat(longitude, latitude); // return the Point
	}

	/**
	 * Return the next point while rotate below degree and change the drone current
	 * position to the return point.
	 * 
	 * @param degree - the number of degree to let drone rotate (0 degree to east).
	 * @return A next location while rotate the number of degree.
	 */
	protected Point nextLocation(int degree) {
		double latitude = nowlocation.latitude() + Math.sin(Math.toRadians(degree)) * steplength;
		double longitude = nowlocation.longitude() + Math.cos(Math.toRadians(degree)) * steplength;
		this.nowlocation = Point.fromLngLat(longitude, latitude); // change the current position.
		return nowlocation;
	}

	/**
	 * Check whether the drone could reach that point by given a Noflyzone.
	 * 
	 * @param point      - The point we want to check.
	 * @param noflyzones - The none fly zones.
	 * @return A boolean value(True for can reach).
	 */
	protected boolean checkLocation(Point point, Noflyzone noflyzone) {
		// The drone will also not go to the same point twice in order to prevent to
		// stuck in a loop.
		if (noflyzone.notInConfinement(point) == false || noflyzone.notInNoFlyZones(point, nowlocation) == false
				|| route.contains(point)) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Calculate the Euclidean_distance between point A and B.
	 * 
	 * @param A - Point A.
	 * @param B - Point B.
	 * @return A double which is the Euclidean_distance between point A and B.
	 */
	protected double distance(Point A, Point B) {
		return Math.sqrt(Math.pow(A.latitude() - B.latitude(), 2) + Math.pow(A.longitude() - B.longitude(), 2));
	}

	/**
	 * Return an int_array which contains whether the drone can read from any
	 * sensor.
	 * 
	 * @param sensorlocation - The list of Point contains each sensor location
	 *                       point.
	 * @return An int_array contains the numbers show whether the drone can read
	 *         from any sensor(1 for readable and 0 for unreadable).
	 */
	protected int[] checkReadSensor(List<Point> sensorlocation) {
		double[] distancearray = new double[sensorlocation.size()];
		// calculate the distance between drone and each sensor.
		for (int i = 0; i < sensorlocation.size(); i++) {
			Point sensorpoint = sensorlocation.get(i);
			double distance = distance(nowlocation, sensorpoint);
			distancearray[i] = distance;
		}
		int[] distancearray1 = new int[sensorlocation.size()];
		for (int i = 0; i < distancearray.length; i++) {
			if (distancearray[i] < readsensordistance) { // whether the distance between drone and that sensor is
															// within 0.0002.
				distancearray1[i] = 1; // can read.
			} else {
				distancearray1[i] = 0; // can not read.
			}
		}
		return distancearray1;
	}

	/**
	 * Convert the flightPath to String and store it.
	 */
	protected void setStringFlightPath() {
		int a = step;
		System.out.println("step = " + a); // output the total number of step.
		ArrayList<Integer> step = new ArrayList<>();
		for (int i = 0; i < outputwords.size(); i++) {
			step.add(i + 1);
		}
		for (int i = 0; i < outputwords.size(); i++) {
			this.flightpath = flightpath + step.get(i) + "," + route.get(i).longitude() + "," + route.get(i).latitude()
					+ "," + degreelist.get(i) + "," + route.get(i + 1).longitude() + "," + route.get(i + 1).latitude()
					+ "," + outputwords.get(i) + "\n";
		}

	}

	/**
	 * Convert the route to feature and store it.
	 */
	protected void setFeatureFlightPath() {
		LineString linestring = LineString.fromLngLats(route);
		this.featurepath = Feature.fromGeometry(linestring);
	}
	
	/**
	 * @return step
	 */
	protected int getStep() {
		return step;
	}
	/**
	 * @return Feature of route.
	 */
	protected Feature getFeatureFlightPath() {
		return featurepath;
	}
	/**
	 * @return The flight_path in string.
	 */
	protected String getStringFlightPath() {
		return flightpath;
	}

}
