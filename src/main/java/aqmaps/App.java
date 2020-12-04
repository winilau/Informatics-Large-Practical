package aqmaps;

import java.io.*;
import java.util.*;
import com.mapbox.geojson.*;

public class App {
	public static void main(String[] args) throws IOException, InterruptedException{
		var pathHelper = new PathHelper(args[6], args[2], args[1], args[0], args[3], args[4]);
		List<Path> finalPath = pathHelper.findPath();
	}
}
