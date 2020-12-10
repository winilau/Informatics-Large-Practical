package uk.ac.ed.inf.aqmaps;

import java.util.*;
import com.mapbox.geojson.Point;

public class TwoOpt {
	int size;
	List<Point> route;
	PathHelper pathHelper;
	
	public TwoOpt(List<Point> route, PathHelper pathHelper) {
		this.route = route;
		size = route.size();
		this.pathHelper = pathHelper;

	}

	public List<Path> findBestRoute() {
		int improve = 0;
		List<Point> bestRoute = route;
		while (improve < 2) {
			for (int i = 1; i < size - 1; i++) {
				for (int k = i + 1; k < size - 1; k++) {
					List<Point> newRoute = TwoOptSwap(i, k, bestRoute);
					// Since the only different part of the route is the reversed part, I only
					// compared the steps it takes to do the route from i-1 to k+1
					List<Path> reversed = pathHelper.findAllSteps(newRoute.subList(i - 1, k + 2));
					List<Path> original = pathHelper.findAllSteps(bestRoute.subList(i - 1, k + 2));

					if (reversed.size() < original.size()) {
						// Improvement found so reset
						improve = 0;
						bestRoute = newRoute;
					}
				}
			}
			improve++;
		}
		return pathHelper.findAllSteps(bestRoute);
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