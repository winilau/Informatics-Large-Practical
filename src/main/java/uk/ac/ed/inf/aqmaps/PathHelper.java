package uk.ac.ed.inf.aqmaps;

import java.awt.geom.Line2D;
import java.util.*;

import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;

public class PathHelper {
	double[][] directions = new double[36][2];
	Polygon[] noFlyZones;
	
	public PathHelper(Polygon[] noFlyZones){
		this.noFlyZones = noFlyZones;
		for (int i = 0; i < 36; i++) {
			double angle = i * 10.0;
			this.directions[i][0] = 0.0003 * Math.cos(Math.toRadians(angle));
			this.directions[i][1] = 0.0003 * Math.sin(Math.toRadians(angle));
		}
	}
	
	public List<Path> findSteps(Point start, Point end) {
		List<Path> results = new ArrayList<Path>();
		double degree = 0;
		Point best = Point.fromLngLat(start.longitude() + directions[0][0], start.latitude() + directions[0][1]);
		Point current = start;
		if (findLength(best, end) < 0.0002 && !inNoFlyZone(start, best)) {
			List<Point> points = Arrays.asList(start, best);
			Path temp = new Path(LineString.fromLngLats(points), degree, end);
			results.add(temp);
			return results;
		}
		List<LineString> visited = new ArrayList<>();
		while (findLength(best, end) >= 0.0002 && visited.size() != 33) {
			List<Double> degrees = new ArrayList<>();
			List<Point> valid = new ArrayList<>();
			for (int i = 1; i < 36; i++) {
				Point currentOption = Point.fromLngLat(current.longitude() + directions[i][0],
						current.latitude() + directions[i][1]);
				if (!inNoFlyZone(current, currentOption)) {
					valid.add(currentOption);
					degrees.add(i * 10.0);
				}
			}
			best = valid.get(0);
			for (int i = 0; i < valid.size(); i++) {
				Point currentOption = valid.get(i);
				List<Point> points = Arrays.asList(current,currentOption);
				if (findLength(currentOption, end) < findLength(best, end) && !visited.contains(LineString.fromLngLats(points))) {
					best = currentOption;
					degree = degrees.get(i);
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
			visited.add(LineString.fromLngLats(points));
		}
		return results;
	}
	
	public List<Path> findAllSteps(List<Point> route) {
		List<Path> results =  new ArrayList<>();
		Point start = route.get(0);
		for (int i = 1; i < route.size();i++) {
			List<Path> temp = findSteps(start,route.get(i));
			if (!temp.isEmpty()) {
				results.addAll(temp);
				start = temp.get(temp.size()-1).getEnd();
			}
		}
		return results;
		
	}

	public double findLength(Point a, Point b) {
		double x = Math.abs(a.longitude() - b.longitude());
		double y = Math.abs(a.latitude() - b.latitude());
		return Math.hypot(x, y);
	}

	public List<LineString> getAllPaths(List<Path> source) {
		List<LineString> results = new ArrayList<>();
		for (int i = 0; i < source.size(); i++) {
			results.add(source.get(i).getPath());
		}
		return results;
	}
	
	public Boolean inNoFlyZone(Point one, Point two) {
		List<LineString> noFlyStrings = new ArrayList<>();
		for (Polygon z : noFlyZones) {
			for (int i = 0; i < z.coordinates().get(0).size() - 1; i++) {
				List<Point> temp = new ArrayList<>();
				temp.add(z.coordinates().get(0).get(i));
				temp.add(z.coordinates().get(0).get(i + 1));
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

			if (Line2D.linesIntersect(x1, y1, x2, y2, x3, y3, x4, y4)) {
				return true;
			}

			if ((x1 == x3 && y1 == y3) || (x2 == x4 && y2 == y4)) {
				return true;
			}
		}
		return false;
	}

}
