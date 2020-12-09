package uk.ac.ed.inf.aqmaps;

import java.io.*;
import java.util.*;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;

public class App {
	public static void main(String[] args) throws IOException, InterruptedException {
		LoadData loadData = new LoadData(args[6], args[2], args[1], args[0], args[3], args[4]);
		

		final Data data = new Data(loadData.year, loadData.month, loadData.date, loadData.drone, loadData.getSensors(),
				loadData.getCoordinates(), loadData.getSensorInfo(), loadData.getNoFlyZones());
		
		PathHelper pathHelper = new PathHelper(data);

		var twoOpt = new TwoOpt(data.startRoute, pathHelper);
		List<Point> finalRoute = twoOpt.findBestRoute();
		List<Path> finalPath = pathHelper.findAllSteps(finalRoute);
		
		List<Feature> fl = new ArrayList<>();
		finalRoute.forEach(f->{
			fl.add(Feature.fromGeometry(f));
		});
		System.out.println(FeatureCollection.fromFeatures(fl).toJson());
		new WriteFiles(finalPath, finalRoute, data, pathHelper);
	}
}
