package uk.ac.ed.inf.aqmaps;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;

/**
 * This class is used for storing the data I get from the webserver and the
 * information from the arguments.
 */

public class Data {
	String year, month, date;
	Point drone;
	Map<Point, String> sensors;
	List<Point> sensorLocations;
	Map<String, List<String>> sensorsInfo;
	Polygon[] noFlyZones;
	/**
	 * Represents the first route my 2opt algorithm is going to take.
	 * 
	 * @see TwoOpt
	 */
	List<Point> startRoute = new ArrayList<>();

	/**
	 * Class constructor.
	 * 
	 * @param year            year of the air quality data; used with month and date
	 * @param month           month of the air quality data; used with year and date
	 * @param date            date of the air quality data; used with month and year
	 * @param drone           starting location of drone
	 * @param sensors         the location of sensors as a Map of Points to
	 *                        what3words.
	 * @param sensorLocations the location of sensors only as Points
	 * @param sensorInfo      a map of the sensor as what3words to the air quality
	 *                        reading and the battery in this order for each sensor.
	 * @param noFlyZones      no fly zones
	 */
	public Data(String year, String month, String date, Point drone, Map<Point, String> sensors,
			List<Point> sensorLocations, Map<String, List<String>> sensorInfo, Polygon[] noFlyZones) {

		this.year = year;
		this.month = month;
		this.date = date;
		this.drone = drone;
		this.sensors = sensors;
		this.sensorLocations = sensorLocations;
		this.sensorsInfo = sensorInfo;
		this.noFlyZones = noFlyZones;

		/**
		 * creating the first route by adding a the location of drone in before and
		 * after the list of sensor locations.
		 */
		startRoute.add(drone);
		startRoute.addAll(sensorLocations);
		startRoute.add(drone);
	}
}
