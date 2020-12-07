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
	Map<String,List<String>> sensorInfo;

	public FeatureHelper(List<Path> finalPath, Map<Point,String> sensors,
			Polygon[] noFlyZones, Map<String,List<String>> sensorInfo) {
		this.finalPath = finalPath;
		this.sensors = sensors;
		this.noFlyZones = noFlyZones;
		this.sensorInfo = sensorInfo;
	}

	public String getFeatureCollection() {
		var pathHelper = new PathHelper(noFlyZones);
		List<Feature> fl = new ArrayList<>();
		
		//limit the moves to 150
		if(finalPath.size()>150) {
			finalPath = finalPath.subList(0, 151);
		}
		
		List<LineString> path = pathHelper.getAllPaths(finalPath);
		for (int i = 0; i < finalPath.size(); i++) {
			Feature line = Feature.fromGeometry(path.get(i));
			fl.add(line);
			if (finalPath.get(i).getSensor() != null) {
				Feature point = Feature.fromGeometry(finalPath.get(i).getSensor());
				String what3word = sensors.get(finalPath.get(i).getSensor());
				point.addStringProperty("location", what3word);
				point.addStringProperty("rbg-string",getStringProperty(sensorInfo.get(what3word)).get(0));
				point.addStringProperty("marker-color",getStringProperty(sensorInfo.get(what3word)).get(0));
				point.addStringProperty("marker-symbol", getStringProperty(sensorInfo.get(what3word)).get(1));
				
				fl.add(point);
			}
		}

		System.out.println(pathHelper.getAllPaths(finalPath).size());
		System.out.println(FeatureCollection.fromFeatures(fl).toJson());
		return FeatureCollection.fromFeatures(fl).toJson();
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
