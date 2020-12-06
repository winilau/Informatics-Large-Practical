package uk.ac.ed.inf.aqmaps;

public class Details {
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
