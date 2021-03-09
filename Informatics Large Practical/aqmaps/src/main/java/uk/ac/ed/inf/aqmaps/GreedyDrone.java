package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.List;

import com.mapbox.geojson.Point;

/**
 * A GreedyDrone class extends by the Drone class, GreedyDrone use the basic
 * functions in Drone and add the Greedy algorithm.
 */
public class GreedyDrone extends Drone {

	private final Noflyzone noflyzone; // none_flyzones.

	/**
	 * Constructor to create a new Drone which having Greedy methods instance by the
	 * constructor of superclass and run Greedy method.
	 * 
	 * @param startpoint - The start point of the drone.
	 * @param map        - The map which the drone will play for.
	 * @param addorminus - "first"(normal Greedy) or "add"(add degree when meet a
	 *                   no_fly_drone) or "minus"(minus degree when meet a
	 *                   no_fly_drone).
	 * @throws Exception a single line error to let users understand what happen.
	 */
	public GreedyDrone(Point startpoint, Map map, String addorminus) throws Exception {
		super(startpoint, map);
		this.noflyzone = new Noflyzone(map.getPort());
		greedy(map.getLocations(), map.getW3words(), addorminus);
	}

	/**
	 * Get the closest sensor from drone current location.
	 * 
	 * @param sensorlocation - A list of point of each sensor location.
	 * @return A point which is the location of the closest sensor.
	 */
	private Point getClosestSensor(List<Point> sensorlocation) {
		// get distances between the drone and each sensors and store distances in
		// distance_array array.
		List<Double> distancearray = new ArrayList<>();
		for (Point sensorpoint : sensorlocation) {
			double distance = distance(nowlocation, sensorpoint);
			distancearray.add(distance);
		}
		// find the shortest distance between the drone and the closest sensor.
		int index = 0;
		double min = distancearray.get(0);
		for (double distance : distancearray) {
			if (distance < min) {
				min = distance; // get the minimum distance.
				index = distancearray.indexOf(min); // get the closest sensor index.
			}
		}
		Point cloest_sensor = sensorlocation.get(index);
		return cloest_sensor;
	}

	/**
	 * Update route, nowlocation, degreelist after movement by rotating the number
	 * of degree.
	 * 
	 * @param degree - The number of degree.
	 */
	private void updateFlightInfo(int degree) {
		route.add(getNextLocation(degree)); // Add the next point to the route.
		nextLocation(degree); // Flight to that optimal point.
		degreelist.add(degree); // Add the degree to the degree_list.
	}

	/**
	 * Greedy algorithm for one step and update the following drone status.
	 * 
	 * @param sensorlocation - A list of point of each sensor location.
	 * @param addorminus     - "first"(normal Greedy) or "add"(add degree when meet
	 *                       a no_fly_drone) or "minus"(minus degree when meet a
	 *                       no_fly_drone).
	 */
	private void greedyOneStep(List<Point> sensorlocation, String addorminus) {
		Point cloest_sensor = getClosestSensor(sensorlocation);
		List<Point> rotatePointlist = new ArrayList<>();
		List<Integer> rotatedegreelist = new ArrayList<>();
		for (int i = mindegree; i < maxdegree; i += addegree) {
			if (checkLocation(getNextLocation(i), noflyzone) == true || addorminus != "first") {
				rotatePointlist.add(getNextLocation(i)); // get all possible drone locations after 1 movement
				rotatedegreelist.add(i); // get all corresponding possible drone rotate degrees(in 0-350)
			}
		}
		// store all distances between the closest sensor and the drone location after
		// each movement.
		List<Double> rotatedistance = new ArrayList<>();
		for (Point point : rotatePointlist) {
			double distance = distance(point, cloest_sensor);
			rotatedistance.add(distance);
		}
		// find the optimal rotation angle
		int rotationdegree = 0;
		double mindistance = rotatedistance.get(0);
		for (double distance : rotatedistance) {
			if (distance < mindistance) {
				mindistance = distance; // get the minimum distance.
				rotationdegree = rotatedistance.indexOf(mindistance);
			}
		}
		int degree = rotatedegreelist.get(rotationdegree);
		if (addorminus == "add" || addorminus == "minus") {
			if (checkLocation(getNextLocation(degree), noflyzone) == true
					&& noflyzone.notInConfinement(getNextLocation(degree))) {
				updateFlightInfo(degree);
			} else {
				int d = degree;
				if (addorminus == "add") {
					int degree1 = getNextAddTurningDegree(d);
					updateFlightInfo(degree1);
				}
				if (addorminus == "minus") {
					int degree2 = getNextMinusTurningDegree(d);
					updateFlightInfo(degree2);
				}
			}
		} else {
			updateFlightInfo(degree);
		}
		step += 1; // step +=1.
	}

	/**
	 * Get the next turning Degree(Minus 10 degree each time to check) for the drone
	 * in a reachable area.
	 * 
	 * @param degree - The input degree.
	 * @return An integer which is the number of degree which after this degree
	 *         rotation the drone could in a reachable area.
	 */
	private int getNextMinusTurningDegree(int degree) {
		if (degree == mindegree) {
			degree = maxdegree;
		}
		degree = degree - addegree;
		while (checkLocation(getNextLocation(degree), noflyzone) == false) {
			degree -= addegree;
			if (degree <= mindegree) {
				degree = maxdegree;
			}
		}
		return degree;
	}

	/**
	 * Get the next turning Degree(Add 10 degree each time to check) for the drone
	 * in a reachable area.
	 * 
	 * @param degree - The input degree.
	 * @return An integer which is the number of degree which after this degree
	 *         rotation the drone could in a reachable area.
	 */
	private int getNextAddTurningDegree(int degree) {
		if (degree == maxdegree) {
			degree = mindegree;
		}
		degree = degree + addegree;
		while (checkLocation(getNextLocation(degree), noflyzone) == false) {
			degree += addegree;
			if (degree >= maxdegree) {
				degree = mindegree;
			}
		}
		return degree;
	}

	/**
	 * The full Greedy algorithm and update the following drone status.
	 * 
	 * @param sensorlocation - A list of point of each sensor location.
	 * @param wordslist      - A list of String contains each corresponding w3words
	 *                       of sensorlocation.
	 * @param addorminus     - "first"(normal Greedy) or "add"(add degree when meet
	 *                       a no_fly_drone) or "minus"(minus degree when meet a
	 *                       no_fly_drone).
	 */
	public void greedy(List<Point> sensorlocation, List<String> wordslist, String addorminus) {
		route.add(startpoint); // First add the start point to the route.
		int doOnce = 0; // This variable is related to the drone returning to the starting point.
		// After read all sensors and return to the start point or the step is over 150,
		// we stop the drone.
		while (sensorlocation.size() > 0 && doOnce < 2 && step < maxstep) {
			if (addorminus == "add") {
				greedyOneStep(sensorlocation, "add"); // One move using greedy.
			} else if (addorminus == "minus") {
				greedyOneStep(sensorlocation, "minus"); // One move using greedy.
			} else {
				greedyOneStep(sensorlocation, "first");
			}
			this.outputwords.add("null"); // We add null for location of any sensor.
			int[] list = checkReadSensor(sensorlocation); // Get the list of contain(1 for readable sensor, 0 for unreadable)
			// Get the list of contain(1 for readable sensor, 0 for unreadable)
			for (int i = 0; i < list.length; i++) {
				// when the drone find one readable sensor.
				if (list[i] == 1) {
					// If there is only one sensor left for reading, we add the start point to the
					// list in order to let the drone go back to the start point.
					if (sensorlocation.size() == 1) {
						sensorlocation.add(startpoint);
						doOnce += 1; // We do this only one time because otherwise we will add the start point
										// forever.
					}
					sensorlocation.remove(i); // After reading this sensor, we remove it from the sensor_list.
					// If drone read a sensor, remove one null from the outputwords_list and add the
					// words_location.
					// Do this while the wordslist.size() = 0(there is nothing left for drone to
					// read).
					if (wordslist.size() > 0) { // we do this while
						this.outputwords.remove(outputwords.size() - 1);
						this.outputwords.add(wordslist.get(i));
						wordslist.remove(i);
					}
					break; // We break the for loop and finish one complete step.
				}
			}
		}
		setFeatureFlightPath();
		System.out.print("Greedy_" + addorminus + ": ");
		setStringFlightPath();
	}

}
