package uk.ac.ed.inf.aqmaps;

import java.util.*;
import com.mapbox.geojson.Point;

/**
 * This class is used to find the final path of using the 2 opt heuristic; uses
 * helper class PathHeler
 * 
 * @see PathHelper
 */

public class TwoOpt {
	int size;
	List<Point> route;
	PathHelper pathHelper;

	/**
	 * Class constructor
	 * 
	 * @param route      initial route
	 * @param pathHelper pathHelper class to access helper method
	 */
	public TwoOpt(List<Point> route, PathHelper pathHelper) {
		this.route = route;
		this.pathHelper = pathHelper;
		/**
		 * set size as size of route
		 */
		size = route.size();

	}

	/**
	 * using the 2 opt algorithm to find best path: set the best route to the
	 * initial route. loop through all possible i to k within size and them and
	 * compare the if the swapped one has lower number of steps then the best. if so
	 * set best to new route. repeat this process until improve == 2 is reached.
	 * 
	 * @return best path obtained through the 2 opt algorithm
	 * 
	 * @see TwoOptSwap(int, int, List<Point>)
	 * @see PathHelper#findAllSteps(List<Point>)
	 */
	public List<Path> findBestPath() {
		int improve = 0;
		List<Point> bestRoute = route;
		while (improve < 2) {
			for (int i = 1; i < size - 1; i++) {
				for (int k = i + 1; k < size - 1; k++) {
					/**
					 * use the twoOptSwap method to get new route
					 */
					List<Point> newRoute = TwoOptSwap(i, k, bestRoute);
					/**
					 * Since the only different part of the route is the reversed part, only
					 * compared the steps it takes to do the route from i-1 to k+1. Obtain the path
					 * from calling findAllSteps in pathHelper
					 */
					List<Path> reversed = pathHelper.findAllSteps(newRoute.subList(i - 1, k + 2));
					List<Path> original = pathHelper.findAllSteps(bestRoute.subList(i - 1, k + 2));

					if (reversed.size() < original.size()) {
						/**
						 * Improvement found so reset
						 */
						improve = 0;
						bestRoute = newRoute;
					}
				}
			}
			/**
			 * increase improve by one
			 */
			improve++;
		}
		/**
		 * return path of bestRoute
		 */
		return pathHelper.findAllSteps(bestRoute);
	}

	/**
	 * swap the route from i to k while leaving anything else untouched and return
	 * new route
	 * 
	 * @param i     start of swap
	 * @param k     end of swap
	 * @param route route to swap
	 * @return swapped route
	 */
	private List<Point> TwoOptSwap(int i, int k, List<Point> route) {
		/**
		 * take route[0] to route[i-1] and add them in order to new_route
		 */
		List<Point> result = new ArrayList<>();
		result.addAll(route.subList(0, i));

		/**
		 * take route[i] to route[k] and add them in reverse order to new_route
		 */
		List<Point> temp = new ArrayList<>(route.subList(i, k + 1));
		Collections.reverse(temp);
		result.addAll(temp);

		/**
		 * take route[k+1] to end and add them in order to new_route
		 */
		result.addAll(route.subList(k + 1, size));
		return result;
	}
}