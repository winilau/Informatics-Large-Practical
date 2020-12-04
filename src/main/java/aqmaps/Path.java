package aqmaps;

import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

public class Path {
	LineString path;
	Double degree;
	Point sensor;
	
	public Path(LineString path, Double degree, Point sensor) {
		this.path = path;
		this.degree = degree;
		this.sensor = sensor;
	}

	public LineString getPath() {
		return path;
	}

	public String getDegree() {
		return degree.toString();
	}
	
	public Point getStart() {
		return path.coordinates().get(0);
	}
	
	public Point getEnd() {
		return path.coordinates().get(1);
	}
	
	public Point getSensor() {
		return sensor;
	}
}
