package uk.ac.ed.inf.aqmaps;

/**
 * This class is used to extract and access information from the words folder in
 * the webserver.
 * 
 * @see LoadData.
 */

public class Words {
	String country;
	Square square;
	String nearestPlace;
	Coordinates coordinates;
	String words;
	String language;
	String map;

	public static class Square {
		Coordinates southwest;
		Coordinates northeast;
	}

	public static class Coordinates {
		double lng;
		double lat;
	}
}
