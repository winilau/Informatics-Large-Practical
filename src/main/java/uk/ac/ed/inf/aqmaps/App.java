package uk.ac.ed.inf.aqmaps;

import java.io.*;
import java.util.*;

import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;

public class App {
	public static void main(String[] args) throws IOException, InterruptedException {
		var loadData = new LoadData(args[6],  args[2], args[1], args[0], args[3], args[4]);
		List<Point> sensorLocations = loadData.getCoordinates();
		Polygon[] noFlyZones = loadData.getNoFlyZones();
		
		List<Point> route = new ArrayList<>();
		route.add(loadData.drone);
		route.addAll(sensorLocations);
		route.add(loadData.drone);
		var twoOpt = new TwoOpt(route);
		List<Path> finalPath = twoOpt.algorithm(noFlyZones);
		
		var featureHelper = new FeatureHelper(finalPath,loadData);
		String geoJson = featureHelper.getFeatureCollection();
		new WriteFiles(finalPath, geoJson, loadData);
	}
}
