package uk.ac.ed.inf.aqmaps;

import java.io.*;
import java.util.*;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;

public class App {
	public static void main(String[] args) throws IOException, InterruptedException {
		//load data from the webserver
		LoadData loadData = new LoadData(args[6], args[2], args[1], args[0], args[3], args[4]);
		
		//store the data into the Data class for easy access
		final Data data = new Data(loadData.year, loadData.month, loadData.date, loadData.drone, loadData.getSensors(),
				loadData.getCoordinates(), loadData.getSensorInfo(), loadData.getNoFlyZones());
		
		PathHelper pathHelper = new PathHelper(data);

		//compute the finalPath with a twoOpt algorithm
		var twoOpt = new TwoOpt(data.startRoute, pathHelper);
		List<Path> finalPath = twoOpt.findBestRoute();
		
		//create and write the files
		new WriteFiles(finalPath, data, pathHelper);
	}
}
