package uk.ac.ed.inf.aqmaps;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;

public class Data {
	String year, month, date;
	Point drone;
	Map<Point, String> sensors;
	List<Point> sensorLocations;
	List<Point> startRoute = new ArrayList<>();
	Map<String, List<String>> sensorsInfo;
	Polygon[] noFlyZones;

	public Data(String year, String month, String date, Point drone, Map<Point, String> sensors,
			List<Point> sensorLocations, Map<String, List<String>> sensorInfo, Polygon[] noFlyZones) {
		
		this.year = year;
		this.month = month;
		this.date = date;
		this.drone = drone;
		this.sensors = sensors;
		this.sensorLocations = sensorLocations;
		this.sensorsInfo = sensorInfo;
		this.noFlyZones = noFlyZones;
		
		startRoute.add(drone);
		startRoute.addAll(sensorLocations);
		startRoute.add(drone);
	}
}
