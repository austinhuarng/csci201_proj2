package comparators;

import java.util.Comparator;

import models.Restaurant;

public class AlphabeticalComparator implements Comparator<Restaurant> {

	public int compare(Restaurant o1, Restaurant o2) {
		return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
	}
	
}
