package uk.ac.ed.inf.aqmaps;

import java.io.*;
import java.util.*;

public class App {
	public static void main(String[] args) throws IOException, InterruptedException {
		/**
		 * load data from the webserver
		 */
		LoadData loadData = new LoadData(args[6], args[2], args[1], args[0], args[3], args[4]);

		/**
		 * store the loaded data for easy access
		 */
		final Data data = new Data(loadData.year, loadData.month, loadData.date, loadData.drone, loadData.getSensors(),
				loadData.getCoordinates(), loadData.getSensorInfo(), loadData.getNoFlyZones());

		PathHelper pathHelper = new PathHelper(data);

		/**
		 * compute the finalPath
		 */
		var twoOpt = new TwoOpt(data.startRoute, pathHelper);
		List<Path> finalPath = twoOpt.findBestPath();

		/**
		 * create and write output files
		 */
		new WriteFiles(finalPath, data);
	}
}
