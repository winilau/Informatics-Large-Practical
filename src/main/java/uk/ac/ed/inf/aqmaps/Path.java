package uk.ac.ed.inf.aqmaps;

import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

/**
 * This class is used for storing the path of the drone; used in TwoOpt and
 * PathHelper
 */

public class Path {
	LineString path;
	Integer degree;
	Point sensor;

	/**
	 * Class constructor.
	 * 
	 * @param path   LineString of a single step
	 * @param degree the degree of that step as an Integer
	 * @param sensor Point of the corresponding sensor of the step if any
	 */
	public Path(LineString path, int degree, Point sensor) {
		this.path = path;
		this.degree = degree;
		this.sensor = sensor;
	}

	/**
	 * @return LineString of a single step
	 */
	public LineString getPath() {
		return path;
	}
	
	/**
	 * @return degree of a step in String
	 */
	public String getDegree() {
		return degree.toString();
	}

	/**
	 * @return return the starting Point of step
	 */
	public Point getStart() {
		return path.coordinates().get(0);
	}

	/**
	 * @return return the ending Point of step
	 */
	public Point getEnd() {
		return path.coordinates().get(1);
	}

	/**
	 * @return Point of the corresponding sensor of the step if any
	 */
	public Point getSensor() {
		return sensor;
	}

	/** 
	 * @param Point of the corresponding sensor of the step if any
	 */
	public void setSensor(Point sensor) {
		this.sensor = sensor;
	}
}
