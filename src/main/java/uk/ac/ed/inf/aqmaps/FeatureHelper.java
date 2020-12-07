package uk.ac.ed.inf.aqmaps;

import java.util.*;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;

public class FeatureHelper {
	List<Path> finalPath;
	Map<Point,String> sensors;
	Polygon[] noFlyZones;
	double[] batteries;
	String[] readings;

	public FeatureHelper(List<Path> finalPath, Map<Point,String> sensors,
			Polygon[] noFlyZones, double[] batteries, String[] readings) {
		this.finalPath = finalPath;
		this.sensors = sensors;
		this.noFlyZones = noFlyZones;
		this.batteries = batteries;
		this.readings = readings;
	}

	public String getFeatureCollection() {
		var pathHelper = new PathHelper(noFlyZones);
		List<Feature> fl = new ArrayList<>();

		List<LineString> path = pathHelper.getAllPaths(finalPath);
		for (int i = 0; i < finalPath.size(); i++) {
			if (finalPath.get(i).getSensor() != null) {
				Feature point = Feature.fromGeometry(finalPath.get(i).getSensor());
				point.addStringProperty("location", sensors.get(finalPath.get(i).getSensor()));
//				point.addStringProperty("rbg-string", "");
//				point.addStringProperty("marker-color", "");
//				point.addStringProperty("maker-symbol", "");
				fl.add(point);
			}
			Feature line = Feature.fromGeometry(path.get(i));
			fl.add(line);
		}

		System.out.println(pathHelper.getAllPaths(finalPath).size());
		System.out.println(FeatureCollection.fromFeatures(fl).toJson());
		return FeatureCollection.fromFeatures(fl).toJson();
	}

	private List<String> getStringProperty(double battery, String reading) {

		return null;
	}

}
