package uk.ac.ed.inf.aqmaps;

import java.io.IOException;
import java.util.*;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;

import java.io.FileWriter; 

public class WriteFiles {

	public WriteFiles(List<Path> finalPath, Data data, PathHelper pathHelper) {
		Map<Point,String> sensors = data.sensors;
		Map<String,List<String>> sensorInfo = data.sensorsInfo;
		List<Point> sensorLocations = data.sensorLocations;

		List<Feature> fl = new ArrayList<>();
		String flightPath = "";
		
		//limit the moves to 150
		if(finalPath.size()>150) {
			System.out.println("did not finish in 150 moves");
			finalPath = finalPath.subList(0, 151);
		}
		
		List<Point> visitedSensors = new ArrayList<>();
		
		for (int i = 0; i < finalPath.size(); i++) {
			double startLong = finalPath.get(i).getStart().coordinates().get(0);
			double startLat = finalPath.get(i).getStart().coordinates().get(1);
			double endLong = finalPath.get(i).getEnd().coordinates().get(0);
			double endLat = finalPath.get(i).getEnd().coordinates().get(1);
			
			Feature line = Feature.fromGeometry(finalPath.get(i).getPath());
			fl.add(line);
			String what3word = "null";
			if (finalPath.get(i).getSensor() != null && sensorInfo.containsKey(sensors.get(finalPath.get(i).getSensor()))) {
				what3word = sensors.get(finalPath.get(i).getSensor());
				Feature point = Feature.fromGeometry(finalPath.get(i).getSensor());
				point.addStringProperty("location", what3word);
				point.addStringProperty("rbg-string",getStringProperty(sensorInfo.get(what3word)).get(0));
				point.addStringProperty("marker-color",getStringProperty(sensorInfo.get(what3word)).get(0));
				point.addStringProperty("marker-symbol", getStringProperty(sensorInfo.get(what3word)).get(1));
				
				fl.add(point);
				visitedSensors.add(finalPath.get(i).getSensor());
			}
			flightPath += i+1 + "," + startLong + "," + startLat + "," + finalPath.get(i).getDegree() + "," + endLong
					+ "," + endLat + "," + what3word + "\n";
		}
		
		//add the non-visited sensors
		if (!visitedSensors.containsAll(sensorLocations)) {
			System.out.println("did not reach all sensors");
			for (int i = 0; i < sensorLocations.size(); i++) {
				if (!visitedSensors.contains(sensorLocations.get(i))){
					Feature point = Feature.fromGeometry(sensorLocations.get(i));
					String what3word = sensors.get(finalPath.get(i).getSensor());
					point.addStringProperty("location", what3word);
					point.addStringProperty("rbg-string", "#aaaaaa");
					point.addStringProperty("marker-color", "#aaaaaa");
					fl.add(point);
				}
			}
		}
		
		//geoJson string of path and sensors as requested from the course work
		String geoJson = FeatureCollection.fromFeatures(fl).toJson();
		
		//create and write the output files
		writeToFiles(data, flightPath, geoJson);
		
		//print out the path with the noFlyZones for myself to see
		for (Polygon P: data.noFlyZones) {
			fl.add(Feature.fromGeometry(P));
		}
		System.out.println(FeatureCollection.fromFeatures(fl).toJson());
	}
	
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

	private List<String> getStringProperty(List<String> info) {
		String reading = info.get(0);
		String battery = info.get(1);
		
		List<String> results = new ArrayList<>();
		if (Double.parseDouble(battery) < 10) {
			results = Arrays.asList("#000000","cross");
			return results;
		}
		
		Double data = Double.parseDouble(reading);
		
		if(data >= 0 && data < 32) {
			results = Arrays.asList("#00ff00","lighthouse");
		}else if(data >= 32 && data < 64) {
			results = Arrays.asList("#40ff00","lighthouse");
		}else if(data >= 64 && data < 96) {
			results = Arrays.asList("#80ff00","lighthouse");
		}else if(data >=96 && data < 128) {
			results = Arrays.asList("#c0ff00","lighthouse");
		}else if(data >= 128 && data < 160) {
			results = Arrays.asList("#ffc000","danger");
		}else if(data >= 160 && data < 192) {
			results = Arrays.asList("#ff8000","danger");
		}else if(data >= 192 && data < 224) {
			results = Arrays.asList("#ff4000","danger");
		}else if(data >= 224 && data < 256) {
			results = Arrays.asList("#ff0000","danger");
		}
		
		return results;
	}

}
