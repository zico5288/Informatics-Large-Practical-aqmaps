package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.List;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;

/**
 * A class to define none fly areas which drone could not reach.
 */
public class Noflyzone {

	private final double[] lat1; // Contains the first polygon latitudes.
	private final double[] lng1; // Contains the first polygon longitudes.
	private final double[] lat2; // Contains the second polygon latitudes.
	private final double[] lng2; // Contains the second polygon longitudes.
	private final double[] lat3; // Contains the third polygon latitudes.
	private final double[] lng3; // Contains the third polygon longitudes.
	private final double[] lat4; // Contains the fourth polygon latitudes.
	private final double[] lng4; // Contains the fourth polygon longitudes.

	private static final double confinment_area_lat1 = 55.942617;
	private static final double confinment_area_lng1 = -3.192473; // Top of the Meadows  (55.946233, −3.192473)
	private static final double confinment_area_lat2 = 55.946233;
	private static final double confinment_area_lng2 = -3.184319; // KFC (55.946233, −3.184319)

	/**
	 * Constructor to create a new none fly zones which will store each latitude and
	 * longitude of every point from each polygon to an array.
	 * 
	 * @param port - The port number.
	 * @throws Exception a single line error to let users understand what happen.
	 */
	public Noflyzone(String port) throws Exception {
		// Get the String of no-fly-zones using the port number.
		String urlstring = "http://localhost:" + port + "/buildings/no-fly-zones.geojson";
		String http = Httprequest.getResponce(urlstring, port);
		FeatureCollection fc = FeatureCollection.fromJson(http);
		List<Feature> featurelist = new ArrayList<>();
		List<Polygon> polygonlist = new ArrayList<>();
		featurelist = fc.features();
		for (Feature feature : featurelist) {
			polygonlist.add((Polygon) feature.geometry());
		}
		// A list of points of the first polygon.
		List<Point> pointlist1 = new ArrayList<>();
		pointlist1 = polygonlist.get(0).coordinates().get(0);
		// A list of points of the second polygon.
		List<Point> pointlist2 = new ArrayList<>();
		pointlist2 = polygonlist.get(1).coordinates().get(0);
		List<Point> pointlist3 = new ArrayList<>();
		// A list of points of the third polygon.
		pointlist3 = polygonlist.get(2).coordinates().get(0);
		// A list of points of the forth polygon.
		List<Point> pointlist4 = new ArrayList<>();
		pointlist4 = polygonlist.get(3).coordinates().get(0);

		// Store latitude and longitude of each point to an array.
		double[] lat1 = getArray(pointlist1, "lat");
		double[] lng1 = getArray(pointlist1, "lng");
		this.lat1 = lat1;
		this.lng1 = lng1;
		double[] lat2 = getArray(pointlist2, "lat");
		double[] lng2 = getArray(pointlist2, "lng");
		this.lat2 = lat2;
		this.lng2 = lng2;
		double[] lat3 = getArray(pointlist3, "lat");
		double[] lng3 = getArray(pointlist3, "lng");
		this.lat3 = lat3;
		this.lng3 = lng3;
		double[] lat4 = getArray(pointlist4, "lat");
		double[] lng4 = getArray(pointlist4, "lng");
		this.lat4 = lat4;
		this.lng4 = lng4;
	}

	/**
	 * Return an array which contains either latitude or longitude of each point
	 * from a list.
	 * 
	 * @param list - A list of Point(A list of Point from a polygon in this task).
	 * @param type - either "lat" or "lng", "lat" for getting all latitude and "lng"
	 *             for getting all longitude.
	 * @return An array of double which contains either latitude or longitude from
	 *         the Point list.
	 */
	private double[] getArray(List<Point> list, String type) {
		double[] array = new double[list.size()];
		int i = 0;
		for (Point point : list) {
			if (type == "lat") {
				array[i] = point.latitude();
			}
			if (type == "lng") {
				array[i] = point.longitude();
			}
			i = i + 1;
		}
		return array;
	}

	/**
	 * Check whether the point is in the confinement area.
	 * 
	 * @param point - An point which we want to know whether is in the confinement
	 *              area.
	 * @return A boolean value (true for the point is not in the confinement area).
	 */
	public boolean notInConfinement(Point point) {
		double latitude = point.latitude();
		double longitude = point.longitude();
		if (latitude <= confinment_area_lat1 || latitude >= confinment_area_lat2 || longitude <= confinment_area_lng1
				|| longitude >= confinment_area_lng2) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Check whether the first line(l1-l2) is intersect with the second line(l3-l4).
	 * 
	 * @param lat1, lng1 - The coordinates of the start of the first line segment.
	 * @param lat2, lng2 - The coordinates of the end of the first line segment.
	 * @param lat3, lng3 - The coordinates of the start of the second line segment.
	 * @param lat4, lng4 - The coordinates of the end of the second line segment.
	 * @return A boolean value (true for two lines intersecting).
	 */
	private boolean isIntersect(double lat1, double lng1, double lat2, double lng2, double lat3, double lng3,
			double lat4, double lng4) {
		double d = (lat2 - lat1) * (lng4 - lng3) - (lng2 - lng1) * (lat4 - lat3);
		if (d != 0) {
			double r = ((lng1 - lng3) * (lat4 - lat3) - (lat1 - lat3) * (lng4 - lng3)) / d;
			double s = ((lng1 - lng3) * (lat2 - lat1) - (lat1 - lat3) * (lng2 - lng1)) / d;
			if ((r >= 0) && (r <= 1) && (s >= 0) && (s <= 1)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check whether a line is intersect with one of the lines of one polygon.
	 * 
	 * @param start            - The start point of the line segment.
	 * @param end              - The end point of the line segment.
	 * @param polygonPoint_lat - Contains all the latitude of the points from the
	 *                         polygon.
	 * @param polygonPoint_lng - Contains all the longitude of the points from the
	 *                         polygon.
	 * @return A boolean value (true for the line is not intersect with the lines of
	 *         the polygon).
	 */
	private boolean notIntersectOnePolygon(Point start, Point end, double[] polygonPoint_lat,
			double[] polygonPoint_lng) {
		// As the start point and end point of the polygon are the same, hence we just
		// need using length-1 to traverse each edge.
		for (int i = 0; i < polygonPoint_lat.length - 1; i++) {
			if (isIntersect(start.latitude(), start.longitude(), end.latitude(), end.longitude(), polygonPoint_lat[i],
					polygonPoint_lng[i], polygonPoint_lat[i + 1], polygonPoint_lng[i + 1])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Check whether a line is intersect with one of the lines of four polygons.
	 * 
	 * @param start - The start point of the line segment.
	 * @param end   - The end point of the line segment.
	 * @return A boolean value (true for the line is not intersect with the lines of
	 *         all four polygons).
	 */
	public boolean notInNoFlyZones(Point start, Point end) {
		return notIntersectOnePolygon(start, end, lat1, lng1) && notIntersectOnePolygon(start, end, lat2, lng2)
				&& notIntersectOnePolygon(start, end, lat3, lng3) && notIntersectOnePolygon(start, end, lat4, lng4);
	}

}
