package comparators;

import java.util.Comparator;

import models.Restaurant;

public class DistanceComparator implements Comparator<Restaurant> {

	public int compare(Restaurant o1, Restaurant o2) {
		return Double.compare(o1.getDistance(), o2.getDistance());
	}

}
