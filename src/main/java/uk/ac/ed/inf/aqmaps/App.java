package uk.ac.ed.inf.aqmaps;

import java.io.*;
import java.util.*;

import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;

public class App {
	public static void main(String[] args) throws IOException, InterruptedException {
		var loadData = new LoadData(args[6],  args[2], args[1], args[0]);
		List<Point> sensorLocations = loadData.getCoordinates();
		List<String> what3words = loadData.getWhat3Words();
		double[] sensorBatteriers = loadData.getBatteries();
		String[] sensorReadings = loadData.getReadings();
		Polygon[] noFlyZones = loadData.getNoFlyZones();
		var pathHelper = new FindPath(args[6], args[2], args[1], args[0], args[3], args[4]);
		List<Path> finalPath = pathHelper.findPath();
		var writeFiles = new WriteFiles(finalPath,sensorLocations, what3words, noFlyZones, sensorBatteriers, sensorReadings);
		
	}
}
