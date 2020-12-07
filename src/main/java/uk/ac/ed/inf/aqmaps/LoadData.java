package uk.ac.ed.inf.aqmaps;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mapbox.geojson.*;

public class LoadData {
	String port, year, month, date;
	String baseurl = "http://localhost:";
	
	public LoadData(String port, String year, String month, String date) {
		this.port = port;
		this.year = year;
		this.month = month;
		this.date = date;
	}

	private static final HttpClient client = HttpClient.newHttpClient();

	public Map<Point,String> getSensors() throws IOException, InterruptedException {

		var mapsRequest = HttpRequest.newBuilder()
				.uri(URI.create(baseurl + port + "/maps/" + year + "/" + month + "/" + date + "/air-quality-data.json"))
				.build();
		var mapsResponse = client.send(mapsRequest, BodyHandlers.ofString());
		Type mapType = new TypeToken<ArrayList<Maps>>() {
		}.getType();
		ArrayList<Maps> mapList = new Gson().fromJson(mapsResponse.body(), mapType);
		Map<Point,String> results = new HashMap<>();
		mapList.forEach((e) -> {
			String temp = e.location;
			var wordsRequest = HttpRequest.newBuilder()
					.uri(URI.create(baseurl + port + "/words/" + temp.replace(".", "/") + "/details.json")).build();
			try {
				var wordsResponse = client.send(wordsRequest, BodyHandlers.ofString());
				var details = new Gson().fromJson(wordsResponse.body(), Words.class);
				results.put(Point.fromLngLat(details.coordinates.lng, details.coordinates.lat),details.words);
			} catch (IOException | InterruptedException e1) {
				e1.printStackTrace();
			}
		});
		return results;
	}
	
	public List<Point> getCoordinates() throws IOException, InterruptedException {

		var mapsRequest = HttpRequest.newBuilder()
				.uri(URI.create(baseurl + port + "/maps/" + year + "/" + month + "/" + date + "/air-quality-data.json"))
				.build();
		var mapsResponse = client.send(mapsRequest, BodyHandlers.ofString());
		Type mapType = new TypeToken<ArrayList<Maps>>() {
		}.getType();
		ArrayList<Maps> mapList = new Gson().fromJson(mapsResponse.body(), mapType);
		List<Point> results = new ArrayList<>();
		mapList.forEach((e) -> {
			String temp = e.location;
			var wordsRequest = HttpRequest.newBuilder()
					.uri(URI.create(baseurl + port + "/words/" + temp.replace(".", "/") + "/details.json")).build();
			try {
				var wordsResponse = client.send(wordsRequest, BodyHandlers.ofString());
				var details = new Gson().fromJson(wordsResponse.body(), Words.class);
				results.add(Point.fromLngLat(details.coordinates.lng, details.coordinates.lat));
			} catch (IOException | InterruptedException e1) {
				e1.printStackTrace();
			}
		});
		return results;
	}

	public double[] getBatteries() throws IOException, InterruptedException {
		var mapsRequest = HttpRequest.newBuilder()
				.uri(URI.create(baseurl + port + "/maps/" + year + "/" + month + "/" + date + "/air-quality-data.json"))
				.build();
		var mapsResponse = client.send(mapsRequest, BodyHandlers.ofString());
		Type mapType = new TypeToken<ArrayList<Maps>>() {
		}.getType();
		ArrayList<Maps> mapList = new Gson().fromJson(mapsResponse.body(), mapType);
		double[] results = new double[mapList.size()];
		mapList.forEach((e) -> {
			results[mapList.indexOf(e)] = e.battery;
		});
		return results;
	}

	public String[] getReadings() throws IOException, InterruptedException {
		var mapsRequest = HttpRequest.newBuilder()
				.uri(URI.create(baseurl + port + "/maps/" + year + "/" + month + "/" + date + "/air-quality-data.json"))
				.build();
		var mapsResponse = client.send(mapsRequest, BodyHandlers.ofString());
		Type mapType = new TypeToken<ArrayList<Maps>>() {
		}.getType();
		ArrayList<Maps> mapList = new Gson().fromJson(mapsResponse.body(), mapType);
		String[] results = new String[mapList.size()];
		mapList.forEach((e) -> {
			results[mapList.indexOf(e)] = e.reading;
		});
		return results;
	}

	public Polygon[] getNoFlyZones() throws IOException, InterruptedException {
		var buildingsRequest = HttpRequest.newBuilder()
				.uri(URI.create(baseurl + port + "/buildings/no-fly-zones.geojson"))
				.build();
		var buildingsResponse = client.send(buildingsRequest, BodyHandlers.ofString());
		List<Feature> polygonList = FeatureCollection.fromJson(buildingsResponse.body()).features();
		Polygon[] results = new Polygon[polygonList.size()];
		polygonList.forEach((e) -> {
			results[polygonList.indexOf(e)] = (Polygon) e.geometry();
		});
		return results;
	}
}
