package uk.ac.ed.inf.aqmaps;

import java.awt.geom.Line2D;
import java.util.*;

import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;

/**
 * This class is a helper class that gets all the steps it takes from one point
 * to another; Used in the class TwoOpt to obtain best path
 * 
 * @see TwoOpt
 */

public class PathHelper {
	/**
	 * a 2D array of the 36 options a point could take ordered in degrees in the
	 * increment of 10 (row 0 being degree 0, row 1 being degree 10 and so on); each
	 * row is one option and column 0 corresponds to x (i.e. longitude) where as
	 * column 1 corresponds to y (i.e. latitude).
	 */
	double[][] directions = new double[36][2];
	Polygon[] noFlyZones;
	Point drone;

	/**
	 * Class constructor.
	 * 
	 * @param data the information from the webserver and main arguments
	 */
	public PathHelper(Data data) {
		/**
		 * access the no fly zones and the starting location of drone from loaded data
		 */
		this.noFlyZones = data.noFlyZones;
		this.drone = data.drone;
		/**
		 * for each degree (36 since they are increments of 10) get the x and y from
		 * unit circle formula and multiply by the specified length 0.0003 then store
		 * the directions in corresponding columns
		 */
		for (int i = 0; i < 36; i++) {
			double angle = i * 10.0;
			this.directions[i][0] = 0.0003 * Math.cos(Math.toRadians(angle));
			this.directions[i][1] = 0.0003 * Math.sin(Math.toRadians(angle));
		}
	}

	/**
	 * Find the steps it takes to complete a route by looping through a route and
	 * using the findSteps method for each one apart from the start
	 * 
	 * @param route the route to find the steps for
	 * @return the steps to complete the route
	 * 
	 * @see findSteps
	 */
	public List<Path> findAllSteps(List<Point> route) {
		List<Path> results = new ArrayList<>();
		Point start = route.get(0);
		for (int i = 1; i < route.size(); i++) {
			List<Path> temp = findSteps(start, route.get(i));
			results.addAll(temp);
			/**
			 * set start of next one as the ending Point of current
			 */
			start = temp.get(temp.size() - 1).getEnd();

		}
		return results;
	}

	/**
	 * Find the steps it takes to get from one point to another
	 * 
	 * @param start starting point
	 * @param end   ending point
	 * @return the steps from start to end
	 * 
	 * @see findLength
	 * @see inNoFlyZone
	 */
	private List<Path> findSteps(Point start, Point end) {
		List<Path> results = new ArrayList<Path>();
		/**
		 * used to keep track of best option; initialise best as the first option at 0
		 * degree
		 */
		Point best = Point.fromLngLat(start.longitude() + directions[0][0], start.latitude() + directions[0][1]);
		/**
		 * the corresponding degree of a step; initialise at 0 corresponding with best
		 */
		int degree = 0;
		/**
		 * used to keep track of current position; initialise current as the start
		 */
		Point current = start;

		/**
		 * check if the first option already satisfies requirement (i.e. close enough to
		 * end point and not in no fly zones) and if true return.
		 */
		if (findLength(best, end) < 0.0002 && !inNoFlyZone(start, best)) {
			List<Point> points = Arrays.asList(start, best);
			/**
			 * make a new path with the LineString from start to best and degree as 0 and
			 * sensor as end (because this only applies if the first option is already close
			 * enough to the end Point which is the sensor)
			 */
			Path temp = new Path(LineString.fromLngLats(points), degree, end);
			results.add(temp);
			return results;
		}

		List<LineString> visited = new ArrayList<>();

		/**
		 * if the first option does not satisfy the requirement then check all options
		 * and find the closest one to the end point and repeat this process until the
		 * end is within 0.0002 distance and the LineString is not in any of the no fly
		 * zones
		 */
		while ((findLength(best, end) >= 0.0002 || inNoFlyZone(current, best)) && visited.size() < 50) {
			List<Integer> degrees = new ArrayList<>();
			List<Point> valid = new ArrayList<>();
			/**
			 * find all the options that are not in the no fly zones and store them in valid
			 * at the same time store the corresponding degree in degrees
			 */
			for (int i = 0; i < directions.length; i++) {
				Point currentOption = Point.fromLngLat(current.longitude() + directions[i][0],
						current.latitude() + directions[i][1]);
				if (!inNoFlyZone(current, currentOption)) {
					valid.add(currentOption);
					/**
					 * degree is just i * 10 because they are in increments of 10
					 */
					degrees.add(i * 10);
				}
			}

			/**
			 * loop through valid and find the closest one to the end by setting best as the
			 * first value of valid and loop through the rest of valid to compare and if an
			 * option is closer to end and if so set best to the new option
			 */
			best = valid.get(0);
			for (int i = 1; i < valid.size(); i++) {
				Point currentOption = valid.get(i);
				List<Point> points = Arrays.asList(current, currentOption);
				double currentDistance = findLength(currentOption, end);
				/**
				 * make sure the LineString isn't already visited
				 */
				if (currentDistance < findLength(best, end) && !visited.contains(LineString.fromLngLats(points))) {
					best = currentOption;
					/**
					 * set degree as the corresponding degree in degrees
					 */
					degree = degrees.get(i);
				}
			}

			/**
			 * check if a sensor is within reach, if so set sensor to end if not leave as
			 * null
			 */
			Point sensor = null;
			if (findLength(best, end) < 0.0002 && end != drone) {
				sensor = end;
			}
			List<Point> points = Arrays.asList(current, best);
			/**
			 * add a path with LineString from current to best, corresponding degree, and
			 * the sensor if any null otherwise
			 */
			Path temp = new Path(LineString.fromLngLats(points), degree, sensor);
			/**
			 * add LineString to visited
			 */
			results.add(temp);
			visited.add(LineString.fromLngLats(points));
			/**
			 * set current to best and the loop will continue with current at the new
			 * location if best doesn't reach a sensor
			 */
			current = best;
		}
		return results;
	}

	/**
	 * Find the euclidean distance of two points
	 * 
	 * @param a point a
	 * @param b point b
	 * @return the euclidean distance of a and b
	 */
	private double findLength(Point a, Point b) {
		double x = Math.abs(a.longitude() - b.longitude());
		double y = Math.abs(a.latitude() - b.latitude());
		return Math.hypot(x, y);
	}

	/**
	 * See if a line is in the no fly zones including the outer boundary
	 * 
	 * @param a first point of line
	 * @param b second point of line
	 * @return true if a line is in the no fly zones including the outer boundary
	 *         false otherwise
	 */
	private Boolean inNoFlyZone(Point a, Point b) {
		List<LineString> noFlyStrings = new ArrayList<>();
		/**
		 * convert the noFlyZones polygons into a the list of LineStrings that
		 * represents their outer lines
		 */
		for (Polygon z : noFlyZones) {
			for (int i = 0; i < z.coordinates().get(0).size() - 1; i++) {
				List<Point> temp = new ArrayList<>();
				temp.add(z.coordinates().get(0).get(i));
				temp.add(z.coordinates().get(0).get(i + 1));
				noFlyStrings.add(LineString.fromLngLats(temp));
			}
		}

		/**
		 * this is the outer square of where the drone should not exceed. Since the
		 * concept is similar to the no fly zones I decided to implement it here.
		 */
		List<Point> outer = Arrays.asList(Point.fromLngLat(-3.192473, 55.946233),
				Point.fromLngLat(-3.184319, 55.946233), Point.fromLngLat(-3.184319, 55.942617),
				Point.fromLngLat(-3.192473, 55.942617));

		/**
		 * add the outer boundaries into noFlyStrings
		 */
		noFlyStrings.add(LineString.fromLngLats(outer.subList(0, 2)));
		noFlyStrings.add(LineString.fromLngLats(outer.subList(1, 3)));
		noFlyStrings.add(LineString.fromLngLats(outer.subList(2, 4)));
		noFlyStrings.add(LineString.fromLngLats(Arrays.asList(outer.get(3), outer.get(0))));

		double x1 = a.longitude();
		double y1 = a.latitude();
		double x2 = b.longitude();
		double y2 = b.latitude();

		/**
		 * loop through noFlyStrings and check if the line intersect with any, and if
		 * any returns true
		 */
		for (int i = 0; i < noFlyStrings.size(); i++) {
			double x3 = noFlyStrings.get(i).coordinates().get(0).longitude();
			double y3 = noFlyStrings.get(i).coordinates().get(0).latitude();
			double x4 = noFlyStrings.get(i).coordinates().get(1).longitude();
			double y4 = noFlyStrings.get(i).coordinates().get(1).latitude();

			if (Line2D.linesIntersect(x1, y1, x2, y2, x3, y3, x4, y4)) {
				return true;
			}
		}
		/**
		 * returns false because none of the line intersects with the noFlyStrings
		 */
		return false;
	}

}