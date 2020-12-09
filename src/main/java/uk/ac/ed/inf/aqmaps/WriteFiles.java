package uk.ac.ed.inf.aqmaps;

import java.util.*;
import com.mapbox.geojson.Point;

import java.io.FileWriter; 
import java.io.IOException;

public class WriteFiles {

	public WriteFiles(List<Path> finalPath, String geoJson, LoadData load) throws IOException, InterruptedException {

		String flightPath = "";
		int count = 0;
		for (int i = 0; i < finalPath.size(); i++) {
			double startLong = finalPath.get(i).getStart().coordinates().get(0);
			double startLat = finalPath.get(i).getStart().coordinates().get(1);
			double endLong = finalPath.get(i).getEnd().coordinates().get(0);
			double endLat = finalPath.get(i).getEnd().coordinates().get(1);
			String sensor = "null";
			if (finalPath.get(i).getSensor() != null) {
				count++;
				Map<Point, String> pointToWhat3Words = load.getSensors();
				sensor = pointToWhat3Words.get(finalPath.get(i).getSensor());
			}
			flightPath += i+1 + "," + startLong + "," + startLat + "," + finalPath.get(i).getDegree() + "," + endLong
					+ "," + endLat + "," + sensor + "\n";
		}
		System.out.println(count);

		try {
			String pathFileName = "flightpath-" + load.date + "-" + load.month + "-" + load.year + ".txt";
			FileWriter myWriter = new FileWriter(pathFileName);
			myWriter.write(flightPath);
			myWriter.close();
			System.out.println("Successfully wrote to " + pathFileName);
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}

		try {
			String ReadingsFileName = "readings-" + load.date + "-" + load.month + "-" + load.year + ".geojson";
			FileWriter myWriter = new FileWriter(ReadingsFileName);
			myWriter.write(geoJson);
			myWriter.close();
			System.out.println("Successfully wrote to " + ReadingsFileName);
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

}
