package aqmaps;

import java.io.IOException;
import java.util.*;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;

public class Path {
	String port, year, month, date;
	String baseurl = "http://localhost:";
	double x, y;

	List<double,double> options = new ArrayList<>();
	
	public Path(String port, String year, String month, String date, String x, String y) {
		this.port = port;
		this.year = year;
		this.month = month;
		this.date = date;
		this.x = Double.parseDouble(x);
		this.y = Double.parseDouble(y);
	}

	public List<LineString> findPath() throws IOException, InterruptedException {
		List<Point> visited = new ArrayList<Point>();
		List<Point> singlePath = new ArrayList<Point>(2);
		Point current = Point.fromLngLat(x, y);
		visited.add(current);
		var loadData = new LoadData(port, year, month, date);
		List<Point> sensorLocations = loadData.getCoordinates();
		double[] sensorBatteriers = loadData.getBatteries();
		String[] sensorReadings = loadData.getReadings();
		Polygon[] noFlyZones = loadData.getNoFlyZones();
		
		visited.forEach((v)->{
			singlePath.add(current);
			sensorLocations.forEach((p)->{
				
			});
		});
		
		return null;
	}
}
