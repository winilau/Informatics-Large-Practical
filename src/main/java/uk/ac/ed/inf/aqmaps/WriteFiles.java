package uk.ac.ed.inf.aqmaps;

import java.util.*;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;

public class WriteFiles {
	public WriteFiles(List<Path> finalPath, List<Point> sensorLocations, List<String> what3words, Polygon[] noFlyZones,
			double[] batteries, String[] readings) {
		var pathHelper = new PathHelper(noFlyZones);
		List<Feature> fl = new ArrayList<>();
		sensorLocations.forEach(s -> {
			Feature point = Feature.fromGeometry(s);
//			point.addStringProperty("location", what3words.get(sensorLocations.indexOf(point)));
//			point.addStringProperty("rbg-string", "");
//			point.addStringProperty("marker-color", "");
//			point.addStringProperty("maker-symbol", "");
			fl.add(point);
		});
		
		for (Polygon p : noFlyZones) {
			Feature polygon = Feature.fromGeometry(p);
			fl.add(polygon);
		}

		List<LineString> path = pathHelper.getAllPaths(finalPath);
		for (int i = 0; i < finalPath.size(); i++) {
			Feature line = Feature.fromGeometry(path.get(i));
			fl.add(line);
		}

		System.out.println(pathHelper.getAllPaths(finalPath).size());
		System.out.println(FeatureCollection.fromFeatures(fl).toJson());

	}

	private List<String> getStringProperty(double battery, String reading) {
		
		return null;
	}
}
