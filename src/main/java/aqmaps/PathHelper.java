package aqmaps;

import java.io.IOException;
import java.util.*;
import java.lang.*;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;

public class PathHelper {
	String port, year, month, date;
	String baseurl = "http://localhost:";
	Point drone;
	double[][] directions = new double[36][2];

	public PathHelper(String port, String year, String month, String date, String y, String x) {
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

	public List<Path> findPath() throws IOException, InterruptedException {
		var loadData = new LoadData(port, year, month, date);
		List<Point> sensorLocations = loadData.getCoordinates();
		double[] sensorBatteriers = loadData.getBatteries();
		String[] sensorReadings = loadData.getReadings();
		Polygon[] noFlyZones = loadData.getNoFlyZones();
		ArrayList<Point> visited = new ArrayList<>();
		visited.add(drone);
		List<Point> remaining = new ArrayList<>();
		remaining.addAll(sensorLocations);
		List<Path> results = new ArrayList<>();

		while (remaining.size() > 0) {
			List<Path> best = findSteps(visited.get(visited.size()-1), remaining.get(0),noFlyZones);
			for (int i = 0; i < remaining.size(); i++) {
				List<Path> current = findSteps(visited.get(visited.size()-1), remaining.get(i),noFlyZones);
				if (current.size() < best.size() || best.size() == 0) {
					best = current;
				}
			}
			remaining.remove(best.get(best.size()-1).getSensor());
			visited.add(best.get(best.size()-1).getEnd());
			results.addAll(best);
		}
		
		List<Path> wrap = findSteps(results.get(results.size()-1).getEnd(),drone, noFlyZones);
		results.addAll(wrap);
		
		List<Feature> fl = new ArrayList<>();
		sensorLocations.forEach(s -> {
			fl.add(Feature.fromGeometry(s));
		});
		for (Polygon p : noFlyZones) {
			fl.add(Feature.fromGeometry(p));
		}
		
		getAllPaths(results).forEach(k -> {
			fl.add(Feature.fromGeometry(k));
		});
		
		System.out.println(getAllPaths(results).size());
		System.out.print(FeatureCollection.fromFeatures(fl).toJson());
		return null;
	}

	private List<Path> findSteps(Point start, Point end, Polygon[] noFlyZones) {
		List<Path> results = new ArrayList<Path>();
		double degree = 0;
		Point best = Point.fromLngLat(start.longitude() + directions[0][0], start.latitude() + directions[0][1]);
		Point current = start;
		if (findLength(best, end) < 0.0002 && inNoFlyZone(noFlyZones, best, end)) {
			List<Point> points = Arrays.asList(start, best);
			Path temp = new Path(LineString.fromLngLats(points), degree, end);
			results.add(temp);
			return results;
		}
		while (findLength(best, end) >= 0.0002) {
			for (int i = 1; i < 36; i++) {
				Point currentOption = Point.fromLngLat(current.longitude() + directions[i][0],
						current.latitude() + directions[i][1]);
				if (findLength(currentOption, end) < findLength(best, end) && inNoFlyZone(noFlyZones, currentOption, end)) {
					best = currentOption;
					degree = i * 10.0;
				}
			}
			List<Point> points = Arrays.asList(current, best);
			
			Point sensor = null;
			if (findLength(best, end) < 0.0002) {
				sensor = end;
			}
			
			Path temp = new Path(LineString.fromLngLats(points), degree, sensor);
			results.add(temp);
			current = best;
		}
		return results;
	}

	private double findLength(Point a, Point b) {
		double x = Math.abs(a.longitude() - b.longitude());
		double y = Math.abs(a.latitude() - b.latitude());
		return Math.hypot(x, y);
	}

	private List<LineString> getAllPaths(List<Path> source) {
		List<LineString> results = new ArrayList<>();
		for (int i = 0; i < source.size(); i++) {
			results.add(source.get(i).getPath());
		}
		return results;
	}
	
	private Boolean inNoFlyZone(Polygon[] zones, Point one, Point two) {
		List<LineString> noFlyStrings = new ArrayList<>();
		for (Polygon z:zones) {
			for (int i = 0; i < z.coordinates().get(0).size()-1;i++) {
				List<Point> temp = new ArrayList<>();
				temp.add(z.coordinates().get(0).get(i));
				temp.add(z.coordinates().get(0).get(i+1));
				noFlyStrings.add(LineString.fromLngLats(temp));
			}
		}
		
		double x1 = one.longitude();
		double y1 = one.latitude();
		double x2 = two.longitude();
		double y2 = two.latitude();
		
		for (int i = 0; i < noFlyStrings.size(); i++) {
			double x3 = noFlyStrings.get(i).coordinates().get(0).longitude();
			double y3 = noFlyStrings.get(i).coordinates().get(0).latitude();
			double x4 = noFlyStrings.get(i).coordinates().get(1).longitude();
			double y4 = noFlyStrings.get(i).coordinates().get(1).latitude();
			
			if (Math.max(x1, x2) < Math.min(x3, x4)) {
				return false;
			}
			
			double a1 = (y1-y2)/(x1-x2);  //Pay attention to not dividing by zero
			double a2 = (y3-y4)/(x3-x4); //Pay attention to not dividing by zero
			double b1 = y1-a1*x1;
			double b2 = y3-a2*x3;
			
			double xa = (b2 - b1) / (a1 - a2);   //Once again, pay attention to not dividing by zero
			
			if (a1 == a2 && b1 != b2) {
				return false; //parallel
			}
			
			if ((xa < Math.max( Math.min(x1,x2), Math.min(x3,x4))) ||
				     (xa > Math.min( Math.max(x1,x2), Math.max(x3,x4) )) ) {
				return false; //intersection is out of bound
			}
			System.out.println(i);
		}
		return true;
	}
}
