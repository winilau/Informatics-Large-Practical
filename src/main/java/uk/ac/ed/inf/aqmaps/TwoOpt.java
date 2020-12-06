package uk.ac.ed.inf.aqmaps;

import java.util.*;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;

public class TwoOpt {
	int size;
	List<Point> route;

	public TwoOpt(List<Point> route) {
		this.route = route;
		size = route.size();

	}

	public List<Path> algorithm(Polygon[] noFlyZones) {
		var pathHelper = new PathHelper(noFlyZones);
		int improve = 0;

		List<Point> bestRoute = route;
		List<Path> bestPath = pathHelper.findAllSteps(route);
		while (improve < 2) {
			for (int i = 1; i < size - 1; i++) {
				for (int k = i + 1; k < size; k++) {
					List<Point> newRoute = TwoOptSwap(i, k, bestRoute);
					List<Path> newPath = pathHelper.findAllSteps(newRoute);

					if (newPath.size() < bestPath.size()) {
						// Improvement found so reset
						improve = 0;
						bestRoute = newRoute;
						bestPath = newPath;
					}
				}
			}
			improve++;
		}
		return bestPath;
	}

	private List<Point> TwoOptSwap(int i, int k, List<Point> route) {
		// 1. take route[0] to route[i-1] and add them in order to new_route
		List<Point> result = new ArrayList<>();
		result.addAll(route.subList(0, i));

		// 2. take route[i] to route[k] and add them in reverse order to new_route
		List<Point> temp = new ArrayList<>(route.subList(i, k + 1));
		Collections.reverse(temp);
		result.addAll(temp);

		// 3. take route[k+1] to end and add them in order to new_route
		result.addAll(route.subList(k + 1, size));
		return result;
	}
}