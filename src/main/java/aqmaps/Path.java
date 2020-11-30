package aqmaps;

import java.io.IOException;
import java.util.*;
import java.lang.*;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;

public class Path {
	String port, year, month, date;
	String baseurl = "http://localhost:";
	Point drone;
	double[][] directions = new double[36][2];

	public Path(String port, String year, String month, String date, String x, String y) {
		this.port = port;
		this.year = year;
		this.month = month;
		this.date = date;
		this.drone = Point.fromLngLat(Double.parseDouble(x), Double.parseDouble(y));
		for (int i = 0; i < 36; i++) {
			double angle = i * 10.0;
			this.directions[i][0] = 0.0003 * Math.cos(Math.toRadians(angle));
			this.directions[i][1] = 0.0003 * Math.sin(Math.toRadians(angle));
		}
	}

	public List<LineString> findPath() throws IOException, InterruptedException {
		List<Point> visited = new ArrayList<Point>();
		List<Point> singlePath = new ArrayList<Point>(2);
		Point current = drone;
		visited.add(current);
		var loadData = new LoadData(port, year, month, date);
		List<Point> sensorLocations = loadData.getCoordinates();
		double[] sensorBatteriers = loadData.getBatteries();
		String[] sensorReadings = loadData.getReadings();
		Polygon[] noFlyZones = loadData.getNoFlyZones();

		Map<LineString, String> results = new HashMap<>();
		List<Point> locations = new ArrayList<>();
		locations.add(drone);
		locations.addAll(sensorLocations);
		locations.add(drone);
		for (int i = 0; i < locations.size()-1;i++) {
			results.putAll(findSteps(locations.get(i),locations.get(i+1)));
		}
		
		
		Set<LineString> keys = results.keySet();
		List<Feature> fl = new ArrayList<>();
		keys.forEach(k ->{
			fl.add(Feature.fromGeometry(k));
		});
		sensorLocations.forEach(s->{
			fl.add(Feature.fromGeometry(s));
		});
		for (Polygon p : noFlyZones) {
			fl.add(Feature.fromGeometry(p));
		}
		System.out.print(FeatureCollection.fromFeatures(fl).toJson());
		return null;
	}

	private Map<LineString, String> findSteps(Point start, Point end) {
		Map<LineString, String> results = new HashMap<>();
		double degree = 0;
		Point best = Point.fromLngLat(start.longitude() + directions[0][0], start.latitude() + directions[0][1]);
		Point current = start;
		while (findLength(best, end) > 0.0002) {
			for (int i = 1; i < 36; i++) {
				Point currentOption = Point.fromLngLat(current.longitude() + directions[i][0],
						current.latitude() + directions[i][1]);
				if (findLength(currentOption, end) < findLength(best, end)) {
					best = currentOption;
					degree = i * 10.0;
				}
			}
			List<Point> points = Arrays.asList(current, best);
			results.put(LineString.fromLngLats(points), Double.toString(degree));
			current = best;
		}
		return results;
	}

	private double findLength(Point a, Point b) {
		double x = Math.abs(a.longitude() - b.longitude());
		double y = Math.abs(a.latitude() - b.latitude());
		return Math.hypot(x, y);
	}
}
