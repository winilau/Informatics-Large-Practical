package aqmaps;

import java.io.*;
import com.mapbox.geojson.*;

public class App {
	public static void main(String[] args) throws IOException, InterruptedException{
		var loadData = new LoadData(args[6], args[2], args[1], args[0]);
		Point[] points = loadData.getCoordinates();
		double[] batteriers = loadData.getBatteries();
		String[] readings = loadData.getReadings();
		Polygon[] noFlyZones = loadData.getNoFlyZones();		
	}
}
