package uk.ac.ed.inf.aqmaps;

import java.awt.geom.Line2D;
import java.io.IOException;
import java.util.*;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;

public class FindPath {
	String port, year, month, date;
	String baseurl = "http://localhost:";
	Point drone;
	double[][] directions = new double[36][2];

	public FindPath(String port, String year, String month, String date, String y, String x) {
		this.port = port;
		this.year = year;
		this.month = month;
		this.date = date;
		this.drone = Point.fromLngLat(Double.parseDouble(x), Double.parseDouble(y));
	}

	public List<Path> findPath() throws IOException, InterruptedException {
		var loadData = new LoadData(port, year, month, date);
		List<Point> sensorLocations = loadData.getCoordinates();
		Polygon[] noFlyZones = loadData.getNoFlyZones();
		List<Point> route = new ArrayList<>();
		route.add(drone);
		route.addAll(sensorLocations);
		route.add(drone);
		
		var twoOpt = new TwoOpt(route);
		List<Path> results = twoOpt.algorithm(noFlyZones);
		
		return results;
	}


}
