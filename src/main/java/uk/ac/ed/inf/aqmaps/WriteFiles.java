package uk.ac.ed.inf.aqmaps;

import java.io.IOException;
import java.util.*;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;

import java.io.FileWriter;

/**
 * This class is used for creating and writing the geojson and txt output files
 * from the output of the algorithm
 */

public class WriteFiles {

	/**
	 * Class constructor; compute the content needed for the two output files and
	 * create/write them
	 * 
	 * @param finalPath the final path obtained from TwoOpt
	 * @param data      get data stored at the start
	 * 
	 * @see writeToFiles
	 * @see getStringProperty
	 */
	public WriteFiles(List<Path> finalPath, Data data) {
		/**
		 * get sensors as Point and what3word from loaded data
		 */
		Map<Point, String> sensors = data.sensors;
		/**
		 * sensors as what3words and corresponding reading and battery information
		 */
		Map<String, List<String>> sensorInfo = data.sensorsInfo;
		List<Point> sensorLocations = data.sensorLocations;
		/**
		 * List of features to be converted into geoJson for output
		 */
		List<Feature> fl = new ArrayList<>();
		/**
		 * txt string of flight to be written into the file
		 */
		String flightPath = "";

		/**
		 * limit the moves to 150
		 */
		if (finalPath.size() > 150) {
			System.out.println("did not finish in 150 moves");
			finalPath = finalPath.subList(0, 151);
		}

		/**
		 * keep track of all the visited sensors to see if all is reached
		 */
		List<Point> visitedSensors = new ArrayList<>();

		/**
		 * for each path add the corresponding path in flightPath and LineString and
		 * Point (if any sensor) to fl for output
		 */
		for (int i = 0; i < finalPath.size(); i++) {
			double startLong = finalPath.get(i).getStart().coordinates().get(0);
			double startLat = finalPath.get(i).getStart().coordinates().get(1);
			double endLong = finalPath.get(i).getEnd().coordinates().get(0);
			double endLat = finalPath.get(i).getEnd().coordinates().get(1);

			/**
			 * add current LineString from path to fl
			 */
			Feature line = Feature.fromGeometry(finalPath.get(i).getPath());
			fl.add(line);
			/**
			 * for the string property "location"; initialise as the string null and if the
			 * current path has a sensor then set to corresponding sensor
			 */
			String what3word = "null";
			/**
			 * get the string property for rbg-string, marker-color, and marker-symbol by
			 * calling the getStringProperty method
			 */
			if (finalPath.get(i).getSensor() != null
					&& sensorInfo.containsKey(sensors.get(finalPath.get(i).getSensor()))) {
				/**
				 * get what3word from getting the value of sensors with the Point from current
				 * Path
				 */
				what3word = sensors.get(finalPath.get(i).getSensor());
				Feature point = Feature.fromGeometry(finalPath.get(i).getSensor());
				point.addStringProperty("location", what3word);
				/**
				 * get reading and battery from getting the value of sensorInfo with the
				 * what3word obtained just now
				 */
				point.addStringProperty("rbg-string", getStringProperty(sensorInfo.get(what3word)).get(0));
				point.addStringProperty("marker-color", getStringProperty(sensorInfo.get(what3word)).get(0));
				point.addStringProperty("marker-symbol", getStringProperty(sensorInfo.get(what3word)).get(1));

				/**
				 * add the point with added string properties to fl
				 */
				fl.add(point);
				/**
				 * add sensor to the visited sensor
				 */
				visitedSensors.add(finalPath.get(i).getSensor());
			}
			/**
			 * append the line representing the current path to flightPath
			 */
			flightPath += i + 1 + "," + startLong + "," + startLat + "," + finalPath.get(i).getDegree() + "," + endLong
					+ "," + endLat + "," + what3word + "\n";
		}

		/**
		 * add the non-visited sensors if any
		 */
		if (!visitedSensors.containsAll(sensorLocations)) {
			System.out.println("did not reach all sensors");
			for (int i = 0; i < sensorLocations.size(); i++) {
				if (!visitedSensors.contains(sensorLocations.get(i))) {
					Feature point = Feature.fromGeometry(sensorLocations.get(i));
					String what3word = sensors.get(finalPath.get(i).getSensor());
					point.addStringProperty("location", what3word);
					point.addStringProperty("rbg-string", "#aaaaaa");
					point.addStringProperty("marker-color", "#aaaaaa");
					fl.add(point);
				}
			}
		}

		/**
		 * convert fl into a geoJson string of path and sensors to be written into the file
		 */
		String geoJson = FeatureCollection.fromFeatures(fl).toJson();

		/**
		 * create and write the output files from the comupted content 
		 */
		writeToFiles(data, flightPath, geoJson);

		/**
		 * print out the path with the noFlyZones for testing
		 */
		for (Polygon P : data.noFlyZones) {
			fl.add(Feature.fromGeometry(P));
		}
		System.out.println(FeatureCollection.fromFeatures(fl).toJson());
	}

	/**
	 * create and write the two output files
	 * 
	 * @param data       get data stored at the start to access date, month, year
	 *                   for filename
	 * @param flightPath String to write to the txt file that represents the
	 *                   flightPath
	 * @param geoJson    geoJson string that represents the geoJson of path and
	 *                   sensor with corresponding properties
	 * 
	 * @throws IOException If an input or output exception occurred
	 */
	private void writeToFiles(Data data, String flightPath, String geoJson) {
		try {
			String pathFileName = "flightpath-" + data.date + "-" + data.month + "-" + data.year + ".txt";
			FileWriter myWriter = new FileWriter(pathFileName);
			myWriter.write(flightPath);
			myWriter.close();
			System.out.println("Successfully wrote to " + pathFileName);
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}

		try {
			String ReadingsFileName = "readings-" + data.date + "-" + data.month + "-" + data.year + ".geojson";
			FileWriter myWriter = new FileWriter(ReadingsFileName);
			myWriter.write(geoJson);
			myWriter.close();
			System.out.println("Successfully wrote to " + ReadingsFileName);
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	/**
	 * check the reading and the battery to get the corresponding string properties
	 * of marker symbol and colour
	 * 
	 * @param sensorInfo
	 * @return corresponding string properties of marker symbol and colour
	 */
	private List<String> getStringProperty(List<String> sensorInfo) {
		String reading = sensorInfo.get(0);
		String battery = sensorInfo.get(1);

		/**
		 * check if battery is low
		 */
		List<String> results = new ArrayList<>();
		if (Double.parseDouble(battery) < 10) {
			results = Arrays.asList("#000000", "cross");
			return results;
		}

		/**
		 * check to see which range the reading is in
		 */
		Double data = Double.parseDouble(reading);
		if (data >= 0 && data < 32) {
			results = Arrays.asList("#00ff00", "lighthouse");
		} else if (data >= 32 && data < 64) {
			results = Arrays.asList("#40ff00", "lighthouse");
		} else if (data >= 64 && data < 96) {
			results = Arrays.asList("#80ff00", "lighthouse");
		} else if (data >= 96 && data < 128) {
			results = Arrays.asList("#c0ff00", "lighthouse");
		} else if (data >= 128 && data < 160) {
			results = Arrays.asList("#ffc000", "danger");
		} else if (data >= 160 && data < 192) {
			results = Arrays.asList("#ff8000", "danger");
		} else if (data >= 192 && data < 224) {
			results = Arrays.asList("#ff4000", "danger");
		} else if (data >= 224 && data < 256) {
			results = Arrays.asList("#ff0000", "danger");
		}

		return results;
	}

}
