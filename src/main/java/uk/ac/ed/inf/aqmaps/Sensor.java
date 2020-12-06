package uk.ac.ed.inf.aqmaps;

import com.mapbox.geojson.Point;

public class Sensor {
	Point location;
	String what3words;
	
	public Sensor(Point location, String what3words) {
		this.location = location;
		this.what3words = what3words;
	}

	public Point getLocation() {
		return location;
	}

	public String getWhat3words() {
		return what3words;
	}
	
}
