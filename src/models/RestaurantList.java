package models;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import util.GsonDataParser;
import util.SimpleJsonDataParser;
import util.FieldNotFoundException;

import java.io.IOException;
import java.util.ArrayList;

public class RestaurantList {
    @SerializedName("data")
    private ArrayList<Restaurant> restaurants;

    public RestaurantList(String fileName) throws ParseException, FieldNotFoundException, IOException,
            ClassCastException {

        // Control which parser is used here
        // Just comment out the one you don't want
        //loadListFromSimpleJson(fileName);
        loadListFromGson(fileName);
    }

    /**
     /**
     * This function loads a given JSON file to the RestaurantList class via GSON
     *
     * @param fileName File name of JSON to load
     *
     * @throws IOException Indicates a missing file
     * @throws FieldNotFoundException Indicates a missing field
     */
    private void loadListFromGson(String fileName) throws IOException, FieldNotFoundException,
            JsonSyntaxException {
        this.restaurants = GsonDataParser.getRestaurantsFromJson(fileName);
    }

    /**
     * This function loads a given JSON file to the RestaurantList class via Simple.JSON
     * It gives more control than GSON since you can see specifically what errors were raised
     *
     * @param fileName File name of JSON to load
     *
     * @throws ParseException Indicates a malformed JSON
     * @throws FieldNotFoundException Indicates a missing field
     * @throws IOException Indicates a missing file
     * @throws ClassCastException Indicates an incorrect file typing
     */
    private void loadListFromSimpleJson(String fileName) throws ParseException, FieldNotFoundException, IOException,
            ClassCastException {
        this.restaurants = SimpleJsonDataParser.getRestaurantsFromJson(fileName);
    }

    public Restaurant getRestaurant(String name) {
        for (Restaurant restaurant: this.restaurants) {
            if (restaurant.getName().equalsIgnoreCase(name)) return restaurant;
        }
        return null;
    }

    public ArrayList<Restaurant> searchRestaurants(String name) {
        ArrayList<Restaurant> temp = new ArrayList<>();
        for (Restaurant restaurant: this.restaurants) {
            // To allow partial searches, we check that our query is in the restaurant name
            // To allow for case insensitivity, we make both strings lowercase
            if (restaurant.getName().toLowerCase().contains(name.toLowerCase())) temp.add(restaurant);
        }
        return temp;
    }

    public ArrayList<String> getRestaurantsWithItem(String item) {
    	
        ArrayList<String> results = new ArrayList<>();
        
        for (Restaurant r : this.restaurants) {
        	
        	boolean hasAtLeastOneItem = false;
        	
        	ArrayList<String> matchingItems = new ArrayList<String>();
        	
        	// Search for matching items
        	for (String menuItem : r.getMenu()) {
        		if(menuItem.toLowerCase().contains(item.toLowerCase())) {
        			hasAtLeastOneItem = true;
        			matchingItems.add(menuItem);
                }
        	}
        	
        	// Menu Output Formatting as follows
            // 1 Item : RestaurantName serves itemname.
            // 2 Items : RestaurantName serves itemname1 and itemname2.
            // 3 Items : RestaurantName serves itemname1, itemname2, and itemname3.
            // 4+ Items : RestaurantName serves itemname1, itemname2, ... , itemnameN-1, and itemnameN.
        	
        	if (hasAtLeastOneItem) {
        		
            	String menuString = r.getName() + " serves";
        		
        		if (matchingItems.size() > 1) {
                	
                	for (int i = 0; i < matchingItems.size() - 1; i++) {
                		menuString += " " + matchingItems.get(i) + ",";
                	}
                	
                	if (matchingItems.size() == 2) {
                		menuString = menuString.substring(0, menuString.length() - 1);
                	}
                	
                	menuString += " and " + matchingItems.get(matchingItems.size() - 1) + ".";
                	
                } else {
                	menuString = r.getName() + " serves " + matchingItems.get(0) + ".";
                }
        		
        		results.add(menuString);
        	}
        	
        }

        return results;
    }

    public ArrayList<Restaurant> getRestaurants() {
        return restaurants;
    }

    @SuppressWarnings("unchecked")
    public JSONObject toJson() {
        // Initialize new JSONObject
        JSONObject jsonObject = new JSONObject();

        // Add String array to JSONArray
        JSONArray jsonArray = new JSONArray();
        this.restaurants.forEach(o -> jsonArray.add(o.toJson()));

        // Insert components into jsonObject
        jsonObject.put("data", jsonArray);

        return jsonObject;
    }


}
