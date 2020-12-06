package uk.ac.ed.inf.aqmaps;
import java.util.*;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;

public class WriteFiles {
	public WriteFiles(List<Path> finalPath, List<Point> sensorLocations, List<String> what3words, Polygon[] noFlyZones) {
		var pathHelper = new PathHelper(noFlyZones);
		List<Feature> fl = new ArrayList<>();
		sensorLocations.forEach(s -> {
			fl.add(Feature.fromGeometry(s));
		});
			
		pathHelper.getAllPaths(finalPath).forEach(k -> {
			fl.add(Feature.fromGeometry(k));
		});

		System.out.println(pathHelper.getAllPaths(finalPath).size());
		System.out.println(FeatureCollection.fromFeatures(fl).toJson());
	
	}
}
