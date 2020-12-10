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

/**
 * This class is used to load the information from the webserver.
 */

public class LoadData {
	String port, year, month, date;
	String baseurl = "http://localhost:";
	Point drone;

	/**
	 * Class constructor taking values from the main arguments.
	 * 
	 * @param port  the port in which the webserver is running
	 * @param year  year of the air quality data; used with month and date
	 * @param month month of the air quality data; used with year and date
	 * @param date  date of the air quality data; used with month and year
	 * @param y     latitutude of starting location
	 * @param x     longitutude of starting location
	 */
	public LoadData(String port, String year, String month, String date, String y, String x) {
		this.port = port;
		this.year = year;
		this.month = month;
		this.date = date;
		/**
		 * store starting location as a point.
		 */
		this.drone = Point.fromLngLat(Double.valueOf(x), Double.valueOf(y));
	}

	private static final HttpClient client = HttpClient.newHttpClient();

	/**
	 * obtain information from the maps and words folder in webserver of
	 * corresponding date/month/year.
	 * 
	 * @return Location of sensors as a Map of Point to what3words(string).
	 * 
	 * @throws IOException          If an input or output exception occurred
	 * @throws InterruptedException If an occupied thread is interrupted
	 */
	public Map<Point, String> getSensors() throws IOException, InterruptedException {

		var mapsRequest = HttpRequest.newBuilder()
				.uri(URI.create(baseurl + port + "/maps/" + year + "/" + month + "/" + date + "/air-quality-data.json"))
				.build();
		var mapsResponse = client.send(mapsRequest, BodyHandlers.ofString());
		/**
		 * convert the json file in response to the Maps class to access the sensors
		 * 
		 * @see Maps
		 */
		Type mapType = new TypeToken<ArrayList<Maps>>() {
		}.getType();
		ArrayList<Maps> mapList = new Gson().fromJson(mapsResponse.body(), mapType);
		Map<Point, String> results = new HashMap<>();
		/**
		 * using the obtained locations in what3words from the Maps folder to get the
		 * sensor locations as Points
		 */
		mapList.forEach((e) -> {
			/**
			 * sensor location as what3words
			 */
			String location = e.location;
			var wordsRequest = HttpRequest.newBuilder()
					.uri(URI.create(baseurl + port + "/words/" + location.replace(".", "/") + "/details.json")).build();
			try {
				/**
				 * covert the response to the Words class to access the coordinates
				 * 
				 * @see Words
				 */
				var wordsResponse = client.send(wordsRequest, BodyHandlers.ofString());
				var details = new Gson().fromJson(wordsResponse.body(), Words.class);
				/**
				 * stores both the coordinates as points and the what3words as string in a map
				 * and return
				 */
				results.put(Point.fromLngLat(details.coordinates.lng, details.coordinates.lat), details.words);
			} catch (IOException | InterruptedException e1) {
				e1.printStackTrace();
			}
		});
		return results;
	}

	/**
	 * obtain information from the maps and words folder in webserver of
	 * corresponding date/month/year.
	 * 
	 * @return Location of sensors as a list of Point.
	 * 
	 * @throws IOException          If an input or output exception occurred
	 * @throws InterruptedException If an occupied thread is interrupted
	 */
	public List<Point> getCoordinates() throws IOException, InterruptedException {

		var mapsRequest = HttpRequest.newBuilder()
				.uri(URI.create(baseurl + port + "/maps/" + year + "/" + month + "/" + date + "/air-quality-data.json"))
				.build();
		var mapsResponse = client.send(mapsRequest, BodyHandlers.ofString());
		/**
		 * convert the json file in response to the Maps class to access the sensors
		 * 
		 * @see Maps
		 */
		Type mapType = new TypeToken<ArrayList<Maps>>() {
		}.getType();
		ArrayList<Maps> mapList = new Gson().fromJson(mapsResponse.body(), mapType);
		List<Point> results = new ArrayList<>();
		/**
		 * using the obtained locations in what3words from the Maps folder to get the
		 * sensor locations as Points
		 */
		mapList.forEach((e) -> {
			String location = e.location;
			var wordsRequest = HttpRequest.newBuilder()
					.uri(URI.create(baseurl + port + "/words/" + location.replace(".", "/") + "/details.json")).build();
			try {
				/**
				 * covert the response to the Words class to access the coordinates
				 * 
				 * @see Words
				 */
				var wordsResponse = client.send(wordsRequest, BodyHandlers.ofString());
				var details = new Gson().fromJson(wordsResponse.body(), Words.class);
				/**
				 * stores the coordinates as points in a list and return
				 */
				results.add(Point.fromLngLat(details.coordinates.lng, details.coordinates.lat));
			} catch (IOException | InterruptedException e1) {
				e1.printStackTrace();
			}
		});
		return results;
	}

	/**
	 * obtain information from the maps folder in webserver of corresponding
	 * date/month/year.
	 * 
	 * @return a map of the sensor as what3words to the air quality reading and the
	 *         battery in this order for each sensor.
	 * 
	 * @throws IOException          If an input or output exception occurred
	 * @throws InterruptedException If an occupied thread is interrupted
	 */
	public Map<String, List<String>> getSensorInfo() throws IOException, InterruptedException {
		var mapsRequest = HttpRequest.newBuilder()
				.uri(URI.create(baseurl + port + "/maps/" + year + "/" + month + "/" + date + "/air-quality-data.json"))
				.build();
		/**
		 * convert the json file in response to the Maps class to access the sensors
		 * 
		 * @see Maps
		 */
		var mapsResponse = client.send(mapsRequest, BodyHandlers.ofString());
		Type mapType = new TypeToken<ArrayList<Maps>>() {
		}.getType();
		ArrayList<Maps> mapList = new Gson().fromJson(mapsResponse.body(), mapType);
		/**
		 * stores the what3words and the readings and batteries for each sensor in a map
		 * and return
		 */
		Map<String, List<String>> results = new HashMap<>();
		mapList.forEach((e) -> {
			results.put(e.location, Arrays.asList(e.reading, e.battery));
		});
		return results;
	}

	/**
	 * obtain information from the buildings folder in webserver
	 * 
	 * @return the no fly zones as an array of Polygons
	 * 
	 * @throws IOException          If an input or output exception occurred
	 * @throws InterruptedException If an occupied thread is interrupted
	 */
	public Polygon[] getNoFlyZones() throws IOException, InterruptedException {
		var buildingsRequest = HttpRequest.newBuilder()
				.uri(URI.create(baseurl + port + "/buildings/no-fly-zones.geojson")).build();
		var buildingsResponse = client.send(buildingsRequest, BodyHandlers.ofString());
		/**
		 * convert the geojson file in response to the an array of Polygons and return
		 */
		List<Feature> polygonList = FeatureCollection.fromJson(buildingsResponse.body()).features();
		Polygon[] results = new Polygon[polygonList.size()];
		polygonList.forEach((e) -> {
			results[polygonList.indexOf(e)] = (Polygon) e.geometry();
		});
		return results;
	}
}
