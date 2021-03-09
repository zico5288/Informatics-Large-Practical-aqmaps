package uk.ac.ed.inf.aqmaps;

/**
 * A class to represent each w3word.
 */
public class W3word {

	String country; // Country of that w3words.
	String nearestPlace; // "Edinburgh".
	Square square; // The coordinates of the south_west & north_east of the square.
	Coordinate coordinates; // Contains the latitude & longitude.
	String words; // The word(e.g. xxxx.xxxx.xxxx).
	String language; // "en".
	String map; // The corresponding http_String.

	/**
	 * A Square class contains the coordinates of the southwest and northeast of a
	 * square of w3word.
	 */
	public class Square {
		Coordinate southwest;
		Coordinate northeast;
	}

	/**
	 * A Coordinate class contains the the latitude and the longitude of a point.
	 */
	public class Coordinate {
		Double lng;
		Double lat;
	}

}
