package aqmaps;

import java.io.*;
import java.util.*;
import com.mapbox.geojson.*;

public class App {
	public static void main(String[] args) throws IOException, InterruptedException{
		var path = new Path(args[6], args[2], args[1], args[0], args[3], args[4]);
		List<LineString> finalPath = path.findPath();
	}
}
