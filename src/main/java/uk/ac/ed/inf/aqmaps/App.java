package uk.ac.ed.inf.aqmaps;

import java.io.*;
import java.util.*;

import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;

public class App {
	public static void main(String[] args) throws IOException, InterruptedException {
		var loadData = new LoadData(args[6],  args[2], args[1], args[0]);
		Map<Point,String> sensorLocations = loadData.getSensors();
		Map<String,List<String>> sensorInfo = loadData.getSensorInfo();
		Polygon[] noFlyZones = loadData.getNoFlyZones();
		var pathHelper = new FindPath(args[6], args[2], args[1], args[0], args[3], args[4]);
		List<Path> finalPath = pathHelper.findPath();
		//var writeFiles = new WriteFiles(finalPath,sensorLocations, what3words, noFlyZones, sensorBatteriers, sensorReadings);
		var featureHelper = new FeatureHelper(finalPath,sensorLocations, noFlyZones, sensorInfo);
		featureHelper.getFeatureCollection();
	}
}
