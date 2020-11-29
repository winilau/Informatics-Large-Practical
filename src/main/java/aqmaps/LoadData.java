package aqmaps;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;

public class LoadData {
	String port;
	String year;
	String month;
	String date;

	public LoadData(String port, String year, String month, String date) {
		this.port = port;
		this.year = year;
		this.month = month;
		this.date = date;
	}

	private static final HttpClient client = HttpClient.newHttpClient();

	public Point[] getCoordinates()
			throws IOException, InterruptedException {
		String baseurl = "http://localhost:";
		var mapsRequest = HttpRequest.newBuilder()
				.uri(URI.create(baseurl + port + "/maps/" + year + "/" + month + "/" + date + "/air-quality-data.json"))
				.build();
		var mapsResponse = client.send(mapsRequest, BodyHandlers.ofString());
		Type mapType = new TypeToken<ArrayList<Maps>>() {
		}.getType();
		ArrayList<Maps> mapList = new Gson().fromJson(mapsResponse.body(), mapType);
		Point[] results = new Point[mapList.size()];
		mapList.forEach((e) -> {
			String temp = e.location;
			var wordsRequest = HttpRequest.newBuilder()
					.uri(URI.create(baseurl + port + "/words/" + temp.replace(".", "/") + "/details.json")).build();
			try {
				var wordsResponse = client.send(wordsRequest, BodyHandlers.ofString());
				var details = new Gson().fromJson(wordsResponse.body(), Details.class);
				results[mapList.indexOf(e)] = Point.fromLngLat(details.coordinates.lng, details.coordinates.lat);
			} catch (IOException | InterruptedException e1) {
				e1.printStackTrace();
			}
		});
		return results;
	}
	
	public double[] getBatteries() throws IOException, InterruptedException {
		return null;
	}
	
	public String[] getReadings() throws IOException, InterruptedException {
		return null;
	}
	
	public Polygon[] getNoFlyZones() throws IOException, InterruptedException {
		return null;
	}
	
	/*
	 * public points getCoordinates(){ var mapsRequest = HttpRequest.newBuilder()
	 * .uri(URI.create(baseurl + port + "/maps/"+ args[2]+"/" + args[1] +"/"+
	 * args[0]+"/air-quality-data.json")) .build(); var mapsResponse =
	 * client.send(mapsRequest, BodyHandlers.ofString()); var buildingsRequest =
	 * HttpRequest.newBuilder() .uri(URI.create(baseurl + port + "/buildings/"+
	 * "no-fly-zones.geojson")) .build(); var buildingsResponse =
	 * client.send(buildingsRequest, BodyHandlers.ofString());
	 * 
	 * Type listType = new TypeToken<ArrayList<Maps>>() {}.getType();
	 * ArrayList<Maps> mapList = new Gson().fromJson(mapsResponse.body(), listType);
	 * mapList.forEach((e)->{ String temp = e.location; var wordsRequest =
	 * HttpRequest.newBuilder() .uri(URI.create(baseurl + port + "/words/"+
	 * temp.replace(".", "/") + "/details.json")) .build(); try { var wordsResponse
	 * = client.send(wordsRequest, BodyHandlers.ofString()); } catch (IOException |
	 * InterruptedException e1) { e1.printStackTrace(); } Type type = new
	 * TypeToken<ArrayList<Maps>>() {}.getType(); ArrayList<Maps> mapList = new
	 * Gson().fromJson(mapsResponse.body(), listType); }); }
	 */

}
